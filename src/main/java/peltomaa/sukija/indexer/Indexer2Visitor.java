/*
Copyright (©) 2025 Hannu Väisänen

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package peltomaa.sukija.indexer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.FileCommandDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import peltomaa.sukija.schema.IndexerConfigurationType;
import peltomaa.sukija.schema.ObjectFactory;
import peltomaa.sukija.schema.OnErrorType;



public class Indexer2Visitor extends SimpleFileVisitor<Path> {
  Indexer2Visitor (IndexerConfigurationType configuration)
    throws IOException, SAXException, TikaException
  {
    this.configuration = configuration;
    writeLimit = configuration.getWriteLimit().intValueExact();
    commitWithinMs = configuration.getCommitWithinMs().intValueExact();
    abortOnError = getAbortOnError();
    recursive = configuration.isRecursive();

    if (configuration.getExcludeSuffix().size() > 0) {
      excludes = Pattern.compile ("(?u)(?i).*[.](" + String.join ("|", configuration.getExcludeSuffix()) + ")$");
    }
    else {
      excludes = Pattern.compile (configuration.getExcludes());
    }

    if (configuration.getTika().length() > 0) {
      tikaConfig = new TikaConfig (configuration.getTika());
    }
    else {
      tikaConfig = TikaConfig.getDefaultConfig();
    }

    client = new Http2SolrClient.Builder(configuration.getCore()).withResponseParser(new XMLResponseParser()).build();
    parser = new AutoDetectParser (tikaConfig);
    parser.setDetector (new FileCommandDetector());
    fileName = Pattern.compile (configuration.getFile());
  }


  @Override
  public FileVisitResult visitFile (Path path, BasicFileAttributes attributes)
    throws IOException
  {
    final String FILE_NAME = path.toString();
//System.out.println ("ALKU  " + FILE_NAME);
    System.out.println (totalDoc + " " + FILE_NAME);
    if (!fileName.matcher(FILE_NAME).matches()) return FileVisitResult.CONTINUE;
    if (excludes.matcher(FILE_NAME).matches()) return FileVisitResult.CONTINUE;

    try {
      if (Files.size (Path.of(FILE_NAME)) == 0) return FileVisitResult.CONTINUE;
    }
    catch (java.nio.file.NoSuchFileException e)
    {
      e.printStackTrace (System.out);
      System.exit (1);
    }

    final ContentHandler textHandler = new BodyContentHandler (writeLimit);
    final Metadata metadata = new Metadata();
    final ParseContext context = new ParseContext();

    final InputStream input = new FileInputStream (FILE_NAME);

    try {
      parser.parse (input, textHandler, metadata, context);
    }
    catch (Exception e)
    {
      System.out.println (String.format ("Tiedostoa %s ei voitu indeksoida.", FILE_NAME));
      e.printStackTrace();
      if (abortOnError) {
        System.exit (1);
      }
      else {
        return FileVisitResult.CONTINUE;
      }
    }

    dumpMetadata (FILE_NAME, metadata);


    final SolrInputDocument doc = new SolrInputDocument();
    doc.addField ("id", FILE_NAME);
    doc.addField ("text", textHandler.toString());
    ++totalDoc;

    docList.add (doc);

    if (docList.size() >= 1000) {
      try {
        final UpdateResponse resp = client.add (docList, commitWithinMs);
        if (resp == null) {
          System.out.println ("resp == null");
          System.exit (1);
        }
        if (resp.getStatus() != 0) {
          System.out.println ("Jokin kamala virhe on tapahtunut, status on: " + resp.getStatus());
          if (abortOnError) {
            System.exit (1);
          }
          else {
            return FileVisitResult.CONTINUE;
          }
        }
      }
      catch (SolrServerException e)
      {
        System.out.println (e.getMessage());
        e.printStackTrace (System.out);
        System.exit (1);
      }
      docList.clear();
    }
//System.out.println ("LOPPU " + FILE_NAME);
    return FileVisitResult.CONTINUE;
  }


  private void dumpMetadata (String fileName, Metadata metadata)
  {
    System.out.println ("Tiedoston " + fileName + " metadata.");
    for (String name : metadata.names()) {
      System.out.println (name + ": " + metadata.get(name));
    }
    System.out.println ("totalDoc " + totalDoc);
  }


  // Just a convenient place to wrap things up.
  public void endIndexing() throws IOException, SolrServerException
  {
    System.out.println ("endIndexing()");

    if (docList.size() > 0) {     // Are there any documents left over?
      client.add(docList, commitWithinMs);
    }
    client.commit(); // Only needs to be done at the end,
    // commitWithin should do the rest.
    // Could even be omitted
    // assuming commitWithin was specified.
    final long endTime = System.currentTimeMillis();
    final long seconds = (endTime - start) / 1000;
    final long minutes = seconds / 60;
    System.out.println ("Aikaa käytettiin " + (endTime - start) +
                        " millisekuntia " + totalDoc + " dokumentin indeksointiin (" +
                        getMinutes(minutes) + (seconds%60) + "," + ((endTime - start)%1000) + " sekuntia).");
    System.exit (0);
  }


  private String getMinutes (long minutes)
  {
    if (minutes == 0) {
      return "";
    }
    else if (minutes == 1) {
      return (minutes + " minuutti ");
    }
    else {
      return (minutes + " minuuttia ");
    }
  }


  private boolean getAbortOnError()
  {
    return (configuration.getOnError().value().compareTo(OnErrorType.ABORT.value()) == 0);
  }


  private final IndexerConfigurationType configuration;
  private final SolrClient client;
  private final AutoDetectParser parser;
  private final Pattern fileName;
  private final Pattern excludes;
  private final Collection<SolrInputDocument> docList = new ArrayList<SolrInputDocument>();
  private final int writeLimit;
  private final int commitWithinMs;
  private final boolean abortOnError;
  private final TikaConfig tikaConfig;
  private final boolean recursive;
  private final long start = System.currentTimeMillis();
  private int totalDoc = 0;

  private static final Logger logger = LogManager.getLogger (Indexer2Visitor.class);
  private static final long serialVersionUID = 1L;
}

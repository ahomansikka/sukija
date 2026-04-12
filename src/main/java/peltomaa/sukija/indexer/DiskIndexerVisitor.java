/*
Copyright (©) 2026 Hannu Väisänen

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

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpJdkSolrClient;
//import org.apache.solr.client.solrj.jetty.HttpJettySolrClient;
import org.apache.solr.client.solrj.response.XMLResponseParser;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.RemoteSolrException;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.util.NamedList;

import org.apache.tika.Tika;
import org.xml.sax.SAXException;

import peltomaa.sukija.schema.IndexerConfigurationType;
import peltomaa.sukija.schema.ObjectFactory;
import peltomaa.sukija.schema.OnErrorType;


public class DiskIndexerVisitor extends SimpleFileVisitor<Path> {
  DiskIndexerVisitor (IndexerConfigurationType configuration)
    throws IOException, SAXException
  {
    this.tika = new Tika();
    this.configuration = configuration;
    commitWithinMs = configuration.getCommitWithinMs().intValueExact();
    abortOnError = getAbortOnError();
    start = System.currentTimeMillis();
    filter = new SukijaFilenameFilter (this.configuration);

    client = new HttpJdkSolrClient.Builder
             (configuration.getSolrURL())
              .withDefaultCollection (configuration.getCollection())
              .withConnectionTimeout (configuration.getConnectionTimeout().intValueExact(), TimeUnit.MILLISECONDS)
              .withIdleTimeout (configuration.getIdleTimeout().intValueExact(), TimeUnit.MILLISECONDS)
              .withRequestTimeout (configuration.getRequestTimeout().intValueExact(), TimeUnit.MILLISECONDS)
              .withResponseParser (new XMLResponseParser())
              .build();
 
/*
    client = new HttpJettySolrClient.Builder
             (configuration.getSolrURL())
              .withDefaultCollection (configuration.getCollection())
              .withConnectionTimeout (configuration.getConnectionTimeout().intValueExact(), TimeUnit.MILLISECONDS)
              .withIdleTimeout (configuration.getIdleTimeout().intValueExact(), TimeUnit.MILLISECONDS)
              .withRequestTimeout (configuration.getRequestTimeout().intValueExact(), TimeUnit.MILLISECONDS)
              .withResponseParser (new XMLResponseParser())
              .build();
*/
  }


  @Override
  public FileVisitResult visitFile (Path path, BasicFileAttributes attributes) throws IOException
  {
    if (!filter.accept(null, path.toString())) return FileVisitResult.CONTINUE;

    final ContentStreamUpdateRequest up = new ContentStreamUpdateRequest ("/update/extract");
    up.setCommitWithin (commitWithinMs);
    final String contentType = tika.detect (path.toString());
    up.addFile (path, contentType);
    up.setParam ("literal.id", path.toString());
    up.setParam (CommonParams.HEADER_ECHO_PARAMS, CommonParams.EchoParamStyle.ALL.toString());

/* ************** Tulosta parametrit.
    ModifiableSolrParams p = up.getParams();
    java.util.Set<String> s = p.getParameterNames();
    Iterator<String> i = s.iterator();
    while (i.hasNext()) {
        String name = i.next();
        System.out.println ("        " + name + " " + p.get(name));
    }
*********************** */
    System.out.println (contentType + " " + String.format ("%05d", totalDoc) + " " + path.toString());

    try {
        NamedList<Object> result = client.request(up);
//        result.forEach (a -> System.out.println (a.getKey() + " " + a.getValue()));
    }
    catch (SolrServerException e)
    {
        System.err.println (e.getMessage());
        e.printStackTrace (System.out);
        return doOnError();
    }
    catch (RemoteSolrException e)
    {
        System.err.println (e.getResponseMessage());
        e.printStackTrace (System.out);
        return doOnError();
    }

    totalDoc++;
    return FileVisitResult.CONTINUE;
  }


  public void endIndexing() throws IOException, SolrServerException
  {
    System.out.println ("endIndexing()");
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


  FileVisitResult doOnError()
  {
    if (abortOnError) {
        System.err.println ("Lopetetaan virheen vuoksi.");
        System.exit (1);
    }
//    System.exit (1);
    return FileVisitResult.CONTINUE;
  }


  private final IndexerConfigurationType configuration;
  private final SolrClient client;
//  private final HttpJettySolrClient client;
  private final int commitWithinMs;
  private final boolean abortOnError;
  private long start = System.currentTimeMillis();
  private int totalDoc = 0;
  private final SukijaFilenameFilter filter;
  private final Tika tika;

//  private static final Logger LOG = LoggerFactory.getLogger (Indexer.class);
  private static final long serialVersionUID = 1L;
}

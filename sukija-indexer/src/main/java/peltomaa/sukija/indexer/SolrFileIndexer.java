/*
Copyright (©) 2012-2013 Hannu Väisänen

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

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;

import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;

import peltomaa.sukija.util.FileIndexer;

public class SolrFileIndexer extends FileIndexer {
  public SolrFileIndexer (String solrUrl, String pattern) throws java.net.MalformedURLException
  {
    super (pattern);
    solr = new CommonsHttpSolrServer (solrUrl);
  }


  /**
   * This function is modified from
   * http://wiki.apache.org/solr/ContentStreamUpdateRequestExample<p>
   *
   * Method to index all types of files into Solr. 
   * @param file  File to index.
   *
   * @throws IOException
   */
  public void indexFile (File file) throws IOException
  {
    try {
      ContentStreamUpdateRequest up = new ContentStreamUpdateRequest ("/update/extract");

      up.addFile (file);
      up.setParam ("literal.id", getId (file));
      up.setParam ("fmap.content", "text");

      up.setAction (AbstractUpdateRequest.ACTION.COMMIT, true, true);

      solr.request (up);
    }
    catch (SolrServerException e)
    {
      System.out.println ("SolrFileIndexer: " + e.getMessage());
      e.printStackTrace (System.out);
    }
/*
    catch (URISyntaxException e)
    {
      System.out.println ("SolrFileIndexer: " + e.getMessage());
      e.printStackTrace (System.out);
    }
    catch (ConnectException e)
    {
      System.out.println ("SolrFileIndexer: " + e.getMessage());
      e.printStackTrace (System.out);
    }
*/
  }

  /** Create unique key for a file.
   */
  String getId (File file) throws IOException
  {
    return file.getCanonicalPath();
//    String s = file.getCanonicalPath();
//    return s.replaceFirst (HOME, "");
  }

  private SolrServer solr;
//  private static final String HOME = System.getProperty ("user.home");
}

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

// Poistetaan kaikki dokumentit Solr:sta.

// Vertaa https://www.tutorialspoint.com/apache_solr/apache_solr_deleting_documents.htm
// joka ei toimi.


package peltomaa.sukija.indexer;

import java.io.IOException;  
import org.apache.solr.client.solrj.SolrClient; 
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.SolrServerException; 
import org.apache.solr.common.SolrInputDocument;  


public class DeleteAllDocuments
{
  public static void main (String[] args)
  {
    try {
      var d = new DeleteAllDocuments (args);
    }
    catch (Exception e)
    {
      e.printStackTrace (System.out);
      System.exit (1);
    }
  }


  private DeleteAllDocuments (String[] args) throws SolrServerException, IOException
  {
    final String solrURL = getSolrURL (args);
    final SolrClient client = new Http2SolrClient.Builder(solrURL).withResponseParser(new XMLResponseParser()).build();

    // Preparing the Solr document.
    SolrInputDocument doc = new SolrInputDocument();

    // Deleting the documents from Solr.
    client.deleteByQuery ("*");      

    // Saving the document.
    client.commit();
    System.out.println ("Kaikki dokumentit poistettu.");
    System.exit (0);
  }


  private String getSolrURL (String[] args)
  {
    if (args.length > 0) {
      return args[0];
    }
    else {
      return "http://localhost:8983/solr/sukija";
    }
  }
}

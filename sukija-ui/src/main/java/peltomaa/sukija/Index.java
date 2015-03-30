/*
Copyright (©) 2015 Hannu Väisänen

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

package peltomaa.sukija;

import java.io.File;
import org.apache.solr.client.solrj.SolrClient;


public class Index {
  public Index (SolrClient client)
  {
    this.client = client;
  }


  public void indexDocs (String[] fileName)
  {
    for (int i = 0; i < fileName.length; i++) {
      indexDoc (new File (fileName[i]));
    }
  }


  private void indexDoc (File file)
  {
    if (file.exists() && file.canRead()) {
      if (file.isDirectory()) {
        indexDocs (file.list());
      }
      else {
      }
    }
  }


  private SolrClient client;
}

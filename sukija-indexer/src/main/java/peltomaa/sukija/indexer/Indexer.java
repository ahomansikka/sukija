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

import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import peltomaa.sukija.util.FileIndexer;
import peltomaa.sukija.util.PropertiesUtil;

/**
 * Indexer.
 *
 */
public class Indexer {
  public static void main (String[] args)
  {
    try {
      PropertiesUtil.configure (r);
      Properties properties = System.getProperties();

      FileIndexer fileIndexer = new SolrFileIndexer
                                  (properties.getProperty ("sukija.solr.url"),
                                   properties.getProperty ("sukija.ignore.files"));
      fileIndexer.indexFiles (args);
    }
    catch (Throwable t)
    {
      System.out.println ("Error: " + t.getMessage());
      t.printStackTrace (System.out);
    }
  }

  private static final String BUNDLE_NAME = "peltomaa.sukija.indexer.Indexer";
  private static ResourceBundle r =
                 PropertyResourceBundle.getBundle (BUNDLE_NAME, Locale.getDefault());
}

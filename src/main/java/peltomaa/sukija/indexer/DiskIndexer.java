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

import java.io.InputStream;
import java.nio.file.Files;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import peltomaa.sukija.schema.IndexerConfigurationType;
import peltomaa.sukija.schema.ObjectFactory;
import peltomaa.sukija.util.XjcIO;


public class DiskIndexer {
  public static void main (String[] args)
  {
    try {
      indexer = new DiskIndexer (args);
      indexer.index();
    }
    catch (Throwable e)
    {
      e.printStackTrace (System.out);
      System.exit (1);
    }
  }


  private DiskIndexer (String[] args) throws Throwable
  {
//System.out.println (System.getProperty("user.home"));

    io = new XjcIO<IndexerConfigurationType> ("/IndexerConfiguration.xsd", IndexerConfigurationType.class, ObjectFactory.class);
    if (io == null) {System.err.println ("io == null"); System.exit(1);}

/* ****************
    if (args.length > 0) {
        configuration = io.read (args[0]);
    }
    else {
        // Ei toimi. )-:
        InputStream is = DiskIndexer.class.getClassLoader().getResourceAsStream ("indexer-configuration.xml");
        if (is == null) {System.err.println ("is == null"); System.exit(1);}
        configuration = io.read (is);
        if (configuration == null) {System.err.println ("configuration == null"); System.exit(1);}
    }
********* */

    if (args.length == 0) {
        System.err.println ("Asetustiedoston nimi puuttuu.");
        System.exit(1);
    }
    configuration = io.read (args[0]);
    if (configuration == null) {System.err.println ("configuration == null"); System.exit(1);} // Pitänee olla throw...
//    io.write (configuration, System.out); System.exit(1);

    visitor = new DiskIndexerVisitor (configuration);
  }


  private void index() throws Throwable
  {
    final String PROP = System.getProperty ("user.home");

    for (String directory : configuration.getBaseDir())
    {
        final String DIR = directory.replace ("${user.home}", PROP);
        Files.walkFileTree (java.nio.file.Path.of(DIR), visitor);
    }
    visitor.endIndexing();
  }


  private static DiskIndexer indexer;
  private final DiskIndexerVisitor visitor;
  private final IndexerConfigurationType configuration;
  private final XjcIO<IndexerConfigurationType> io;


//  private static final Logger LOG = LoggerFactory.getLogger (DiskIndexer.class);
  private static final long serialVersionUID = 1L;
}

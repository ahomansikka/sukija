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

import java.nio.file.Files;
import java.nio.file.FileVisitor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import peltomaa.sukija.schema.IndexerConfigurationType;
import peltomaa.sukija.schema.ObjectFactory;
import peltomaa.sukija.util.XjcIO;


public class Indexer2 {
  public static void main (String[] args)
  {
    try {
      indexer = new Indexer2 (args);
      indexer.index();
    }
    catch (Exception e)
    {
      e.printStackTrace (System.out);
      System.exit (1);
    }
  }


  private Indexer2 (String[] args) throws Exception
  {
    io = new XjcIO<IndexerConfigurationType> ("/IndexerConfiguration.xsd", IndexerConfigurationType.class, ObjectFactory.class);

    if (args.length > 0) {
      configuration = io.read (args[0]);
    }
    else {
      configuration = io.read (Indexer.class.getResourceAsStream ("/indexer-configuration-default.xml"));
    }

    visitor = new Indexer2Visitor (configuration);
  }


  private void index() throws Exception
  {
    for (String dir : configuration.getBaseDir())
    {
      index (dir);
    }
    visitor.endIndexing();
  }


  private void index (String fileName) throws Exception
  {
    Files.walkFileTree (java.nio.file.Path.of(fileName), visitor);
  }

  private static Indexer2 indexer;
  private final Indexer2Visitor visitor;
  private final IndexerConfigurationType configuration;
  private final XjcIO<IndexerConfigurationType> io;

  private static final Logger logger = LogManager.getLogger (Indexer2.class);
  private static final long serialVersionUID = 1L;
}

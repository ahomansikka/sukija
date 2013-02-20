/*
Copyright (©) 2011-2012 Hannu Väisänen

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

package peltomaa.sukija.tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParsingReader;


/** Get Tika ParsingReader.
 */
final public class TikaParsingReaderFactory {
  public static final Reader getParsingReader (FileInputStream stream, Metadata metadata)
    throws FileNotFoundException, IOException
  {
    ParseContext context = new ParseContext();
    Parser parser = new AutoDetectParser();
    context.set (Parser.class, parser);
    return new ParsingReader (parser, stream, metadata, context);
  }


  public static final Reader getParsingReader (FileInputStream stream)
    throws FileNotFoundException, IOException
  {
    return getParsingReader (stream, new Metadata());
  }


  public static final Reader getParsingReader (String fileName, Metadata metadata)
    throws FileNotFoundException, IOException
  {
    FileInputStream stream = new FileInputStream (new File (fileName));
    return getParsingReader (stream, metadata);
  }


  public static final Reader getParsingReader (String fileName)
    throws FileNotFoundException, IOException
  {
    return getParsingReader (fileName, new Metadata());
  }
}

/*
Copyright (©) 2017 Hannu Väisänen

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

package peltomaa.sukija.suggestion.distance;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MapMaker {
  public static final void main (String[] args)
  {
    try {
      MapMaker m = new MapMaker (args);
    }
    catch (Throwable t)
    {
      t.printStackTrace (System.out);
    }
  }


  private MapMaker (String[] args) throws FileNotFoundException, IOException
  {
    for (String file : args) {
      makeMapFromColumns (new FileReader (file));
    }
    stringMap.write ("output.txt");
    stringMap.write_gzip_file ("output.txt.gz");
    stringMap.read_gzip_file ("output.txt.gz");
    stringMap.write ("output2.txt");
  }


   /** Luetaan tiedot taulukosta, jonka eka sarakkeena on sanan perusmuoto ja
    *  toka sarakkeena sen taivutusmuoto, esimerkiksi
<pre>
aakkonen aakkonen
aakkonen aakkoseen
aakkonen aakkoseksi
aakkonen aakkosella
aakkonen aakkoselle
aakkonen aakkosemme
aakkonen aakkosen
aakkonen aakkosena
aakkonen aakkosensa
</pre>
    *
    * @param r Olio, josta luetaan.
    */   private void makeMapFromColumns (Reader r) throws FileNotFoundException, IOException
   {
     BufferedReader b = new BufferedReader (r);
     String line;
     while ((line = b.readLine()) != null) {
        String[] s = SEP.split (line);
        if (s.length != 2) throw new RuntimeException ("s.length != 2: " + line);

        stringMap.putValue (s[0], s[1]);
     }
   }


  private StringMap stringMap = new StringMap();
  private static final Pattern SEP = Pattern.compile ("(\\p{Punct}|\\p{Space})+");
}

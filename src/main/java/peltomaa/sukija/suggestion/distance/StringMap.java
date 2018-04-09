/*
Copyright (©) 2017-2018 Hannu Väisänen

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;


class StringMap extends TreeMap<String,TreeSet<String>> {
  public StringMap() {}


  public StringMap (String key, String value)
  {
    putValue (key, value);
  }


  public StringMap (String key, TreeSet<String> value)
  {
    put (key, value);
  }


  public void putValue (String key, String value)
  {
    if (!containsKey (key)) {
      put (key, new TreeSet<String>());
    }
    get(key).add (value);
  }


  public void putValue (String key, String[] value)
  {
    if (!containsKey (key)) {
      put (key, new TreeSet<String>());
    }
    get(key).addAll (Arrays.asList (value));
  }



  /** Luetaan assosiatiivinen taulukko, joka on tallennettu metodilla {@code write(Writer)}.
   *
   * @param r  Olio, josta luetaan.
   */
  private void read (Reader r) throws IOException
  {
    BufferedReader br = new BufferedReader (r);
    String line;
    while ((line = br.readLine()) != null) {
      final int n = line.indexOf(' ');
//System.out.println ("StringMap.read [" + line + "]");
      String key = line.substring (0, n);
      String[] value = SEP.split (line.substring(n+2));
//System.out.println ("XXXX " + key + " " + Arrays.asList(value).toString() + " §" + line + "§");
      putValue (key, value);
    }
  }


  /** Luetaan assosiatiivinen taulukko tiedostosta.
   *
   * @param file  Tiedosto, josta luetaan.
   */
  private void read (String file) throws FileNotFoundException, IOException
  {
    try {
      read (new FileReader (file));
    }
    catch (FileNotFoundException e)
    {
//      ClasspathResourceLoader loader = new ClasspathResourceLoader();
      ClasspathResourceLoader loader = new ClasspathResourceLoader (this.getClass());
      InputStreamReader isr = new InputStreamReader (loader.openResource (file));
      read (isr);
//      throw e;
    }
  }


  /** Tallennetaan assosiatiivinen taulukko muotoon, josta se
   *  voidaan lukea funktiolla {@code read(Reader)}.<p>
   *
   * Tiedostoon tallennetaan ensin sanan perusmuoto ja sen jälkeen hakasulkujen
   * sisälle kaikki sen taivutusmodot, kaikki yhdelle riville. Esimerkiksi
   *
<pre>
aadolfinkatu [aadolfinkadulla, aadolfinkadun, aadolfinkatu, aadolfinkatuun]
aakkosellisuus [aakkosellisuudesta, aakkosellisuus]
</pre>
   *
   * Huomaa, että tiedoston rivit voivat olla hyvin pitkiä, jos taivutusmuotoja on paljon.
   *
   * @param w Olio, johon kirjoitetaan.
   */
  private void write (Writer w) throws IOException
  {
    for (String key : keySet()) {
      final Set<String> set = get(key);
      if (set.size() > 1) {
        w.write (key + " " + set.toString() + "\n");
        w.flush();
      }
    }
  }


  /** Kirjoitetaan assosiatiivinen taulukko tiedostoon, josta se
   *  voidaan lukea funktiolla {@code read(String)}.
   *
   * @param file Tiedosto, johon kirjoitetaan.
   */
  private void write (String file) throws IOException
  {
    write (new FileWriter (file));
  }


  public void readGzipFile (String file) throws FileNotFoundException, IOException
  {
    GZIPInputStream gis = new GZIPInputStream (new FileInputStream (file));
    InputStreamReader reader = new InputStreamReader (gis);
    read (reader);
    reader.close();
  }


  public void writeGzipFile (String file) throws FileNotFoundException, IOException
  {
    GZIPOutputStream gos = new GZIPOutputStream (new FileOutputStream (file));
    OutputStreamWriter writer = new OutputStreamWriter (gos);
    write (writer);
    writer.close();
  }


  private static final Pattern SEP = Pattern.compile ("(\\p{Punct}|\\p{Space})+");
}

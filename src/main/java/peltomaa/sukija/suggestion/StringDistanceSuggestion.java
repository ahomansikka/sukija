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

package peltomaa.sukija.suggestion;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.lucene.search.spell.JaroWinklerDistance;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.apache.lucene.search.spell.NGramDistance;
import org.apache.lucene.search.spell.StringDistance;


import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.voikko.VoikkoUtils;

public class StringDistanceSuggestion  extends Suggestion {
  /**
   * Muodostin.
   *
   * @param voikko            Voikko.
   * @param reader            Lukija, josta merkkijonokartta luetaan.
   * @param distanceClassName Luokka, joka laskee kahden merkkijonon samanlaisuuden. Sallitut arvot ovat
                              @{code JaroWinklerDistance}, @{code LevensteinDistance},
                              @{code LuceneLevenshteinDistance} ja @{code NGramDistance}.
   * @param parameter         NGramDistance-luokan muodostimen argumentti.
   * @param threshold
   */
  public StringDistanceSuggestion (Voikko voikko, Reader reader, String distanceClassName, String parameter, float threshold) throws IOException
  {
    super (voikko);
    map = readMap (reader);
    sd = getDistanceClass (distanceClassName, parameter);
    this.threshold = threshold;
  }


  /**
   * Muodostin.
   *
   * @param voikko            Voikko.
   * @param fileName          Tiedosto, josta merkkijonokartta luetaan.
   * @param distanceClassName Luokka, joka laskee kahden merkkijonon samanlaisuuden. Sallitut arvot ovat
                              @{code JaroWinklerDistance}, @{code LevensteinDistance},
                              @{code LuceneLevenshteinDistance} ja @{code NGramDistance}.
   * @param parameter         NGramDistance-luokan muodostimen argumentti.
   * @param threshold
   */
  public StringDistanceSuggestion (Voikko voikko, String fileName, String distanceClassName, String parameter, float threshold)
    throws FileNotFoundException, IOException
  {
    this (voikko, new FileReader (fileName), distanceClassName, parameter, threshold);
  }


  public boolean suggest (String word)
  {
//System.out.println ("1");
    if (word.length() <= keyLength) return false;

    final String key = word.substring (0, keyLength);
//System.out.println ("2");
    if (!map.containsKey (key)) return false;

    final Entry entry = bestMatch (word, map.get (key));
    if (entry.distance <= threshold) {
//System.out.println ("3");
      return false;
    }
    else {
//System.out.println ("4");
      result.clear();
      return VoikkoUtils.analyze (voikko, entry.string, result);
    }
  }


  private StringDistance getDistanceClass (String className, String parameter)
  {
    switch (className) {
      case "JaroWinklerDistance": {
        JaroWinklerDistance jwd = new JaroWinklerDistance();
        if (parameter != null) {
          jwd.setThreshold (Float.valueOf (parameter));
        }
        return jwd;
      }
      case "LevensteinDistance": return new LevensteinDistance();
      case "LuceneLevenshteinDistance": return new LuceneLevenshteinDistance();
      case "NGramDistance": {
         if (parameter == null) {
           return new NGramDistance();
         }
         else {
           return new NGramDistance (Integer.valueOf (parameter));
         }
      }
      default: throw new RuntimeException (getClass().getName() + ": väärä luokan nimi " + className);
    }
  }


  private Map<String, Set<String>> readMap (Reader reader) throws IOException
  {
    HashMap<String, Set<String>> m = new HashMap<String, Set<String>>();

    BufferedReader r = new BufferedReader (reader);
    String line;

    while ((line = r.readLine()) != null) {
//System.out.println (line);
      final int N = line.indexOf (" [");
      if (N == -1) {
        throw new RuntimeException ("Tiedoston formaatti on väärin: " + line);
      }

      if (keyLength == 0) {
        keyLength = N;
      }
      else if (N != keyLength) {
        throw new RuntimeException ("Tiedoston formaatti on väärin: " + line);
      }

      final String[] s = line.substring(N+2,line.length()-1).split (", ");;
      if (s.length == 0) {
        throw new RuntimeException ("Tiedoston formaatti on väärin: " + line);
      }

      final String key = line.substring(0,N);
      if (m.containsKey (key)) {
        throw new RuntimeException ("Tiedoston formaatti on väärin: " + line);
      }
      m.put (key, new HashSet<String> (Arrays.asList (s)));
    }
    return m;
  }


  private static class  Entry {
    public float distance;
    public String string;
    public Entry (float d, String s) {distance = d; string = s;}
    public String toString() {return String.format ("%f %s", distance, string);}
  }


  private Entry bestMatch (String s, Set<String> set)
  {
    Entry entry = new Entry (0, "");
    for (String u : set) {
      final float d = sd.getDistance (s, u);
//      System.out.println ("\n" + s + " " + (new Entry (d, u)) + " maxDistance");
      if (d > entry.distance) {
        entry.distance = d;
        entry.string = u;
      }
    }
//    System.out.println (s + " " + entry + " maxDistance xxx");
    return entry;
  }


  private final StringDistance sd;
  private final Map<String, Set<String>> map;
  private final float threshold;
  private int keyLength = 0;
}

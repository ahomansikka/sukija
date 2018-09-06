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
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.search.spell.JaroWinklerDistance;
import org.apache.lucene.search.spell.LevenshteinDistance;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.apache.lucene.search.spell.NGramDistance;
import org.apache.lucene.search.spell.StringDistance;

import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;

import peltomaa.sukija.voikko.VoikkoUtils;


/** Testataan, mitä arvoja eri Distance-luokat antavat.
 */
public class DistanceTest {
  public static final void main (String[] args)
  {
    try {
      DistanceTest m = new DistanceTest (args);
    }
    catch (Throwable t)
    {
      t.printStackTrace (System.out);
    }
  }


  private DistanceTest (String[] args) throws FileNotFoundException, IOException
  {
    keyMap = keyMap.newInstance (DISTANCE_FILE, KEY_LENGTH);

    for (String file : args) {
      test (file);
    }
  }


  private void test (String file) throws FileNotFoundException, IOException
  {
    String word;
    BufferedReader reader = new BufferedReader (new FileReader (file));

    while ((word = reader.readLine()) != null) {
      if (word.indexOf('-') >= 0) continue;
      List<Analysis> analysis = voikko.analyze (word);
      if (analysis.size() == 0) {
        final String key = keyMap.makeKey (word);
        if (keyMap.containsKey (key)) {
          match (key, word);
        }
      }
    }
    reader.close();
  }


  private void match (String key, String word)
  {
   Iterator<StringMap> i = keyMap.get(key).iterator();
   while (i.hasNext()) {
      StringMap m = i.next();
      for (String baseForm : m.keySet()) {
        for (String u : m.get(baseForm)) {
          final float d1 = jaro.getDistance (word, u);
          final float d2 = levenshtein.getDistance (word, u);
          final float d3 = lucene.getDistance (word, u);
          final float d4 = ngram.getDistance (word, u);
          System.out.println (String.format ("%f %f %f %f %s %s", d1, d2, d3, d4, word, u));
        }
      }
    }
  }


  private static final String VOIKKO_PATH = "/usr/local/lib/voikko";
  private static final String LIBRARY_PATH = "/usr/local/lib";
  private static final String LIBVOIKKO = "/usr/local/lib/libvoikko.so";

//  private static final String LANGUAGE = "fi";
  private static final String LANGUAGE = "fi-x-sukija";


  private KeyMap keyMap;
  private static final int KEY_LENGTH = 7;
  private static final String DISTANCE_FILE = "distance.txt.gz";
  private static final Voikko voikko = VoikkoUtils.getVoikko (LANGUAGE, VOIKKO_PATH, LIBRARY_PATH, LIBVOIKKO);


  private StringDistance jaro = new JaroWinklerDistance();
  private StringDistance levenshtein = new LevenshteinDistance();
  private StringDistance lucene = new LuceneLevenshteinDistance();
  private StringDistance ngram = new NGramDistance();
}

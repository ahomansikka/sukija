/*
Copyright (©) 2015-2017, 2020, 2022 Hannu Väisänen

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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.apache.lucene.search.spell.JaroWinklerDistance;
import org.apache.lucene.search.spell.LevenshteinDistance;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.apache.lucene.search.spell.NGramDistance;
import org.apache.lucene.search.spell.StringDistance;

import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.suggestion.distance.Distance;


public class StringDistanceSuggestion extends Suggestion {
  /**
   * Muodostin.
   *
   * @param voikko            Voikko.
   * @param fileName          Tiedosto, josta merkkijonokartta luetaan.
   * @param distanceClassName Luokka, joka laskee kahden merkkijonon samanlaisuuden. Sallitut arvot ovat
                              {@code JaroWinklerDistance}, {@code LevenshteinDistance},
                              {@code LuceneLevenshteinDistance} ja {@code NGramDistance}.
   * @param parameter         Joko NGramDistance-luokan muodostimen argumentti (int, oletus 2),
   *                          tai JaroWinklerDistance-luokan bonus (float, oletus 0.7).
                              jos tämä on {@code null}, käytetään oletusarvoja.
   * @param keyLength      Merkkijonokartan avainkentän maksimipituus.
   * @param threshold      Kaksi merkkijonoa ovat samanlaiset, jos luokan distanceClass
   *                       palauttama arvo on &gt;= kuin tämä arvo.
   */
   public StringDistanceSuggestion (Voikko voikko, String fileName, String distanceClassName,
                                    String parameter, int keyLength, float threshold)
     throws FileNotFoundException, IOException
  {
    super (voikko);

    StringDistance sd = getDistanceClass (distanceClassName, parameter);
    distance = new Distance (sd, fileName, keyLength, threshold);
  }


  @Override
  public boolean suggest (String word)
  {
    clearAnalysis();
    if (word.indexOf("-") >= 0) return false;

    final String value = distance.bestMatch (word);
    if (value != null) {
      return analyze (value);
    }
    return false;
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
      case "LevenshteinDistance": return new LevenshteinDistance();
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


  private Distance distance;
}

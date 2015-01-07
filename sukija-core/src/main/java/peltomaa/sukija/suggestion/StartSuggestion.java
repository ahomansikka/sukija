/*
Copyright (©) 2014 Hannu Väisänen

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

import java.util.Set;
import java.util.TreeSet;
import peltomaa.sukija.morphology.Morphology;


public class StartSuggestion extends Suggestion {
  /**
   * Constructor.
   *
   * @param morphology  An instance of morphology.
   */
  public StartSuggestion (Morphology morphology, int minLength, int maxLength, boolean baseFormOnly)
  {
    super (morphology);
    this.minLength = minLength;
    this.maxLength = maxLength;
    this.baseFormOnly = baseFormOnly;
  }


  @Override
  public boolean suggest (String word)
  {
    reset();

    // Käydään läpi kaikki sanan alut pisimmästä alkaen ja
    // lopetetaan, kun löytyy eka tunnistettu sana.
    //
    for (int i = Math.min (maxLength, word.length()); i >= minLength; i--) {
      final String startOfWord = word.substring (0, i);
      if (baseFormOnly) {
        set.clear();
        if (analyse (startOfWord, set)) {
          result.clear();
          for (String s: set) {
            if (startOfWord.equals (s)) {
              result.add (s);
            }
          }
          return (result.size() > 0);
        }
      }
      else {
        sb.append (startOfWord);
        return analyse();
      }      
    }
    return false;
  }


  private final int minLength;
  private final int maxLength;
  private final boolean baseFormOnly;
  private Set<String> set = new TreeSet<String>();
}

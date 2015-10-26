/*
Copyright (©) 2014-2015 Hannu Väisänen

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

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.voikko.VoikkoUtils;


public class StartSuggestion extends Suggestion {
  /**
   * Constructor.
   *
   * @param voikko  An instance of voikko.
   */
  public StartSuggestion (Voikko voikko, int minLength, int maxLength, boolean baseFormOnly, boolean tryAll)
  {
    super (voikko);
    this.minLength = Math.max (minLength, 0);
    this.maxLength = maxLength;
    this.baseFormOnly = baseFormOnly;
    this.tryAll = tryAll;
  }


  @Override
  public boolean suggest (String word)
  {
    result.clear();


    // Käydään läpi kaikki sanan alut pisimmästä alkaen.
    //
    for (int i = Math.min (maxLength, word.length()-1); i >= minLength; i--) {
      final String startOfWord = word.substring (0, i);
      if (baseFormOnly) {
        if (VoikkoUtils.analyze (voikko, startOfWord, set)) {
//System.out.println ("\nStart1 " + startOfWord + " " + set.toString());
          for (String s: set) {
            if (startOfWord.equals (s)) {
              result.add (s);
            }
          }
          if (!tryAll && result.size() > 0) return true;
        }
      }
      else if (VoikkoUtils.analyze (voikko, startOfWord, set)) {
//System.out.println ("\nStart2 " + startOfWord + " " + set.toString());
        result.addAll (set);
        if (!tryAll) return true;
      }      
    }
    return (result.size() > 0);
  }


  private final int minLength;
  private final int maxLength;
  private final boolean baseFormOnly;
  private final boolean tryAll;
  private Set<String> set = new HashSet<String>();
}

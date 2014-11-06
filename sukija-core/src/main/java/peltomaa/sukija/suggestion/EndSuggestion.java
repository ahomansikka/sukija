/*
Copyright (©) 2009-2011, 2013-2014 Hannu Väisänen

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

import java.util.Vector;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.util.Pair;


/**
 * Lisätään sanan loppuun merkkijono ja yritetään tunnistaa sana sen jälkeen.
 */
public class EndSuggestion extends Suggestion {
  private EndSuggestion (Morphology morphology, Vector<Pair<String,String>> data)
  {
    super (morphology);
    v = data;
  }


  /** Jos <code>array = {"ks", "i"}</code> ja tunnistettava sana on {@code puuks},
   * yritetään tunnistaa sana {@code puuksi}.
   */
  public EndSuggestion (Morphology morphology, String[] array)
  {
    this (morphology, Pair.makePair (array));
  }


  public boolean suggest (String word)
  {
    for (int i = 0; i < v.size(); i++) {
      if (word.endsWith (v.get(i).first)) {
        reset();
        sb.append(word).append(v.get(i).second);
        if (analyse()) return true;
      }
    }
    return false;
  }


  private Vector<Pair<String,String>> v;
}

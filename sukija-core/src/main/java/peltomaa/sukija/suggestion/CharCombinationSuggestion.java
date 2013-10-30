/*
Copyright (©) 2009-2011, 2013 Hannu Väisänen

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
import peltomaa.sukija.util.Combination;


/**
 * Replace combinations of characters.
 */
public class CharCombinationSuggestion extends Suggestion {
  public CharCombinationSuggestion (Morphology morphology, String from, String to)
  {
    super (morphology);
    if (from.length() != to.length()) {
      throw new IllegalArgumentException ("from.length() != to.length()");
    }
    this.from = from;
    this.to = to;
  }


  public boolean suggest (String word)
  {
    reset();

    Vector<Integer> v = new Vector<Integer>();

    // Put the indices of 'from' characters in 'word' into vector v.
    //
    for (int i = 0; i < word.length(); i++) {
      if (from.indexOf (word.charAt (i)) >= 0) {
        v.add (i);
      }
    }

    if ((0 < v.size()) && (v.size() <= MAX_CHARS)) {
      for (int i = 0; i <= v.size(); i++) {
        Combination c = new Combination (v.size(), i);
        do {
          sb.delete (0, sb.length());
          sb.append (word);

          for (int k = 0; k < c.getK(); k++) {
            final int p = v.get (c.get (k));
            final int u = from.indexOf (sb.charAt (p));
            sb.replace (p, p+1, to.substring(u,u+1));
          }
          if (analyse()) return true;
        } while (c.next());
      }
    }

    return false;
  }

  private String from;
  private String to;
  private static int MAX_CHARS = 10;
}

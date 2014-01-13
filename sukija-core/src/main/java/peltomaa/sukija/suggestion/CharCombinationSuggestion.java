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

    int size = 0;

    // Put the indices of 'from' characters in 'word' into array v.
    //
    for (int i = 0; i < word.length(); i++) {
      if (from.indexOf (word.charAt (i)) >= 0) {
        v[size] = i;
        w[size++] = to.charAt (from.indexOf (word.charAt (i)));
        if (size >= MAX_CHARS) {
          return false;  // Return silently, if we have too many chars to replace.
        }
      }
    }

    if (size > 0) {                      // Do we have something to replace?
      for (int i = 0; i <= size; i++) {  // If we do, we test all combinations.
        Combination c = new Combination (size, i);
        do {
          sb.delete (0, sb.length());
          sb.append (word);

          for (int k = 0; k < c.getK(); k++) {
            sb.setCharAt (v[c.get(k)], w[c.get(k)]);
          }
          if (analyse()) return true;
        } while (c.next());
      }
    }
    return false;
  }


  private String from;
  private String to;
  private static int MAX_CHARS = 11;  // Max number of allowed permutations is 10! or 3628800.
  private int[] v = new int[MAX_CHARS];
  private char[] w = new char[MAX_CHARS];
}

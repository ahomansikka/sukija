/*
Copyright (©) 2009-2011 Hannu Väisänen

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

import peltomaa.sukija.morphology.Morphology;


/**
 * Replace three consecutive same letters with two.<p>
 *
 * For example the word "kauttta" is converted to "kautta".
 */
public class Length3Suggestion extends Suggestion {
  public Length3Suggestion (Morphology morphology)
  {
    super (morphology);
  }


  public boolean suggest (CharSequence word)
  {
    reset();

    int i = 0;
    for ( ; i < word.length()-2; i++) {
      if ((word.charAt(i) == word.charAt(i+1)) && (word.charAt(i) == word.charAt(i+2))) {
        sb.append(word.charAt(i)).append(word.charAt(i+1));
        i += 2;
        found = true;
      }
      else {
        sb.append (word.charAt(i));
      }
    }
    sb.append (word.subSequence (i, word.length()));

    if (!found) return false;
    return analyse();
  }
}

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

import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.util.StringUtil;


/**
 * Erase characters that are between from and to.<p>
 * For example<p>
 * {@code EraseSuggestion (morphology, '[', ']');}<p>
 * converts
 * {@code sitte[n]} to {@code sitten} and {@code sitte}
 * and tries to recognize them.
 */
public class EraseSuggestion extends Suggestion {
  public EraseSuggestion (Morphology morphology, char from, char to)
  {
    super (morphology);
    this.from = from;
    this.to = to;
    this.fromTo.append(from).append(to);
  }


  public boolean suggest (String word)
  {
    if ((StringUtil.indexOf (word, from) == -1) &&
        (StringUtil.indexOf (word, to)   == -1)) {
      return false;
    }

    reset();
    sb.append (word);
    StringUtil.erase (sb, fromTo);

    if (analyse()) return true;

    reset();
    sb.append (word);
    StringUtil.erase (sb, from, to);

    return analyse();
  }

  private char from;
  private char to;
  private StringBuffer fromTo = new StringBuffer();
}

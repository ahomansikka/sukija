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


/**
 * Replace characters with other characters.<p>
 *
 * Each from[i] is replaced with to[i] one at a time.<p>
 * For example<p>
 * {@code CharSuggestion (morphology, "w", "v");}<p>
 * replace {@code w} with {@code v}; e.g.
 * replacing {@code wanha} with {@code vanha}.<p>
 *
 * If you are replacing more than one letter use
 * {@link peltomaa.sukija.suggestion.CharCombinationSuggestion} instead.
 */
public class CharSuggestion extends Suggestion {
  public CharSuggestion (Morphology morphology, String from, String to)
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
    sb.append (word);

    for (int i = 0; i < sb.length(); i++) {
      for (int j = 0; j < from.length(); j++) {
        if (sb.charAt (i) == from.charAt (j)) {
          final char ch = sb.charAt (i);
          sb.setCharAt (i, to.charAt (j));
          if (analyse()) {
            return true;
          }
          sb.setCharAt (i, ch);
        }
      }
    }

    return found;
  }

  private String from;
  private String to;
}

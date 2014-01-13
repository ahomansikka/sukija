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

import peltomaa.sukija.morphology.Morphology;
import java.util.Collection;
import java.util.Vector;


/**
 * Replace a string and try to recognise a word after that.
 */
public class StringSuggestion extends Suggestion {
  public StringSuggestion (Morphology morphology, String from, String to)
  {
    super (morphology);
    this.str.add (from);
    this.str.add (to);
  }


  public StringSuggestion (Morphology morphology, Collection<String> str)
  {
    super (morphology);
    this.str.addAll (str);
  }


  public boolean suggest (String word)
  {
    reset();

    for (int i = 0; i < str.size(); i += 2) {
      if (word.indexOf (str.get(i)) >= 0) {
        final String s = word.replace (str.get(i), str.get(i+1));
        result.clear();
        if (analyse (s, result)) {
          return true;
        }
      }
    }
    return false;
  }

  private Vector<String> str = new Vector<String>();
}

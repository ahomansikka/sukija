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
import java.util.Set;
import java.util.TreeSet;


/**
 * Replace a string and try to recognise a word after that.
 */
public class StringSuggestion extends Suggestion {
  public StringSuggestion (Morphology morphology, String[][] input)
  {
    super (morphology);
    this.input = input;
  }


  public StringSuggestion (Morphology morphology, String[] input)
  {
    super (morphology);
    this.input = new String[1][];
    this.input[0] = input;
  }


  public boolean suggest (String word)
  {
    for (int i = 0; i < input.length; i++) {
      result.clear();
      if (word.indexOf (input[i][0]) >= 0) {
        for (int j = 1; j < input[i].length; j++) {
          final String s = word.replace (input[i][0], input[i][j]);
          set.clear();
          if (analyse (s, set)) {
            result.addAll (set);
          }
        }
        return (result.size() > 0);
      }
    }
    return false;
  }

  private String[][] input;
  private Set<String> set = new TreeSet<String>();
}

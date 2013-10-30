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

import java.util.Set;
import java.util.Vector;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.util.Pair;
import peltomaa.sukija.util.RegexCombinator;


public class RegexCombinationSuggestion extends Suggestion {
  public RegexCombinationSuggestion (Morphology morphology, Vector<Pair<String,String>> data)
  {
    super (morphology);
    rc = new RegexCombinator (data);
  }


  public RegexCombinationSuggestion (Morphology morphology, String[] array)
  {
    super (morphology);
    rc = new RegexCombinator (array);
  }


  public boolean suggest (String word)
  {
// System.out.println ("Regex " + word);
    Set<String> set = rc.makeCombination (word);

    if (set == null) return false;

    return analyse (set);
  }


  private RegexCombinator rc;
}

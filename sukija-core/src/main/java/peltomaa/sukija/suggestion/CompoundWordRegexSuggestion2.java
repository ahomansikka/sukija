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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.TreeSet;

import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.morphology.MorphologyException;
import peltomaa.sukija.util.RegexUtil;


/**
 * Jos {@code morphology} tunnistaa säännöllisen lausekkeen {@code regex}
 * jälkeen tulevan sanan osan sanaksi, muutetaan tunnistettu sana
 * perusmuotoon ja palautetaan sanan alkuosa + tunnistettu perusmuoto.
 * <p>
 * Esimerkki: jos {@code regex} on {@code "l[aä]is"} ja sana on
 * {@code aldabranjättiläiskilpikonnalla} palautetaan
 * {@code aldabranjättiläiskilpikonna}.
 */
public class CompoundWordRegexSuggestion2 extends Suggestion {
  /**
   * @param morphology  Morphology.
   * @param regex       Regular expression
   */
  public CompoundWordRegexSuggestion2 (Morphology morphology, String regex)
  {
    super (morphology);
    pattern = RegexUtil.makePattern (regex);
  }


  public boolean suggest (String word)
  {
    Matcher matcher = pattern.matcher (word);

    if (matcher.find()) {
      reset();
      final String end = word.substring (matcher.end());
      Set<String> set = new TreeSet<String>();

      if (analyse (end, set)) {
        result.clear();
        final String start = word.substring (0, matcher.end());
        for (String s: set) {
          result.add (start + s);
        }
        return true;
      }
    }
    return false;
  }

  private final Pattern pattern;
}

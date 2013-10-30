/*
Copyright (©) 2013 Hannu Väisänen

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


/**
 * Split a word and try to recognise each part.<p>
 *
 * For example, {@code vanhaanmalliin} is split as {@code vanhaan},
 * {@code malliin} and base forms are {@code vanha} and {@code malli}.
 * This class splits word only in two parts, otherwise we would get
 * too many incorrect words.
 */
public class CompoundWordRegexSuggestion extends Suggestion {
  /**
   * @param morphology  Morphology.
   * @param regex       Regular expression
   */
  public CompoundWordRegexSuggestion (Morphology morphology, String regex)
  {
    super (morphology);
    pattern = Pattern.compile (regex);
  }


  public boolean suggest (String word)
  {
    Matcher matcher = pattern.matcher (word);

    if (matcher.find()) {
      reset();
      final String end = word.substring (matcher.start());
      Set<String> set = new TreeSet<String>();

      if (analyse (end, set)) {
        result.clear();
        final String start = word.substring (0, matcher.start());
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

/*
Copyright (©) 2009-2012 Hannu Väisänen

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

import java.util.Arrays;

import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.morphology.MorphologyException;


/**
 * Split a word on regular expression and try to recognise each part.<p>
 *
 * For example, {@code SplitSuggestion (morphology, "-")} splits
 * {@code aaa-bbb-ccc} to {@code aaa}, {@code bbb} and {@code ccc} and
 * tries to convert each part into base form. If base form is not found
 * unconverted string ({@code aaa} etc) is returned as a base form.
 */
public class SplitSuggestion extends Suggestion {
  /**
   * @param morphology  Morphology.
   * @param regex       Regular expression to split the word.
   */
  public SplitSuggestion (Morphology morphology, String regex)
  {
    super (morphology);
    this.regex = regex;
  }


  public boolean suggest (CharSequence word)
  {
    reset();
    result.clear();


    final String[] s = word.toString().split (regex);
/*
    if (LOG.isDebugEnabled()) {
      LOG.debug ("Split1 " + word.toString() + " " + s.length);
    }
*/
    if (s.length == 1) {  // Regex did not match.
      return false;
    }

    for (int i = 0; i < s.length; i++) {
      if (s[i].length() > 0) {
        if (!analyse (s[i])) {
          return false;
        }
      }
    }
    return true;
  }


  private boolean analyse (String word)
  {
    try {
      morphology.analyzeLowerCase (word, result);
/*
      if (LOG.isDebugEnabled()) {
        LOG.debug ("Split2 " + word.toString() + " "
                      + Arrays.toString (result.toArray (new String[0])));
      }
*/
      return true;
    }
    catch (MorphologyException e)
    {
      LOG.error (e.getMessage());
    }
    return false;
  }

  private final String regex;
}

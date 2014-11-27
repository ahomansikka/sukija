/*
Copyright (©) 2009-2014 Hannu Väisänen

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
import java.util.Iterator;
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
public class CompoundWordSuggestion extends Suggestion {
  /**
   * @param morphology  Morphology.
   * @param minLength  Minumum length of word.
   */
  public CompoundWordSuggestion (Morphology morphology, int minLength)
  {
    super (morphology);
    this.minLength = minLength;
  }


  public boolean suggest (String word)
  {
    reset();
    result.clear();
    found = split (word);
/*
    if (LOG.isDebugEnabled() && found) {
      LOG.debug ("CompoundWord " + word.toString() + " "
                    + Arrays.toString (result.toArray (new String[0])))
    }
*/
    return found;
  }


  private boolean split (String word)
  {
    for (int i = minLength; i < word.length()-minLength; i++) {
      if (suggest (word.substring (0, i), word.substring (i, word.length()))) {
        return true;
      }
    }
    return false;
  }



  private boolean suggest (CharSequence word1, CharSequence word2)
  {
    try {
      tmp1.clear();
      tmp2.clear();

      if (morphology.analyzeLowerCase (word1.toString(), tmp1) &&
          morphology.analyzeLowerCase (word2.toString(), tmp2)) {
        result.addAll (tmp1);
        result.addAll (tmp2);
        return true;
      }
    }
    catch (MorphologyException e)
    {
      LOG.error (e.getMessage());
    }
    return false;
  }


  private final int minLength;
  private Set<String> tmp1 = new TreeSet<String>();
  private Set<String> tmp2 = new TreeSet<String>();
}

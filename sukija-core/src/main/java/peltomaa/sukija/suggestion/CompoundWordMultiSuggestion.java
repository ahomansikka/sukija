/*
Copyright (©) 2013-2014 Hannu Väisänen

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.TreeSet;


import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.morphology.MorphologyException;


/**
 * Katkaistaan sana säännöllisen lausekkeen kohdalta ja yritetään tunnistaa molemmat osat.
 *
 * Esimerkiksi jos säännöllinen lauseke on {@code "-"} ja sana on {@code kuun-kauden},
 * yritetään tunnistaa {@code kuun} ja {@code kauden}, jolloin perusmuodot ovat
 * {@code kuu} ja {@code kausi}
 */
public class CompoundWordMultiSuggestion extends Suggestion {
  /**
   * @param morphology  Morfologia.
   * @param splitRegex  Säännöllinen lauseke, jolla yhdyssana katkaistaan, yleensä "-".
   * @param parseAll    True, jos kaikki sanan osat muutetaan perusmuotoon, muuten false.
   */
  public CompoundWordMultiSuggestion (Morphology morphology, String splitRegex, boolean parseAll)
  {
    super (morphology);
    this.splitPattern = Pattern.compile (splitRegex);
    this.parseAll = parseAll;
  }


  public boolean suggest (String word)
  {
    try {
      reset();
      result.clear();

      final String[] words = splitPattern.split (word);

      if (words.length > 1) {
        found = suggest (words);

        if (parseAll) {
          for (int i = 0; i < words.length; i++) {
            found |= analyse (words[i], result);
          }
        }
      }
      return found;
    }
    catch (MorphologyException e)
    {
      LOG.error (e.getMessage());
    }
    return false;
  }


  private boolean suggest (String[] word)
  {
    splitSet.clear();
    if (morphology.analyzeLowerCase (word[word.length-1], splitSet)) {
      final String START = join (word, 0, word.length-1, "-") + "-";
      for (String s : splitSet) {
        result.add (START + s);
      }
      return true;
    }
    return false;
  }


  private String join (String[] s, int start, int end, String separator)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = start; i < end; i++) {
      sb.append (s[i]);
      if (i < end-1) {
        sb.append (separator);
      }
    }
    return sb.toString();
  }


  private final Pattern splitPattern;
  private Set<String> splitSet = new TreeSet<String>();
  private boolean parseAll;
}

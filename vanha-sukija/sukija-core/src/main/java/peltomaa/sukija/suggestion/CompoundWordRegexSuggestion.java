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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.morphology.MorphologyException;
import peltomaa.sukija.util.RegexUtil;


public class CompoundWordRegexSuggestion extends Suggestion {
  public CompoundWordRegexSuggestion (Morphology morphology, String regex)
  {
    super (morphology);
    pattern = new Pattern[1];
    pattern[0] = RegexUtil.makePattern (regex);
  }


  public CompoundWordRegexSuggestion (Morphology morphology, List<String> regex)
  {
    super (morphology);
    pattern = new Pattern[regex.size()];
    for (int i = 0; i < regex.size(); i++) {
      pattern[i] = RegexUtil.makePattern (regex.get(i));
    }
  }


  public boolean suggest (String word)
  {
    for (int i = 0; i < pattern.length; i++) {
      Matcher matcher = pattern[i].matcher (word);

      if (matcher.find()) {
        result.clear();
        if (suggest (word, matcher.start()) || suggest (word, matcher.end())) {
          return true;
        }
      }
    }
    return false;
  }


  private boolean suggest (String word, int first)
  {
    reset();
    set.clear();
    final String end = word.substring (first);

    if (analyse (end, set)) {
      final String start = word.substring (0, first);
      for (String s: set) {
        result.add (start + s);
      }
      return true;
    }
    return false;
  }


  private final Pattern[] pattern;
  private Set<String> set = new TreeSet<String>();
}

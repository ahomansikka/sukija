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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.util.RegexUtil;


public class RegexSuggestion2 extends Suggestion {
  public RegexSuggestion2 (Morphology morphology, List<String> input, String afterTest)
  {
    super (morphology);

    for (int i = 0; i < input.size(); i += 2) {
      pattern.add (RegexUtil.makePattern (input.get(i)));
      replacement.add (input.get(i+1));
    }

    this.afterTest = Pattern.compile (afterTest);
  }


  public boolean suggest (String word)
  {
    for (int i = 0; i < pattern.size(); i++) {
      if (suggest (i, word)) {
        set.clear();
        for (String s: result) {
          if (afterTest.matcher(s).find()) {
            set.add (s);
          }
        }
        if (set.size() > 0) {
          result.clear();
          result.addAll (set);
          return true;
        }
      }
    }
    return false;
  }


  public boolean suggest (int i, String word)
  {
    reset();
    Matcher m = pattern.get(i).matcher (word);

    int start = 0;

    while (m.find()) {
      sb.append (word.subSequence (start, m.start(1)));  // Append start of word.
      sb.append (replacement.get(i));                    // Append replacement.
      start = m.end (1);           // Continue searching after the previous match.
      found = true;
    }

    if (!found) return false;

    sb.append (word.subSequence (start, word.length())); // Append end of word.
    return analyse();
  }


  private Vector<Pattern> pattern = new Vector<Pattern>();
  private Vector<String> replacement = new Vector<String>();
  private Pattern afterTest;
  private Set<String> set = new TreeSet<String>();
}

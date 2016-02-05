/*
Copyright (©) 2014-2016 Hannu Väisänen

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
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import peltomaa.sukija.util.RegexUtil;
import peltomaa.sukija.voikko.VoikkoUtils;
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;


public class CompoundWordEndSuggestion extends Suggestion {
  /**
   * Muodostin.
   *
   * @param voikko Voikko.
   * @param pattern
   * @param end
   * @param addStart
   * @param addBaseFormOnly
   * @param addEnd
   */
  public CompoundWordEndSuggestion (Voikko voikko, Pattern[] pattern, String[] end,
                                    boolean addStart, boolean addBaseFormOnly, boolean addEnd)
  {
    super (voikko);
    this.pattern = pattern;
    this.end = end;
    this.addStart = addStart;
    this.addBaseFormOnly = addBaseFormOnly;
    this.addEnd = addEnd;
  }


  public boolean suggest (String word)
  {
    boolean found = false;

    for (int i = 0; i < pattern.length; i++) {
//System.out.println ("CompoundWordEndSuggestion " + pattern.get(i).pattern());
      Matcher matcher = pattern[i].matcher (word);
      if (matcher.find(1)) {
        result.clear();
        set.clear();

        if (VoikkoUtils.analyze (voikko, word.substring(matcher.start()), set)) {
          final String start = word.substring (0, matcher.start());
          if (addStart) addStart (start);
          if (addEnd) result.add (end[i]);
          for (String s: set) {
            if (s.endsWith (end[i])) {
//System.out.println ("Huu [" + word + "] [" + s + "] [" + start + "] [" + s + "] [" + (start+s) + "] [" + end[i] + "]");
              result.add (start + s);
              found = true; // Vielä ei voida palata, koska tuloksia on ehkä enemmän kuin yksi.
            }
          }
          if (found) return true;
//System.out.println (result.toString());
        }
      }
    }
    return false;
  }


  private void addStart (String s)
  {
    if (VoikkoUtils.analyze (voikko, s, startSet)) {
      for (String p: startSet) {
        addWithoutDash (p);
      }
    }
    else if (!addBaseFormOnly) {
      addWithoutDash (s);
    }
  }


  private void addWithoutDash (String s)
  {
    if (s.endsWith ("-")) {
      result.add (s.substring (0, s.length()-1));
    }
    else {
      result.add (s);
    }
  }


  private final Pattern[] pattern;
  private final String[] end;
  private Set<String> set = new HashSet<String>();
  private Set<String> startSet = new HashSet<String>();
  private final boolean addStart;
  private final boolean addBaseFormOnly;
  private final boolean addEnd;
}

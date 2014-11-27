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
import peltomaa.sukija.morphology.MorphologyException;
import peltomaa.sukija.util.RegexUtil;


public class CompoundWordEndSuggestion extends Suggestion {
  public CompoundWordEndSuggestion (Morphology morphology, String regex, String last)
  {
    super (morphology);
    pattern.add (RegexUtil.makePattern (regex));
    end.add (last);
  }


  public CompoundWordEndSuggestion (Morphology morphology, List<String> s)
  {
    super (morphology);
    for (int i = 0; i < s.size(); i += 2) {
      pattern.add (RegexUtil.makePattern (s.get(i)));
      end.add (s.get(i+1));
    }
  }


  public boolean suggest (String word)
  {
    for (int i = 0; i < pattern.size(); i++) {
      Matcher matcher = pattern.get(i).matcher (word);

      if (matcher.find()) {
        reset();
        set.clear();
        result.clear();
        if (analyse (word.substring(matcher.start()), set)) {
          final String start = word.substring (0, matcher.start());
          result.add (end.get(i));
          for (String s: set) {
            if (s.endsWith (end.get(i))) {
//System.out.println ("\n\nHuu [" + word + "] [" + s + "] [" + (start+s) + "] [" + end.get(i));
              result.add (start + s);
              found = true;
            }
          }
          return found;
        }
      }
    }
    return false;
  }


  private Vector<Pattern> pattern = new Vector<Pattern>();
  private Vector<String> end = new Vector<String>();
  private Set<String> set = new TreeSet<String>();
}

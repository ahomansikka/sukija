/*
Copyright (©) 2009-2011, 2013-2015 Hannu Väisänen

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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import peltomaa.sukija.util.RegexUtil;
import peltomaa.sukija.voikko.VoikkoUtils;
import org.puimula.libvoikko.Voikko;


/**
 * Replace regular expression with a string.<p>
 *
 * First capturing group of the regular expression is replaced. For example<p>
 * {@code RegexSuggestion (voikko, "^([0-9]+)", "");}<p>
 * deletes digits from the beginning of a word;
 * e.g. replacing {@code 123abc} with {@code abc}.
 */
public class RegexSuggestion extends Suggestion {
  public RegexSuggestion (Voikko voikko, String regex, String replacement)
  {
    super (voikko);
    this.pattern.add (RegexUtil.makePattern (regex));
    this.replacement.add (replacement);
  }


  public RegexSuggestion (Voikko voikko, List<String> input, boolean tryAll)
  {
    super (voikko);

    for (int i = 0; i < input.size(); i += 2) {
      this.pattern.add (RegexUtil.makePattern (input.get (i)));
      this.replacement.add (input.get (i+1));
    }
    this.tryAll = tryAll;
  }


  public boolean suggest (String word)
  {
    boolean success = false;
    result.clear();

    for (int i = 0; i < pattern.size(); i++) {
      if (suggest (i, word)) {
        result.addAll (set);
        if (tryAll) {
          success = true;
        }
        else {
          return true;
        }
      }
    }
    return success;
  }


  private boolean suggest (int i, String word)
  {
    boolean found = false;
    sb.delete (0, sb.length());
    Matcher m = pattern.get(i).matcher(word);

    while (m.find()) {
      m.appendReplacement (sb, replacement.get(i));
      found = true;
    }
    if (!found) return false;

    m.appendTail (sb);
    set.clear();
    return VoikkoUtils.analyze (voikko, sb.toString(), set);
  }

  private Vector<Pattern> pattern = new Vector<Pattern>();
  private Vector<String> replacement = new Vector<String>();
  private Set<String> set = new TreeSet<String>();
  private boolean tryAll;
  private StringBuffer sb = new StringBuffer (500);
}

/*
Copyright (©) 2009-2011, 2013-2016, 2022 Hannu Väisänen

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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.TreeSet;
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.util.RegexUtil;


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
    this.pattern = new Pattern[1];
    this.replacement = new String[1];
    this.pattern[0] = RegexUtil.makePattern (regex);
    this.replacement[0] = replacement;
    this.tryAll = true;
  }


  public RegexSuggestion (Voikko voikko, Pattern[] pattern, String[] replacement, boolean tryAll)
  {
    super (voikko);
    this.pattern = pattern;
    this.replacement = replacement;
    this.tryAll = tryAll;
  }


  @Override
  public boolean suggest (String word)
  {
    clearAnalysis();

    boolean success = false;

//System.out.println ("RegexSuggestion a [" + word + "]");

    for (int i = 0; i < pattern.length; i++) {
      if (suggest (i, word)) {
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
//System.out.println ("RegexSuggestion b " + i + " [" + word + "] [" + pattern[i].toString() + "]");

    final Matcher m = pattern[i].matcher(word);
    int start = 0;

    while (m.find (start)) {
//System.out.println ("RegexSuggestion c " + start);
      if (analyse (m, i, word)) {
        return true;
      }
      start = m.end();
    }
    return Xsuggest (i, word);
  }


  private boolean analyse (Matcher m, int i, String word)
  {
    sb.delete (0, sb.length());
    m.appendReplacement (sb, replacement[i]);
    m.appendTail (sb);
//System.out.println ("RegexSuggestion d " + i + " " + word + " " + replacement[i] + " [" + sb.toString() + "]");

    List<Analysis> a = voikko.analyze (sb.toString());
//System.out.println ("RegexSuggestion e " + a + " " + (a == null) + " " + a.size());
    addToAnalysis (a);
    return (a.size() > 0);
  }


  private boolean Xsuggest (int i, String word)
  {
    boolean found = false;
    sb.delete (0, sb.length());
    Matcher m = pattern[i].matcher(word);

    while (m.find()) {
      m.appendReplacement (sb, replacement[i]);
      found = true;
    }
    if (!found) return false;

    m.appendTail (sb);

    final List<Analysis> a = voikko.analyze (sb.toString());
    addToAnalysis (a);
    return (a.size() > 0);
  }

  private final Pattern[] pattern;
  private final String[] replacement;
  private final boolean tryAll;
  private StringBuffer sb = new StringBuffer (500);
}

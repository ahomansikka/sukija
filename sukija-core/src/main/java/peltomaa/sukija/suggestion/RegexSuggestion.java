/*
Copyright (©) 2009-2011 Hannu Väisänen

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
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.util.RegexUtil;


/**
 * Replace regular expression with a string.<p>
 *
 * First capturing group of the regular expression is replaced. For example<p>
 * {@code RegexSuggestion (morphology, "^([0-9]+)", "");}<p>
 * deletes digits from the beginning of a word;
 * e.g. replacing {@code 123abc} with {@code abc}.
 */
public class RegexSuggestion extends Suggestion {
  public RegexSuggestion (Morphology morphology, String regex, String replacement)
  {
    super (morphology);
    pattern = RegexUtil.makePattern (regex);
    this.replacement = replacement;
  }


  public boolean suggest (CharSequence word)
  {
    reset();
    Matcher m = pattern.matcher (word);
    int start = 0;

    while (m.find()) {
      MatchResult r = m.toMatchResult();
/*
System.out.println (sb.toString()
                    + " " + word.toString()
                    + " " + word.subSequence (start, m.start(1)).toString()
                    + " " + m.group(0)
                    + " " + m.group(1)
                    + " " + pattern.pattern()
                    + " [" + replacement
                    + "] " + start
                    + " " + m.start()
                    + " " + m.end()
                    + " " + m.start(1)
                    + " " + m.end(1));
*/
      sb.append (word.subSequence (start, m.start(1)));  // Append start of word.
      sb.append (replacement);                           // Append replacement.
      start = m.end (1);           // Continue searching after the previous match.
      found = true;
    }

    if (!found) return false;

    sb.append (word.subSequence (start, word.length())); // Append end of word.
    return analyse();
  }


  private Pattern pattern;
  private String replacement;
}

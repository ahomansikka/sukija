/*
Copyright (©) 2016 Hannu Väisänen

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
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.puimula.libvoikko.*;


public class VoikkoAttributeSuggestion extends Suggestion {
  public VoikkoAttributeSuggestion (Voikko voikko, List<String> input, String attribute, String regex, boolean tryAll)
  {
    super (voikko);
    this.pattern = makePattern (input);
    this.replacement = makeReplacement (input);
    this.attribute = attribute;
    this.regex = Pattern.compile (regex);
    this.tryAll = tryAll;
  }


  public boolean suggest (String word)
  {
    boolean success = false;
    result.clear();

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
    boolean success = false;
    Matcher m = pattern[i].matcher(word);

    if (m.find()) {
      final String u = m.replaceAll (replacement[i]);
      final List<Analysis> analysis = voikko.analyze (u);
      for (Analysis a: analysis) {
        if (ok (a)) {
          result.add (a.get("BASEFORM").toLowerCase());
          success = true;
        }
      }
    }
    return success;
  }


  private Pattern[] makePattern (List<String> input)
  {
    Pattern[] p = new Pattern[input.size()/2];
    for (int i = 0; i < input.size(); i += 2) {
      p[i] = Pattern.compile (input.get (i));
    }
    return p;
  }


  private String[] makeReplacement (List<String> input)
  {
    String[] s = new String[input.size()/2];
    for (int i = 0; i < input.size(); i += 2) {
      s[i] = input.get (i+1);
    }
    return s;
  }


  private boolean ok (Analysis analysis)
  {
    final String key = analysis.get (attribute);
    if (key != null) {
      return regex.matcher(key).find();
    }
    return false;
  }


  private final Pattern[] pattern;
  private final String[] replacement;
  private final String attribute;
  private final Pattern regex;
  private final boolean tryAll;
}

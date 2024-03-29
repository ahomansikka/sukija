/*
Copyright (©) 2017-2018, 2022 Hannu Väisänen

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
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;


public class VoikkoSpellingSuggestion extends Suggestion {
  public VoikkoSpellingSuggestion (Voikko voikko, int n)
  {
    super (voikko);
    this.n = n;
  }


  @Override
  public boolean suggest (String word)
  {
    clearAnalysis();
    boolean found = false;
    final List<String> list = voikko.suggest (word);

    for (int i = 0; i < Math.min (n, list.size()); i++) {
      final String s = list.get(i);
      if (s.indexOf(' ') >= 0) {
        for (String w : SPACES.split (list.get(i))) {
          final List<Analysis> result = voikko.analyze (w);
          if (result.size() > 0) {
            addToAnalysis (result);
            found = true;
          }
        }
      }
      else {
        final List<Analysis> result = voikko.analyze (s);
        if (result.size() > 0) {
          addToAnalysis (result);
          found = true;
        }
      }
    }
    return found;
  }


  private int n;
  private static final Pattern SPACES = Pattern.compile ("\\s+");
}

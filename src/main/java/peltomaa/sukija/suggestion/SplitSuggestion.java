/*
Copyright (©) 2017, 2022 Hannu Väisänen

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
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;


/**
Katkaistaan sana ja yritetään tunnistaa molemmat puolet erikseen.<p>
*/
public class SplitSuggestion extends Suggestion {
  public SplitSuggestion (Voikko voikko)
  {
    super (voikko);
  }


  @Override
  public boolean suggest (String word)
  {
    clearAnalysis();
    if (word.indexOf('-') >= 0) return false;

    for (int i = word.length()-2; i >= 2; i--) {
      if (spellOK (word.substring(0,i)) &&
          spellOK (word.substring(i))) {
//System.out.println ("HUUHAA " + word + " [" + word.substring(0,i) + "] [" + word.substring(i) + "]");
        return true;
      }
    }
    return false;
  }


  private boolean spellOK (String word)
  {
    final List<Analysis> list = voikko.analyze (word);
    if (list.size() > 0) {
      addToAnalysis (list);
      return true;
    }
    return false;
  }
}

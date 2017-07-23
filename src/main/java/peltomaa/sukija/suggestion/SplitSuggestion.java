/*
Copyright (©) 2017 Hannu Väisänen

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
import peltomaa.sukija.attributes.VoikkoAttribute;


/**
Katkaistaan sana ja yritetään tunnistaa molemmat puolet erikseen.<p>
*/
public class SplitSuggestion extends Suggestion {
  public SplitSuggestion (Voikko voikko)
  {
    super (voikko);
  }


  @Override
  public boolean suggest (String word, VoikkoAttribute voikkoAtt)
  {
    if (word.indexOf('-') >= 0) return false;

    for (int i = word.length()-2; i >= 2; i--) {
      if (spellOK (word.substring(0,i), voikkoAtt) &&
          spellOK (word.substring(i),   voikkoAtt)) {
//System.out.println ("HUUHAA " + word + " [" + word.substring(0,i) + "] [" + word.substring(i) + "]");
        return true;
      }
    }
    return false;
  }


  private boolean spellOK (String word, VoikkoAttribute voikkoAtt)
  {
    List<Analysis> list = voikko.analyze (word);
    if (list.size() > 0) {
      voikkoAtt.addAnalysis (list);
      return true;
    }
    return false;
  }


//  @Override
  public boolean xsuggest (String word, VoikkoAttribute voikkoAtt)
  {
    if (word.length() < 5) return false;

    List<String> suggestions = voikko.suggest (word);
    if (suggestions.size() == 0 || (suggestions.get(0).compareTo(word) == 0)) {
      return false;
    }

    boolean found = false;

    for (String s : suggestions) {
      if (s.indexOf(' ') > 0) {
        for (String p: s.split (" ")) {
          List<Analysis> list = voikko.analyze (p);
          if (list.size() > 0) {
System.out.println ("HUUHAA [" + word + "] s=[" + s + "] p=[" + p + "]");
            voikkoAtt.addAnalysis (list);
            found = true;
          }
        }
      }
    }
    return found;
  }
}

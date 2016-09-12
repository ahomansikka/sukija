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

import java.util.ArrayList;
import java.util.List;
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.voikko.VoikkoUtils;


public class StartSuggestion extends Suggestion {
  /**
   * Constructor.
   *
   * @param voikko  An instance of voikko.
   */
  public StartSuggestion (Voikko voikko, int minLength, int maxLength, boolean baseFormOnly, boolean hyphen, boolean tryAll)
  {
    super (voikko);
    this.minLength = Math.max (minLength, 0);
    this.maxLength = maxLength;
    this.baseFormOnly = baseFormOnly;
    this.hyphen = hyphen;
    this.tryAll = tryAll;
  }


  @Override
  public boolean suggest (String word, VoikkoAttribute voikkoAtt)
  {
    if (!hyphen && (word.indexOf ("-") >= 0)) {
//System.out.println ("DING   " + word);
      return false;
    }

//System.out.println ("HUUHAA " + hyphen + " " + word + " " + word.indexOf ("-"));
    List<Analysis> analysisList = new ArrayList<Analysis>();

    // Käydään läpi kaikki sanan alut pisimmästä alkaen.
    //
    for (int i = Math.min (maxLength, word.length()-1); i >= minLength; i--) {
      final String startOfWord = word.substring (0, i);
      List<Analysis> analysis = voikko.analyze (startOfWord);
      if (baseFormOnly) {
        for (Analysis a: analysis) {
          if (startOfWord.equals (a.get("BASEFORM").toLowerCase())) {
            analysisList.add (a);
          }
        }
        if (!tryAll && analysisList.size() > 0) {
          voikkoAtt.addAnalysis (analysisList);
          return true;
        }
      }
      else {
        if (analysis.size() > 0) {
          analysisList.addAll (analysis);
          if (!tryAll) {
            voikkoAtt.addAnalysis (analysisList);
            return true;
          }
        }
      }
    }
    voikkoAtt.addAnalysis (analysisList);
    return (analysisList.size() > 0);
  }


  private final int minLength;
  private final int maxLength;
  private final boolean baseFormOnly;
  private final boolean hyphen;
  private final boolean tryAll;
}

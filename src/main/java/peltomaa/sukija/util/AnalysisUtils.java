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

package peltomaa.sukija.util;

import java.util.List;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.attributes.BaseFormAttribute;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.util.CharCombinator;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionUtils;
import peltomaa.sukija.voikko.VoikkoUtils;


public final class AnalysisUtils {
  private AnalysisUtils() {}


  public static final boolean analyze (Voikko voikko, String word, VoikkoAttribute voikkoAtt,
                                       BaseFormAttribute baseFormAtt, FlagsAttribute flagsAtt)
  {
    List<Analysis> list = voikko.analyze (word);
    if (list.size() > 0) {
//      flagsAtt.setFlags (flagsAtt.getFlags() | Constants.FOUND);
      voikkoAtt.setAnalysis (list);
      baseFormAtt.addBaseForms (VoikkoUtils.getBaseForms (list));
      return true;
    }
    return false;
  }
}

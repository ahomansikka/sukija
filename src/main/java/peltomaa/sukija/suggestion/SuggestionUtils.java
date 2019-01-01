/*
Copyright (©) 2015-2016, 2018 Hannu Väisänen

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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.CharArraySet;
import org.puimula.libvoikko.*;
import peltomaa.sukija.attributes.BaseFormAttribute;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.util.AnalysisUtils;
import peltomaa.sukija.util.CharCombinator;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.voikko.VoikkoUtils;


public final class SuggestionUtils {
  private SuggestionUtils() {}


  public static final boolean getSuggestions (Suggestion[] suggestion, String word, VoikkoAttribute voikkoAtt,
                                              BaseFormAttribute baseFormAtt)
  {
    if (suggestion == null) throw new RuntimeException ("suggestion == null");
    for (int i = 0; i < suggestion.length; i++) {
//System.out.println ("Analyze5 " + word + " " + suggestion[i].getClass().getName());
      if (suggestion[i].suggest (word, voikkoAtt)) {
//System.out.println ("Analyze6 " + word);
        baseFormAtt.clear();
        Set<String> BF = suggestion[i].getExtraBaseForms();
        if (BF != null) {
          for (String s : BF) {
            baseFormAtt.addBaseForm (s.toLowerCase());
          }                                
        }                                
        baseFormAtt.addBaseForms (VoikkoUtils.getBaseForms (voikkoAtt.getAnalysis()));
        return true;
      }
    }
    return false;
  }


  public static final boolean getSuggestions (Suggestion[] suggestion, String word, VoikkoAttribute voikkoAtt,
                                              Set<String> baseForms)
  {
    if (suggestion == null) throw new RuntimeException ("suggestion == null");
    for (int i = 0; i < suggestion.length; i++) {
//System.out.println ("Analyze5 " + word + " " + suggestion[i].getClass().getName());
      if (suggestion[i].suggest (word, voikkoAtt)) {
//System.out.println ("Analyze6 " + word);
        baseForms.clear();
        Set<String> BF = suggestion[i].getExtraBaseForms();
        if (BF != null) {
          for (String s : BF) {
            baseForms.add (s.toLowerCase());
          }                                
        }                                
        baseForms.addAll (VoikkoUtils.getBaseForms (voikkoAtt.getAnalysis()));
//System.out.println ("Analyze7 " + word + " " + suggestion[i].getClass().getName() + "\n" + voikkoAtt.getAnalysis());
        return true;
      }
    }
    return false;
  }


  public static final boolean analyze (Voikko voikko, String word, VoikkoAttribute voikkoAtt,
                                       BaseFormAttribute baseFormAtt, FlagsAttribute flagsAtt,
                                       Suggestion[] suggestion, String from, String to)
  {
////System.out.println ("Analyze1 " + from + " " + to + " " + word);
    boolean suggestionResult = getSuggestions (suggestion, word, voikkoAtt, baseFormAtt);

    if (!suggestionResult) {
      if (from.length() > 0) {
////System.out.println ("Analyze2 " + from + " " + to + " " + word);
        suggestionResult = getSuggestions (voikko, word, voikkoAtt, baseFormAtt, flagsAtt, suggestion, from, to); 
      }
    }

////System.out.println ("AnalyzeÖ " + from + " " + to + " " + word + " " + suggestionResult);

    return suggestionResult;
  }


  private static final boolean getSuggestions
    (Voikko voikko, String word, VoikkoAttribute voikkoAtt,
     BaseFormAttribute baseFormAtt, FlagsAttribute flagsAtt,
     Suggestion[] suggestion, String from, String to)
  {
////System.out.println ("Analyze3 " + from + " " + to + " " + word);

    CharCombinator charCombinator = new CharCombinator (word, from, to);
    Iterator<String> iterator = charCombinator.iterator();

    while (iterator.hasNext()) {
      final String s = iterator.next();

      if (AnalysisUtils.analyze (voikko, s, voikkoAtt, baseFormAtt, flagsAtt)) {
////System.out.println ("Analyze4 " + from + " " + to + " " + word + " " + s);
        return true;
      }
      if (getSuggestions (suggestion, s, voikkoAtt, baseFormAtt)) {
////System.out.println ("Analyze9 " + from + " " + to + " " + word + " " + s);
        return true;
      }
    }
    return false;
  }
}

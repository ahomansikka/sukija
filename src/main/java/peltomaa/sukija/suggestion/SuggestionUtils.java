/*
Copyright (©) 2015-2016, 2018, 2021 Hannu Väisänen

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.puimula.libvoikko.*;
import peltomaa.sukija.attributes.BaseFormAttribute;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.util.AnalysisUtils;
import peltomaa.sukija.util.CharCombinator;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.suggestion.ahocorasick.AhoCorasickCorrector;


public final class SuggestionUtils {
  private SuggestionUtils() {}


  public static final boolean getSuggestions (Suggestion[] suggestion, String word, VoikkoAttribute voikkoAtt,
                                              BaseFormAttribute baseFormAtt)
  {
    if (suggestion == null) throw new RuntimeException ("suggestion == null");
    for (int i = 0; i < suggestion.length; i++) {
if (LOG.isDebugEnabled()) LOG.debug ("Analyze1 " + word + " " + suggestion[i].getClass().getName());
      if (suggestion[i].suggest (word, voikkoAtt)) {
if (LOG.isDebugEnabled()) LOG.debug ("Analyze2 " + word);
        baseFormAtt.clear();
        Set<String> BF = suggestion[i].getExtraBaseForms();
        if (BF != null) {
          for (String s : BF) {
            baseFormAtt.addBaseForm (s.toLowerCase());
          }                                
        }                                
        baseFormAtt.addBaseForms (AhoCorasickCorrector.getCorrections (voikkoAtt.getAnalysis()));
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
if (LOG.isDebugEnabled()) LOG.debug ("Analyze3 " + word + " " + suggestion[i].getClass().getName());
      if (suggestion[i].suggest (word, voikkoAtt)) {
if (LOG.isDebugEnabled()) LOG.debug ("Analyze4 " + word);
        baseForms.clear();
        Set<String> BF = suggestion[i].getExtraBaseForms();
        if (BF != null) {
          for (String s : BF) {
            baseForms.add (s.toLowerCase());
          }                                
        }                                
        baseForms.addAll (AhoCorasickCorrector.getCorrections (voikkoAtt.getAnalysis()));

if (LOG.isDebugEnabled()) LOG.debug ("Analyze5 " + word + " " + suggestion[i].getClass().getName() + "\n" + voikkoAtt.getAnalysis());
        return true;
      }
    }
    return false;
  }


  public static final boolean getSuggestions
    (Voikko voikko, String word, VoikkoAttribute voikkoAtt,
     BaseFormAttribute baseFormAtt, FlagsAttribute flagsAtt,
     Suggestion[] suggestion, String from, String to)
  {
if (LOG.isDebugEnabled()) LOG.debug ("Analyze6 " + from + " " + to + " " + word);

    CharCombinator charCombinator = new CharCombinator (word, from, to);
    Iterator<String> iterator = charCombinator.iterator();

    while (iterator.hasNext()) {
      final String s = iterator.next();
      if (AnalysisUtils.analyze (voikko, s, voikkoAtt, baseFormAtt, flagsAtt)) {
if (LOG.isDebugEnabled()) LOG.debug ("Analyze7 " + from + " " + to + " " + word + " " + s);
        return true;
      }
      if (getSuggestions (suggestion, s, voikkoAtt, baseFormAtt)) {
if (LOG.isDebugEnabled()) LOG.debug ("Analyze8 " + from + " " + to + " " + word + " " + s);
        return true;
      }
if (LOG.isDebugEnabled()) LOG.debug ("Analyze9 " + from + " " + to + " " + word + " " + s);
    }
    return false;
  }

  private static final Logger LOG = LoggerFactory.getLogger (SuggestionUtils.class);
}

/*
Copyright (©) 2015-2016, 2018, 2021-2022 Hannu Väisänen

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

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.puimula.libvoikko.*;
import peltomaa.sukija.attributes.BaseFormAttribute;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.suggestion.ahocorasick.AhoCorasickCorrector;
import peltomaa.sukija.util.CharCombinator;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.voikko.VoikkoUtils;


public final class SuggestionUtils {
  private SuggestionUtils() {}


  public static final boolean getSuggestions (Suggestion[] suggestion, String word, List<Analysis> list,
                                              BaseFormAttribute baseFormAtt, VoikkoAttribute voikkoAtt)
  {
    if (suggestion == null) throw new RuntimeException ("suggestion == null");
    list.clear();

    for (int i = 0; i < suggestion.length; i++) {
if (LOG.isDebugEnabled()) LOG.debug ("SUG1 " + word + " " + suggestion[i].getClass().getName());
      if (suggestion[i].suggest (word)) {
if (LOG.isDebugEnabled()) LOG.debug ("SUG2 " + word);
        list.addAll (suggestion[i].getAnalysis());
        voikkoAtt.setAnalysis (list);
        baseFormAtt.setBaseForms (VoikkoUtils.getBaseForms (list));
        if (suggestion[i].getExtraBaseForms() != null) baseFormAtt.addBaseFormsLowerCase (suggestion[i].getExtraBaseForms());
        return true;
      }
    }
if (LOG.isDebugEnabled()) LOG.debug ("SUG3 " + word);
    return false;
  }


  public static final boolean getSuggestions (Voikko voikko, Suggestion[] suggestion, String word, List<Analysis> list,
                                              BaseFormAttribute baseFormAtt, VoikkoAttribute voikkoAtt,
                                              String from, String to)
  {
    if (suggestion == null) throw new RuntimeException ("suggestion == null");

if (LOG.isDebugEnabled()) LOG.debug ("SUG4 " + word + " " + from + " " + to);

    final CharCombinator charCombinator = new CharCombinator (word, from, to);
    final Iterator<String> iterator = charCombinator.iterator();

    while (iterator.hasNext()) {
      final String s = iterator.next();
if (LOG.isDebugEnabled()) LOG.debug ("SUG5 " + word + " " + s);
      final List<Analysis> result = voikko.analyze (s);
      if (result.size() > 0) {
if (LOG.isDebugEnabled()) LOG.debug ("SUG6 " + word + " " + s);
        list.addAll (result);
        voikkoAtt.setAnalysis (list);
        baseFormAtt.setBaseForms (VoikkoUtils.getBaseForms (list));
        return true;
      }
      if (getSuggestions (suggestion, s, list, baseFormAtt, voikkoAtt)) {
if (LOG.isDebugEnabled()) LOG.debug ("SUG7 " + word + " " + s);
        return true;
      }
    }
if (LOG.isDebugEnabled()) LOG.debug ("SUG8 " + word);
    return false;
  }

/********************************************************************/


  public static final boolean getSuggestions (Suggestion[] suggestion, String word, List<Analysis> list, Set<String> extraBaseForms)
  {
    if (suggestion == null) throw new RuntimeException ("suggestion == null");
    list.clear();

    for (int i = 0; i < suggestion.length; i++) {
if (LOG.isDebugEnabled()) LOG.debug ("SUG1 " + word + " " + suggestion[i].getClass().getName());
      if (suggestion[i].suggest (word)) {
if (LOG.isDebugEnabled()) LOG.debug ("SUG2 " + word);
        list.addAll (suggestion[i].getAnalysis());
        if (suggestion[i].getExtraBaseForms() != null) extraBaseForms.addAll (suggestion[i].getExtraBaseForms());
        return true;
      }
    }
if (LOG.isDebugEnabled()) LOG.debug ("SUG3 " + word);
    return false;
  }


  public static final boolean getSuggestions (Voikko voikko, Suggestion[] suggestion, String word, List<Analysis> list,
                                              Set<String> extraBaseForms, String from, String to)
  {
    if (suggestion == null) throw new RuntimeException ("suggestion == null");

if (LOG.isDebugEnabled()) LOG.debug ("SUG4 " + word + " " + from + " " + to);

    list.clear();
    final CharCombinator charCombinator = new CharCombinator (word, from, to);
    final Iterator<String> iterator = charCombinator.iterator();

    while (iterator.hasNext()) {
      final String s = iterator.next();
if (LOG.isDebugEnabled()) LOG.debug ("SUG5 " + word + " " + s);
      final List<Analysis> result = voikko.analyze (s);
      if (result.size() > 0) {
if (LOG.isDebugEnabled()) LOG.debug ("SUG6 " + word + " " + s);
        list.addAll (result);
        return true;
      }
      if (getSuggestions (suggestion, s, list, extraBaseForms)) {
if (LOG.isDebugEnabled()) LOG.debug ("SUG7 " + word + " " + s);
        return true;
      }
    }
if (LOG.isDebugEnabled()) LOG.debug ("SUG8 " + word);
    return false;
  }


  private static final Logger LOG = LoggerFactory.getLogger (SuggestionUtils.class);
}

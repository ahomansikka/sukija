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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.util.RegexUtil;
import peltomaa.sukija.util.StringUtil;
import peltomaa.sukija.voikko.VoikkoUtils;
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;


public class CompoundWordEndSuggestion extends Suggestion {
  /**
   * Muodostin.
   *
   * @param voikko Voikko.
   * @param pattern
   * @param end
   * @param addStart
   * @param addBaseFormOnly
   * @param addEnd
   */
  public CompoundWordEndSuggestion (Voikko voikko, Pattern[] pattern, String[] end,
                                    boolean addStart, boolean addBaseFormOnly, boolean addEnd)
  {
    super (voikko);
    this.pattern = pattern;
    this.end = end;
    this.addStart = addStart;
    this.addBaseFormOnly = addBaseFormOnly;
    this.addEnd = addEnd;
  }


  public boolean suggest (String word, VoikkoAttribute voikkoAtt)
  {
//System.out.println ("CompoundWordEndSuggestion1 " + word);
    extraBaseForms.clear();
    boolean found = false;

    for (int i = 0; i < pattern.length; i++) {
//System.out.println ("CompoundWordEndSuggestion2 " + word + " " + pattern[i].pattern());
      Matcher matcher = pattern[i].matcher (word);
      if (matcher.find(1)) {
        List<Analysis> list = voikko.analyze (word.substring (matcher.start()));
        if (list.size() > 0) {
          String start = word.substring (0, matcher.start());

          for (Analysis a : list) {
            final String BASEFORM = a.get("BASEFORM").toLowerCase();
//System.out.println ("CompoundWordEndSuggestion3 " + word + " " + pattern[i].pattern() + " " + BASEFORM);

            if (BASEFORM.endsWith (end[i])) {
              if (addStart) addStart (start, voikkoAtt);
              if (addEnd) extraBaseForms.add (end[i]);

//System.out.println ("CompoundWordEndSuggestion4 [" + word + "] base=[" + BASEFORM + "] start=[" + start
//                    + "] start+base=[" + (start+BASEFORM) + "] end=[" + end[i] + "]");
              extraBaseForms.add (start + BASEFORM);
              final String DH = dehyphen (start, BASEFORM);
              if (DH != null) extraBaseForms.add (DH);
              found = true; // Vielä ei voida palata, koska tuloksia on ehkä enemmän kuin yksi.
            }
          }
          if (found) {
            voikkoAtt.addAnalysis (list);
//System.out.println ("CompoundWordEndSuggestion5 " + word + " " + pattern[i].pattern() + " " + VoikkoUtils.getBaseForms(voikkoAtt.getAnalysis()));
//System.out.println ("CompoundWordEndSuggestion6 " + word + " " + (voikkoAtt.getAnalysis() == null));
            return true;
          }
        }
      }
    }
    return false;
  }


  private void addStart (String start, VoikkoAttribute voikkoAtt)
  {
    final String s = (start.endsWith("-") ? start.substring(0,start.length()-1) : start);
    List<Analysis> analysis = voikko.analyze (s);
    if (analysis.size() > 0) {
      voikkoAtt.addAnalysis (analysis);
      for (int i = 0; i < analysis.size(); i++) {
        addWithoutDash (analysis.get(i).get("BASEFORM"));
      }
    }
    else if (!addBaseFormOnly) {
      addWithoutDash (s);
    }
  }


  private void addWithoutDash (String s)
  {
//System.out.println ("CompoundWordEndSuggestion7 " + s);
    if (s.endsWith ("-")) {
      extraBaseForms.add (s.substring (0, s.length()-1));
    }
    else {
      extraBaseForms.add (s);
    }
  }


  @Override
  public Set<String> getExtraBaseForms()
  {
    return extraBaseForms;
  }


  private String dehyphen (String start, String end)
  {
//System.out.println ("De " + start.charAt(start.length()-2) + " " + end.charAt(0)
//                    + " " + StringUtil.isVowel(end.charAt(0)));
    if (start.endsWith ("-") &&
        !(start.charAt(start.length()-2) == end.charAt(0) && StringUtil.isVowel(end.charAt(0)))) {
     return (start.substring (0, start.length()-1) + end);
    }
    else {
      return null;
    }
  }


  private final Pattern[] pattern;
  private final String[] end;
  private final boolean addStart;
  private final boolean addBaseFormOnly;
  private final boolean addEnd;
  private HashSet<String> extraBaseForms = new HashSet<String>();
}

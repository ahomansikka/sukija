/*
Copyright (©) 2014-2016, 2020 Hannu Väisänen

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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.util.RegexUtil;
import peltomaa.sukija.util.StringUtil;
import peltomaa.sukija.voikko.VoikkoUtils;
import org.ahocorasick.interval.*;
import org.ahocorasick.trie.*;
import org.ahocorasick.trie.handler.*;

import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;


public class CompoundWordEndSuggestion extends Suggestion {
  /**
   * Muodostin.
   *
   * @param voikko Voikko.
   * @param map
   * @param addStart
   * @param addBaseFormOnly
   * @param addEnd
   */
  public CompoundWordEndSuggestion (Voikko voikko, Map<String,String> map,
                                    boolean addStart, boolean addBaseFormOnly, boolean addEnd)
  {
    super (voikko);
    this.map = map;
//System.out.println (map.toString());
    Trie.TrieBuilder builder = Trie.builder();

    for (String s : this.map.keySet()) {
      builder.addKeyword (s);
    }
    trie = builder.build();


    this.addStart = addStart;
    this.addBaseFormOnly = addBaseFormOnly;
    this.addEnd = addEnd;
  }


  public boolean suggest (String word, VoikkoAttribute voikkoAtt)
  {
//System.out.println ("CompoundWordEndSuggestion1 " + word);
    extraBaseForms.clear();
    boolean found = false;

    sb.delete (0, sb.length());
    boolean hasToken = false;

    for (Token token : trie.tokenize (word)) {
//System.out.println ("CompoundWordEndSuggestion1 " + word + " " + token.getFragment());
      if (token.isMatch()) {
//System.out.println ("CompoundWordEndSuggestion2 " + word + " " + token.getFragment() + " " + word.substring (token.getEmit().getStart()));
        List<Analysis> list = voikko.analyze (word.substring (token.getEmit().getStart()));
        if (list.size() > 0) {
          String start = word.substring (0, token.getEmit().getStart());
          for (Analysis a : list) {
            final String BASEFORM = a.get("BASEFORM").toLowerCase();
//System.out.println ("CompoundWordEndSuggestion3 " + word + " " + token.getFragment() + " " + BASEFORM);

            final String END = map.get (token.getFragment());

            if (BASEFORM.endsWith (END)) {
              if (addStart) addStart (start, voikkoAtt);
              if (addEnd) extraBaseForms.add (END);

//System.out.println ("CompoundWordEndSuggestion4 " + word + " " + (start + BASEFORM));
              extraBaseForms.add (start + BASEFORM);
              final String DH = dehyphen (start, BASEFORM);
              if (DH != null) extraBaseForms.add (DH);
              found = true; // Vielä ei voida palata, koska tuloksia on ehkä enemmän kuin yksi.
            }
          }
          if (found) {
            voikkoAtt.addAnalysis (list);
//System.out.println ("CompoundWordEndSuggestion5 " + word + " " + token.getFragment()
//                    + " " + extraBaseForms.toString());
            return true;
          }
        }
      }
    }
    return false;
  }


  private void addStart (String start, VoikkoAttribute voikkoAtt)
  {
    final String s = (start.endsWith("-") ? END_DASHES.matcher(start).replaceAll("") : start);
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
      extraBaseForms.add (END_DASHES.matcher(s).replaceAll(""));
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
    
    if (start.endsWith ("-")) {
      final String s = END_DASHES.matcher(start).replaceAll("");
      if (!((s.charAt(s.length()-1) == end.charAt(0)) && StringUtil.isVowel(end.charAt(0)))) {
        return (s + end);
      }
    }
    return null;
  }


  private static final Pattern END_DASHES = Pattern.compile ("-+$");
  private StringBuilder sb = new StringBuilder (500);
  private Map<String,String> map;
  private Trie trie;

  private final boolean addStart;
  private final boolean addBaseFormOnly;
  private final boolean addEnd;
  private HashSet<String> extraBaseForms = new HashSet<String>();
}

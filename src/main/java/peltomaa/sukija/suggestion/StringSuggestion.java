/*
Copyright (©) 2016-2017 Hannu Väisänen

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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.ahocorasick.interval.*;
import org.ahocorasick.trie.*;
import org.ahocorasick.trie.handler.*;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.voikko.VoikkoUtils;


public class StringSuggestion extends Suggestion {
  public StringSuggestion (Voikko voikko, Map<String,String> map)
  {
    super (voikko);
    this.map = map;

    Trie.TrieBuilder builder = Trie.builder().ignoreOverlaps();

    for (String s : this.map.keySet()) {
      builder.addKeyword (s);
    }
    trie = builder.build();
  }


  @Override
  public boolean suggest (String word, VoikkoAttribute voikkoAtt)
  {
//System.out.println ("Bing Bong " + word);
    newWords.clear();
    sb.delete (0, sb.length());
    boolean hasToken = false;

    for (Token token : trie.tokenize (word)) {
      if (token.isMatch()) {
        final String replacement = map.get (token.getFragment());
//        sb.append ("{" + replacement + "}");
//System.out.println (token.getFragment() + " " + word.replace(token.getFragment(), replacement) + " " + sb.toString() + " X");
        sb.append (replacement);
        newWords.add (word.replace(token.getFragment(), replacement));
        hasToken = true;
      }
      else {
//        sb.append ("[" + token.getFragment() + "]");
        sb.append (token.getFragment());
      }
//      System.out.println (word + " " + sb.toString());
    }
    newWords.add (sb.toString());

    if (hasToken) {
//      System.out.println (newWords.toString());
      boolean found = false;
      for (String s : newWords) {
        if (analyze (s, voikkoAtt)) {
          found = true;
        }
      }
      if (found) {
//        System.out.println ("SUG " + word + " " + sb.toString() + " "
//                            + VoikkoUtils.getBaseForms(voikkoAtt.getAnalysis()));
        return true;
      }
    }
    return false;
  }


  private StringBuilder sb = new StringBuilder (500);
  private Map<String,String> map;
  private Trie trie;
  private Set<String> newWords = new HashSet<String>();
}

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

package peltomaa.sukija.suggestion;

import java.util.Map;
import peltomaa.sukija.voikko.VoikkoUtils;
import org.ahocorasick.interval.*;
import org.ahocorasick.trie.*;
import org.ahocorasick.trie.handler.*;
import org.puimula.libvoikko.Voikko;


public class StringSuggestion extends Suggestion {
  public StringSuggestion (Voikko voikko, Map<String,String> map)
  {
    super (voikko);
    this.map = map;

    Trie.TrieBuilder builder = trie.builder();

    for (String s : this.map.keySet()) {
      builder.addKeyword (s);
    }
    trie = builder.build();
  }


  @Override
  public boolean suggest (String word)
  {
    sb.delete (0, sb.length());
    boolean hasToken = false;

    for (Token token : trie.tokenize (word)) {
      if (token.isMatch()) {
        final String replacement = map.get (token.getFragment());
//        sb.append ("{" + replacement + "}");
        sb.append (replacement);
        hasToken = true;
      }
      else {
//        sb.append ("[" + token.getFragment() + "]");
        sb.append (token.getFragment());
      }
//      System.out.println (word + " " + sb.toString());
    }

    if (hasToken) {
      // Tämä kutsu tyhjentää muuttujan 'result' ennen
      // kuin tallentaa siihen analyysin tuloksen.
      //
      return VoikkoUtils.analyze (voikko, sb.toString(), result);
    }
    return false;
  }


  private StringBuilder sb = new StringBuilder (500);
  private Map<String,String> map;
  private Trie trie;
}

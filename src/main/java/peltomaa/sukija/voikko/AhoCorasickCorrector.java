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

package peltomaa.sukija.voikko;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.ahocorasick.interval.*;
import org.ahocorasick.trie.*;
import org.ahocorasick.trie.handler.*;
import org.puimula.libvoikko.Analysis;


class AhoCorasickCorrector {
  AhoCorasickCorrector()
  {
    Trie.TrieBuilder builder = Trie.builder().ignoreOverlaps();
    for (String s : MapClass.map.keySet()) {
      builder.addKeyword (s);
    }
    trie = builder.build();
  }


  public Set<String> getCorrections (List<Analysis> analysis)
  {
    Set<String> set = new HashSet<String>();

    for (Analysis a : analysis) {
      final String wb = a.get ("WORDBASES");
      final String baseForm = a.get ("BASEFORM");
      set.add (baseForm.toLowerCase());
      if (wb != null) {
//System.out.println ("Base0 " + wb + " " + baseForm);
        for (Token token : trie.tokenize (wb)) {
//System.out.println ("Base2 " + token.getFragment());
          if (token.isMatch()) {
//System.out.println ("Base3 " + token.getFragment());
            String[] s = MapClass.map.get (token.getFragment());
//System.out.println ("Base4 " + java.util.Arrays.asList(s).toString());
            for (int i = 1; i < s.length; i++) {
//System.out.println ("Base5 " + s[i]);
              if (baseForm.indexOf(s[i]) >= 0) {
                String bf = baseForm.replace (s[i], s[0]);
//System.out.println ("Base8 " + baseForm.indexOf(s[i]));
//System.out.println ("Base9 " + baseForm + " " + bf + " § " + wb);
                set.add (bf.toLowerCase());
              }
            }
          }
        }
      }
    }
    return set;
  }


  private Trie trie;
}

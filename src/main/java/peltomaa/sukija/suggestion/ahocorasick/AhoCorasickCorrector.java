/*
Copyright (©) 2016-2017, 2021, 2025 Hannu Väisänen

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

package peltomaa.sukija.suggestion.ahocorasick;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ahocorasick.interval.*;
import org.ahocorasick.trie.*;
import org.ahocorasick.trie.handler.*;
import org.puimula.libvoikko.Analysis;


public class AhoCorasickCorrector {
  private AhoCorasickCorrector() {}


  public Set<String> internalGetCorrections (List<Analysis> analysis)
  {
    final Set<String> set = new HashSet<String>();

    for (Analysis a : analysis) {set.add (a.get("BASEFORM").toLowerCase());} // Testi, mitä funktio tekee.

    for (Analysis a : analysis) {
      final String wb = a.get ("WORDBASES");
      final String baseForm = a.get ("BASEFORM");
      set.add (baseForm.toLowerCase());
      if (wb != null) {
if (LOG.isDebugEnabled()) LOG.debug ("Aho0 " + wb + " " + baseForm);
        for (PayloadToken token : trie.tokenize (wb)) {
if (LOG.isDebugEnabled()) LOG.debug ("Aho1 " + token.getFragment());
          if (token.isMatch()) {
if (LOG.isDebugEnabled()) LOG.debug ("Aho2 " + token.getFragment() + " " + wb + " " + baseForm);
            final String[] s = (String[])token.getEmit().getPayload();
if (LOG.isDebugEnabled()) LOG.debug ("Aho3 " + java.util.Arrays.asList(s).toString());
            for (int i = 1; i < s.length; i++) {
if (LOG.isDebugEnabled()) LOG.debug ("Aho4 " + s[i]);
              if (baseForm.indexOf(s[i]) >= 0) {
                final String bf = baseForm.replace (s[i], s[0]);
if (LOG.isDebugEnabled()) LOG.debug ("Aho5 " + baseForm + " " + bf + " § " + wb + " § " + token.getFragment() + " § " + baseForm.indexOf(s[i]));
                set.add (bf.toLowerCase());
              }
            }
          }
        }
      }
    }

if (LOG.isDebugEnabled()) LOG.debug ("Aho6 " + set.toString());
if (LOG.isDebugEnabled())for (String s : set) LOG.debug ("Aho7 " + s);

    return set;
  }

  private static final AhoCorasickCorrector aho = new AhoCorasickCorrector();

  public static final Set<String> getCorrections (List<Analysis> analysis)
  {
    return aho.internalGetCorrections (analysis);
  }

  private static PayloadTrie<String[]> trie = PayloadTrieClass.getPayloadTrie();
  private static final Logger LOG = LoggerFactory.getLogger (AhoCorasickCorrector.class);
}

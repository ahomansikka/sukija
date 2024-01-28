/*
Copyright (©) 2016-2017, 2021-2022 Hannu Väisänen

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ahocorasick.interval.*;
import org.ahocorasick.trie.*;
import org.ahocorasick.trie.handler.*;
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;


public class StringSuggestion extends Suggestion {
  public StringSuggestion (Voikko voikko, PayloadTrie<String> payloadTrie)
  {
    super (voikko);
    this.payloadTrie = payloadTrie;
  }


  @Override
  public boolean suggest (String word)
  {
    clearAnalysis();
    result.clear();

if (LOG.isDebugEnabled()) LOG.debug ("String0 " + word);

    newWords.clear();
    sb.delete (0, sb.length());
    boolean hasToken = false;

    for (PayloadToken<String> token : payloadTrie.tokenize (word)) {
if (LOG.isDebugEnabled()) LOG.debug ("String1 " + token.getFragment() + " " + sb.toString());
      if (token.isMatch()) {
        final String replacement = token.getEmit().getPayload();
//        sb.append ("{" + replacement + "}");
if (LOG.isDebugEnabled()) LOG.debug ("String2 " + token.getFragment() + " " + word.replace(token.getFragment(), replacement) + " " + sb.toString() + " X");
        sb.append (replacement);
        newWords.add (word.replace(token.getFragment(), replacement));
        hasToken = true;
      }
      else {
//        sb.append ("[" + token.getFragment() + "]");
        sb.append (token.getFragment());
if (LOG.isDebugEnabled()) LOG.debug ("String3 " + word + " " + sb.toString());
      }
if (LOG.isDebugEnabled()) LOG.debug ("String4 " + word + " " + sb.toString());
    }
if (LOG.isDebugEnabled()) LOG.debug ("String5 " + word + " " + sb.toString());
    newWords.add (sb.toString());

    if (hasToken) {
if (LOG.isDebugEnabled()) LOG.debug ("String6 "  + newWords.toString());
      boolean found = false;
      for (String s : newWords) {
if (LOG.isDebugEnabled()) LOG.debug ("String7 "  + newWords.toString() + " " + s);
        if (analyze (s)) {
if (LOG.isDebugEnabled()) LOG.debug ("String8 "  + newWords.toString() + " " + s);
if (LOG.isDebugEnabled()) for (Analysis a: getAnalysis()) LOG.debug ("     " + a.get("BASEFORM"));
          result.addAll (getAnalysis());
          found = true;
        }
      }
      if (found) {
        setAnalysis (result);
if (LOG.isDebugEnabled()) LOG.debug ("String9 " + word);
if (LOG.isDebugEnabled()) for (Analysis a: getAnalysis()) LOG.debug ("     " + a.get("BASEFORM"));
        return true;
      }
    }
if (LOG.isDebugEnabled()) LOG.debug ("StringÖ " + word + " " + sb.toString());
    return false;
  }


  private final StringBuilder sb = new StringBuilder (500);
  private final PayloadTrie<String> payloadTrie;
  private final Set<String> newWords = new HashSet<String>();
  private final List<Analysis> result = new ArrayList<Analysis>();
  private static final Logger LOG = LoggerFactory.getLogger (StringSuggestion.class);
}

/*
Copyright (©) 2021 Hannu Väisänen

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

package peltomaa.sukija.filters.ahocorasick;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.puimula.libvoikko.*;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.filters.HVTokenFilterBase;
import peltomaa.sukija.util.Constants;
import org.ahocorasick.interval.*;
import org.ahocorasick.trie.*;
import org.ahocorasick.trie.handler.*;

public class AhoCorasickFilter extends HVTokenFilterBase {
  public AhoCorasickFilter (TokenStream input)
  {
    super (input);
    flagsAtt = input.addAttribute (FlagsAttribute.class);
  }


  protected void decompose()
  {
//System.out.println ("TERM  " + termAtt.toString());
    final String originalTerm = termAtt.toString();
    final Set<String> set = new HashSet<String>();
    tokens.clear();

    for (PayloadToken<String> token : TrieClass.payloadTrie.tokenize (originalTerm)) {
//System.out.println ("Haa " + originalTerm);
      if (token.isMatch()) {
        final String s = token.getEmit().getPayload();
//System.out.println ("Hee " + s);
        final String r = originalTerm.replace (token.getFragment(), s);
//System.out.println ("Hii " + originalTerm + " " + s + " " + r);
        set.add (r.toLowerCase());
      }
    }

    final Iterator<String> i = set.iterator();
    while (i.hasNext()) {
      final String r = i.next();
//System.out.println ("BaseA " + r);
      if (r.compareTo(originalTerm) != 0) {
//System.out.println ("BaseB " + r);
        if (r.indexOf('-') > 0) {
          tokens.add (new NewToken (r, Constants.COMPOUND_WORD | flagsAtt.getFlags()));
        }
        else {
          tokens.add (new NewToken (r, Constants.WORD | flagsAtt.getFlags()));
        }
      }
    }
  }

  private VoikkoAttribute voikkoAtt;
  private FlagsAttribute flagsAtt;
}

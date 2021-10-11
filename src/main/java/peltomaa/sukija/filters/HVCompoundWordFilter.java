/*
Copyright (©) 2020 Hannu Väisänen

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

package peltomaa.sukija.filters;

import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.util.NgramUtils;
import peltomaa.sukija.util.StringUtil;
import peltomaa.sukija.attributes.OriginalWordAttribute;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.compound.CompoundWordTokenFilterBase;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;



/** Hajotetaan yhdysviivalliset yhdyssanat osiinsa ja yhdistetään
oikeinkirjoitussääntöjen mukaisesti ngrammeiksi. Esimerkiksi

<p>{@code linja-auto-opisto => linja-auto-opisto, linja-auto, auto-opisto, linja, auto, opisto}

<p>{@code kuu-kausi => kuukausi, kuu, kausi}
*/

public class HVCompoundWordFilter extends HVTokenFilterBase {

  public HVCompoundWordFilter (TokenStream input)
  {
    super (input);
//    super (input, CharArraySet.EMPTY_SET, 2, 2, 999, false);
  }


  @Override
  protected void decompose()
  {
//System.out.println ("NewToken0 " + termAtt.toString() + " " + Constants.toString(flagsAtt));
   if (Constants.hasAnyFlag (flagsAtt, Constants.COMPOUND_WORD, Constants.LATEX_COMPOUND_WORD)) {
      assert termAtt.toString().indexOf('-') > -1;
      assert termAtt.toString().indexOf(".-") == -1; // Ev.-lut. ei ole yhdyssana.
      Set<String> ngrams = NgramUtils.unique_ngram (termAtt.toString());
//for (String u : ngrams) System.out.println ("NewToken1  " + termAtt.toString() + " " + u);
      for (String u : ngrams) {
//System.out.println ("NewToken2 " + termAtt.toString() + " " + u);
        if (u.indexOf('-') > -1) {   // Jos ngrammi on yhdyssana, esim. linja-auto-opisto => linja-auto, auto-opisto, linja, auto, opisto.
          if (termAtt.toString().compareTo(u) != 0) {  // Jos ei ole alkuperäinen sana...
//System.out.println ("NewToken3 " +  u);
//            tokens.add (new NewToken (u, Constants.COMPOUND_WORD | Constants.NGRAM));  // Lisätään se.
            tokens.add (new NewToken (u, Constants.COMPOUND_WORD));  // Lisätään se.
          }
        }
        else {
//System.out.println ("NewToken2 " +  u);
//          tokens.add (new NewToken (u, Constants.WORD | Constants.NGRAM));
          tokens.add (new NewToken (u, Constants.WORD));
        }
      }
    }
  }

  private final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  private final FlagsAttribute flagsAtt = addAttribute (FlagsAttribute.class);
  private final OffsetAttribute offsetAtt = addAttribute (OffsetAttribute.class);
  private final OriginalWordAttribute originalWordAtt = addAttribute (OriginalWordAttribute.class);
  private final PositionIncrementAttribute posIncAtt = addAttribute (PositionIncrementAttribute.class);
}

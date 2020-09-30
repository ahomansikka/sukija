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
import peltomaa.sukija.util.StringUtil;


/** Hajotetaan yhdysviivalliset yhdyssanat osiinsa ja yhdistetään
oikeinkirjoitussääntöjen mukaisesti ngrammeiksi. Esimerkiksi

<p>{@code linja-auto-opisto => linja-auto-opisto, linja-auto, auto-opisto, linja, auto, opisto}

<p>{@code kuu-kausi => kuukausi, kuu, kausi}
*/
public class HVCompoundWordFilter extends HVTokenFilterBase {

  public HVCompoundWordFilter (TokenStream input)
  {
    super (input);
  }


  @Override
  protected void decompose()
  {
   if (Constants.hasFlag (flagsAtt, Constants.COMPOUND_WORD)) {
      assert termAtt.toString().indexOf('-') > -1;
      assert termAtt.toString().indexOf(".-") == -1; // Ev.-lut. ei ole yhdyssana.
      Set<String> ngrams = StringUtil.unique_ngram (termAtt.toString(), IS_COMPOUND_WORD, f);
      for (String u : ngrams) {
        if (u.indexOf('-') > -1) {   // Jos ngrammi on yhdyssana...
          if (termAtt.toString().compareTo(u) != 0) {  // Jos ei ole alkuperäinen sana...
            tokens.add (new NewToken (u, Constants.COMPOUND_WORD));  // Lisätään se.
          }
        }
        else {
          tokens.add (new NewToken (u, Constants.WORD));
        }
      }
    }
  }


  /** Asetetaan tarvittaessa yhdysviiva ngrammien väliin: 'linja', 'auto' => 'linja-auto',
   *  mutta: 'kuu', 'kausi' => 'kuukausi'.
   */
  private static class F implements BiFunction<CharSequence,CharSequence,String> {
    @Override
    public String apply (CharSequence u, CharSequence v)
    {
      if ((u.charAt(u.length()-1) == v.charAt(0)) && StringUtil.isVowel(v.charAt(0))) {
        return "-";
      }
      else {
        return "";
      }
    }
  }


  private static final F f = new F();
  private static final Pattern IS_COMPOUND_WORD = Pattern.compile ("-");
}

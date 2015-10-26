/*
Copyright (©) 2014-2015 Hannu Väisänen

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

package peltomaa.sukija.hyphen;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import peltomaa.sukija.util.Constants;


/**
Poistetaan sanasta tarpeettomat yhdysviivat.<p>

Esimerkiksi "poissa-olo" => poissaolo, mutta "linja-auto" => linja-auto.<p>

Lisäksi sana katkaistaan yhdysviivan kohdalta.<p>

Esimerkiksi "poissa-olo" => poissa-olo, poissaolo, poissa, olo<p>
"linja-auto" => linja-auto, linja, auto<p>
"Maija-niminen" => Maija-niminen, Maijaniminen, Maija, niminen

*/
public final class HyphenFilter extends TokenFilter {
  public HyphenFilter (TokenStream in, String regex, String replacement)
  {
    this (in, Pattern.compile(regex), replacement);
  }


  public HyphenFilter (TokenStream in, Pattern regex, String replacement)
  {
    super (in);
    this.pattern = regex;
    this.replacement = replacement;
    if (LOG.isDebugEnabled()) LOG.debug ("Tehdään luokka " + getClass().getName() + ".");
  }


  public HyphenFilter (TokenStream in)
  {
    this (in, Constants.HYPHEN_REGEX, "-");
  }


  @Override
  public final boolean incrementToken() throws IOException
  {
    if (iterator == null || !iterator.hasNext()) {
      if (!input.incrementToken()) {
         return false;
      }
      v.clear();
      final String word = termAtt.toString();
      v.add (word);

      if (Constants.hasFlag (flagsAtt, Constants.HYPHEN)) {
        final String[] s = pattern.split (word);

        final String w = pattern.matcher(word).replaceAll(replacement);
        v.addAll (Arrays.asList(s));
        if (!word.equals(w)) v.add (w);
        final String h = dehyphen (s);
        if (h != null) v.add (h);
        positionIncrement = 1;
 //       System.out.println ("HyphenFilter " + word + " " + v.toString());
      }
      iterator = v.iterator();
    }
    if (iterator.hasNext()) {
      final String word = iterator.next();
      if (!pattern.matcher(word).find()) {
        flagsAtt.setFlags (Constants.WORD);
//System.out.println ("HyphenFilter1 " + word);
      }
      termAtt.setEmpty().append (word);
      posIncrAtt.setPositionIncrement (positionIncrement);
/*
System.out.println ("HyphenFilter2 " + termAtt.toString()
                    + " " + Constants.toString(flagsAtt)
                    + " " + offsetAtt.startOffset()
                    + " " + offsetAtt.endOffset());
*/
      positionIncrement = 0;
    }
    return true;
  }


  /** Poistetaan sanasta tarpeettomat väliviivat.
   *  "Linja-auto" säilyy, mutta "poissa-olosta" tulee "poissaolosta".
   */
  private String dehyphen (String[] s)
  {
    boolean flag = false;

    sb.delete (0, sb.length());
    for (int i = 0; i < s.length; i++) {
      sb.append (s[i]);
      if (i < s.length-1) {
        if ((s[i].charAt(s[i].length()-1) == s[i+1].charAt(0)) && isVowel(s[i+1].charAt(0))) {
          sb.append (replacement);
//System.out.println ("Blong " + i + " " + sb.toString());
        }
        else {
//System.out.println ("Bling " + i + " " + sb.toString());
          flag = true;
        }
      }
    }
    return (flag ? sb.toString() : null);
  }


  private boolean isVowel (char c)
  {
    switch (c) {
      case 'a':
      case 'e':
      case 'i':
      case 'o':
      case 'u':
      case 'y':
      case 'ä':
      case 'ö':
        return true;
      default:
        return false;
    }
  }


  private int positionIncrement = 1;
  private State savedState;
  private final FlagsAttribute flagsAtt = addAttribute (FlagsAttribute.class);
  private final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  private final OffsetAttribute offsetAtt = addAttribute (OffsetAttribute.class);
  private final PositionIncrementAttribute posIncrAtt = addAttribute (PositionIncrementAttribute.class);
  private final Pattern pattern;
  private final String replacement;
  private Vector<String> v = new Vector<String>();
  private StringBuilder sb = new StringBuilder(500);
  private Iterator<String> iterator;
  private static final Logger LOG = LoggerFactory.getLogger (HyphenFilter.class);
}

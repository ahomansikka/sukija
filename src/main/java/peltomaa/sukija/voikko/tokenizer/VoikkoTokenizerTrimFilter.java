/*
Copyright (©) 2015 Hannu Väisänen

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


package peltomaa.sukija.voikko.tokenizer;


import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


/** Poistetaan merkkijonon alusta tai lopusta merkit,
 *  jotka eivät ole numeroita eivätkä kirjaimia.
 */
public final class VoikkoTokenizerTrimFilter extends TokenFilter {

  /** Tehdään uusi {@link VoikkoTokenizerTrimFilter}.
   *
   *  @param input  Trimmattavat symbolit.
   */
  public VoikkoTokenizerTrimFilter (TokenStream input)
  {
    super (input);
  }

  @Override
  public boolean incrementToken() throws IOException
  {
    if (!input.incrementToken()) return false;

    char[] termBuffer = termAtt.buffer();
    int len = termAtt.length();

    if (len == 0) return true;

    int start = 0;
    int end = 0;

    // Poistetaan ylimääräiset merkit alusta ja lopusta.
    //
    for (start = 0; start < len && !Character.isLetterOrDigit (termBuffer[start]); start++)
    {
    }
    for (end = len; end >= start && !Character.isLetterOrDigit (termBuffer[end - 1]); end--)
    {
    }

    if (start > 0 || end < len) {
      if (start < end) {
        termAtt.copyBuffer (termBuffer, start, (end - start));
      }
      else {
        termAtt.setEmpty();
      }
    }

    return true;
  }

  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
}

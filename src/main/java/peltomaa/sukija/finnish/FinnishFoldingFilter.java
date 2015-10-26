/*
Copyright (©) 2012-2013 Hannu Väisänen

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

package peltomaa.sukija.finnish;

import java.io.IOException;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


/**
 * This class converts Unicode characters to Finnish equivalents, if one exists.
 * The result is the same than ASCIIFoldingFilter except that letters
 * ÅÄÖåäö are not converted and 
 * 'Ü' and 'Ű' are converted to 'Y',
 * 'ü' and 'ű' are converted to 'y',
 * 'W' and its accented forms are converted to 'V',
 * 'w' and its accented forms are converted to 'v',
 * 'Ø', 'Õ' and 'Ő' are converted to 'Ö', and
 * 'ø', 'õ' and 'ő' are converted to 'ö'.
 */
public final class FinnishFoldingFilter extends TokenFilter {
  public FinnishFoldingFilter (TokenStream input)
  {
    super (input);
  }


  @Override
  public boolean incrementToken() throws IOException
  {
    if (input.incrementToken()) {
      boolean changed = false;
      int outputPos = 0;
      final char[] buffer = termAtt.buffer();
      final int length = termAtt.length();
      final int maxSizeNeeded = 4 * length;

      if (output.length < maxSizeNeeded) {
        output = new char[maxSizeNeeded];
      }
      for (int i = 0; i < length; i++) {
        final char c = buffer[i];
        switch (c) {
          case 'Ü':
          case 'Ű':
            output[outputPos++] = 'Y';
            changed = true;
            break;
          case 'ü':
          case 'ű':
            output[outputPos++] = 'y';
            changed = true;
            break;
          case 'W':      // Copied from ASCIIFoldingFilter.foldToASCII.
          case '\u0174': // Ŵ  [LATIN CAPITAL LETTER W WITH CIRCUMFLEX]
          case '\u01F7': // Ƿ  http://en.wikipedia.org/wiki/Wynn  [LATIN CAPITAL LETTER WYNN]
          case '\u1D21': // ᴡ  [LATIN LETTER SMALL CAPITAL W]
          case '\u1E80': // Ẁ  [LATIN CAPITAL LETTER W WITH GRAVE]
          case '\u1E82': // Ẃ  [LATIN CAPITAL LETTER W WITH ACUTE]
          case '\u1E84': // Ẅ  [LATIN CAPITAL LETTER W WITH DIAERESIS]
          case '\u1E86': // Ẇ  [LATIN CAPITAL LETTER W WITH DOT ABOVE]
          case '\u1E88': // Ẉ  [LATIN CAPITAL LETTER W WITH DOT BELOW]
          case '\u24CC': // Ⓦ  [CIRCLED LATIN CAPITAL LETTER W]
          case '\u2C72': // <U+2C72>  [LATIN CAPITAL LETTER W WITH HOOK]
          case '\uFF37': // Ｗ  [FULLWIDTH LATIN CAPITAL LETTER W]
            output[outputPos++] = 'V';
            changed = true;
            break;
          case 'w':      // Copied from ASCIIFoldingFilter.foldToASCII.
          case '\u0175': // ŵ  [LATIN SMALL LETTER W WITH CIRCUMFLEX]
          case '\u01BF': // ƿ  http://en.wikipedia.org/wiki/Wynn  [LATIN LETTER WYNN]
          case '\u028D': // ʍ  [LATIN SMALL LETTER TURNED W]
          case '\u1E81': // ẁ  [LATIN SMALL LETTER W WITH GRAVE]
          case '\u1E83': // ẃ  [LATIN SMALL LETTER W WITH ACUTE]
          case '\u1E85': // ẅ  [LATIN SMALL LETTER W WITH DIAERESIS]
          case '\u1E87': // ẇ  [LATIN SMALL LETTER W WITH DOT ABOVE]
          case '\u1E89': // ẉ  [LATIN SMALL LETTER W WITH DOT BELOW]
          case '\u1E98': // ẘ  [LATIN SMALL LETTER W WITH RING ABOVE]
          case '\u24E6': // ⓦ  [CIRCLED LATIN SMALL LETTER W]
          case '\u2C73': // <U+2C73>  [LATIN SMALL LETTER W WITH HOOK]
          case '\uFF57': // ｗ  [FULLWIDTH LATIN SMALL LETTER W]
            output[outputPos++] = 'v';
            changed = true;
            break;
          case 'Å':
          case 'Ä':
          case 'Ö':
          case 'å':
          case 'ä':
          case 'ö':
            output[outputPos++] = c;
            break;
          case 'Ø':
          case 'Õ':  // Viron kielen Õ.
          case 'Ő':
            output[outputPos++] = 'Ö';
            changed = true;
            break;
          case 'ø':
          case 'õ':  // Viron kielen õ.
          case 'ő':
            output[outputPos++] = 'ö';
            changed = true;
            break;
          default:
            if (c < '\u0080') {
              output[outputPos++] = c;
            }
            else {
              outputPos = ASCIIFoldingFilter.foldToASCII (buffer, i, output, outputPos, 1);
              changed = true;
            }
            break;
        }
      }
      if (changed) {
        termAtt.copyBuffer (output, 0, outputPos);
      }
      return true;
    }
    else {
      return false;
    }
  }

  private char[] output = new char[512];
  private final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
}

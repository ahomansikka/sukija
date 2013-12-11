/*
Copyright (©) 2013 Hannu Väisänen

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
 * This is a combination of FinnishFoldigFilter and Lucene's LowerCaseFilter,
 * but only letters in the following two Unicode blocks are converted to lowercase.
 * Finnish needs only these blocks. :-)
 *
 * <ul>
 *   <li>C1 Controls and Latin-1 Supplement: <a href="http://www.unicode.org/charts/PDF/U0080.pdf">http://www.unicode.org/charts/PDF/U0080.pdf</a>
 *   <li>Latin Extended-A: <a href="http://www.unicode.org/charts/PDF/U0100.pdf">http://www.unicode.org/charts/PDF/U0100.pdf</a>
 * </ul>
 */
public final class FinnishFoldingLowerCaseFilter extends TokenFilter {
  public FinnishFoldingLowerCaseFilter (TokenStream input)
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
          case 'A': output[outputPos++] = 'a'; changed = true; break;
          case 'B': output[outputPos++] = 'b'; changed = true; break;
          case 'C': output[outputPos++] = 'c'; changed = true; break;
          case 'D': output[outputPos++] = 'd'; changed = true; break;
          case 'E': output[outputPos++] = 'e'; changed = true; break;
          case 'F': output[outputPos++] = 'f'; changed = true; break;
          case 'G': output[outputPos++] = 'g'; changed = true; break;
          case 'H': output[outputPos++] = 'h'; changed = true; break;
          case 'I': output[outputPos++] = 'i'; changed = true; break;
          case 'J': output[outputPos++] = 'j'; changed = true; break;
          case 'K': output[outputPos++] = 'k'; changed = true; break;
          case 'L': output[outputPos++] = 'l'; changed = true; break;
          case 'M': output[outputPos++] = 'm'; changed = true; break;
          case 'N': output[outputPos++] = 'n'; changed = true; break;
          case 'O': output[outputPos++] = 'o'; changed = true; break;
          case 'P': output[outputPos++] = 'p'; changed = true; break;
          case 'Q': output[outputPos++] = 'q'; changed = true; break;
          case 'R': output[outputPos++] = 'r'; changed = true; break;
          case 'S': output[outputPos++] = 's'; changed = true; break;
          case 'T': output[outputPos++] = 't'; changed = true; break;
          case 'U': output[outputPos++] = 'u'; changed = true; break;
          case 'V': output[outputPos++] = 'v'; changed = true; break;
          case 'W': output[outputPos++] = 'v'; changed = true; break;
          case 'X': output[outputPos++] = 'x'; changed = true; break;
          case 'Y': output[outputPos++] = 'y'; changed = true; break;
          case 'Z': output[outputPos++] = 'z'; changed = true; break;

          case 'À': output[outputPos++] = 'a'; changed = true; break;
          case 'Á': output[outputPos++] = 'a'; changed = true; break;
          case 'Â': output[outputPos++] = 'a'; changed = true; break;
          case 'Ã': output[outputPos++] = 'a'; changed = true; break;
          case 'Ä': output[outputPos++] = 'ä'; changed = true; break;
          case 'Å': output[outputPos++] = 'å'; changed = true; break;
          case 'Æ': output[outputPos++] = 'a'; output[outputPos++] = 'e'; changed = true; break;
          case 'Ç': output[outputPos++] = 'c'; changed = true; break;
          case 'È': output[outputPos++] = 'e'; changed = true; break;
          case 'É': output[outputPos++] = 'e'; changed = true; break;
          case 'Ê': output[outputPos++] = 'e'; changed = true; break;
          case 'Ë': output[outputPos++] = 'e'; changed = true; break;
          case 'Ì': output[outputPos++] = 'i'; changed = true; break;
          case 'Í': output[outputPos++] = 'i'; changed = true; break;
          case 'Î': output[outputPos++] = 'i'; changed = true; break;
          case 'Ï': output[outputPos++] = 'i'; changed = true; break;
          case 'Ð': output[outputPos++] = 'd'; changed = true; break;
          case 'Ñ': output[outputPos++] = 'n'; changed = true; break;
          case 'Ò': output[outputPos++] = 'o'; changed = true; break;
          case 'Ó': output[outputPos++] = 'o'; changed = true; break;
          case 'Ô': output[outputPos++] = 'o'; changed = true; break;
          case 'Õ': output[outputPos++] = 'ö'; changed = true; break;
          case 'Ö': output[outputPos++] = 'ö'; changed = true; break;
          case 'Ø': output[outputPos++] = 'ö'; changed = true; break;
          case 'Ù': output[outputPos++] = 'u'; changed = true; break;
          case 'Ú': output[outputPos++] = 'u'; changed = true; break;
          case 'Û': output[outputPos++] = 'u'; changed = true; break;
          case 'Ü': output[outputPos++] = 'y'; changed = true; break;
          case 'Ý': output[outputPos++] = 'y'; changed = true; break;
          case 'Þ': output[outputPos++] = 't'; output[outputPos++] = 'h'; changed = true; break;
          case 'ß': output[outputPos++] = 's'; output[outputPos++] = 's'; changed = true; break;
          case 'à': output[outputPos++] = 'a'; changed = true; break;
          case 'á': output[outputPos++] = 'a'; changed = true; break;
          case 'â': output[outputPos++] = 'a'; changed = true; break;
          case 'ã': output[outputPos++] = 'a'; changed = true; break;
          case 'ä': output[outputPos++] = 'ä'; changed = true; break;
          case 'å': output[outputPos++] = 'å'; changed = true; break;
          case 'æ': output[outputPos++] = 'a'; output[outputPos++] = 'e'; changed = true; break;
          case 'ç': output[outputPos++] = 'c'; changed = true; break;
          case 'è': output[outputPos++] = 'e'; changed = true; break;
          case 'é': output[outputPos++] = 'e'; changed = true; break;
          case 'ê': output[outputPos++] = 'e'; changed = true; break;
          case 'ë': output[outputPos++] = 'e'; changed = true; break;
          case 'ì': output[outputPos++] = 'i'; changed = true; break;
          case 'í': output[outputPos++] = 'i'; changed = true; break;
          case 'î': output[outputPos++] = 'i'; changed = true; break;
          case 'ï': output[outputPos++] = 'i'; changed = true; break;
          case 'ð': output[outputPos++] = 'd'; changed = true; break;
          case 'ñ': output[outputPos++] = 'n'; changed = true; break;
          case 'ò': output[outputPos++] = 'o'; changed = true; break;
          case 'ó': output[outputPos++] = 'o'; changed = true; break;
          case 'ô': output[outputPos++] = 'o'; changed = true; break;
          case 'õ': output[outputPos++] = 'ö'; changed = true; break;
          case 'ö': output[outputPos++] = 'ö'; changed = true; break;
          case 'ø': output[outputPos++] = 'ö'; changed = true; break;
          case 'ù': output[outputPos++] = 'u'; changed = true; break;
          case 'ú': output[outputPos++] = 'u'; changed = true; break;
          case 'û': output[outputPos++] = 'u'; changed = true; break;
          case 'ü': output[outputPos++] = 'y'; changed = true; break;
          case 'ý': output[outputPos++] = 'y'; changed = true; break;
          case 'þ': output[outputPos++] = 't'; output[outputPos++] = 'h'; changed = true; break;
          case 'ÿ': output[outputPos++] = 'y'; changed = true; break;
          case 'Ā': output[outputPos++] = 'a'; changed = true; break;
          case 'ā': output[outputPos++] = 'a'; changed = true; break;
          case 'Ă': output[outputPos++] = 'a'; changed = true; break;
          case 'ă': output[outputPos++] = 'a'; changed = true; break;
          case 'Ą': output[outputPos++] = 'a'; changed = true; break;
          case 'ą': output[outputPos++] = 'a'; changed = true; break;
          case 'Ć': output[outputPos++] = 'c'; changed = true; break;
          case 'ć': output[outputPos++] = 'c'; changed = true; break;
          case 'Ĉ': output[outputPos++] = 'c'; changed = true; break;
          case 'ĉ': output[outputPos++] = 'c'; changed = true; break;
          case 'Ċ': output[outputPos++] = 'c'; changed = true; break;
          case 'ċ': output[outputPos++] = 'c'; changed = true; break;
          case 'Č': output[outputPos++] = 'c'; changed = true; break;
          case 'č': output[outputPos++] = 'c'; changed = true; break;
          case 'Ď': output[outputPos++] = 'd'; changed = true; break;
          case 'ď': output[outputPos++] = 'd'; changed = true; break;
          case 'Đ': output[outputPos++] = 'd'; changed = true; break;
          case 'đ': output[outputPos++] = 'd'; changed = true; break;
          case 'Ē': output[outputPos++] = 'e'; changed = true; break;
          case 'ē': output[outputPos++] = 'e'; changed = true; break;
          case 'Ĕ': output[outputPos++] = 'e'; changed = true; break;
          case 'ĕ': output[outputPos++] = 'e'; changed = true; break;
          case 'Ė': output[outputPos++] = 'e'; changed = true; break;
          case 'ė': output[outputPos++] = 'e'; changed = true; break;
          case 'Ę': output[outputPos++] = 'e'; changed = true; break;
          case 'ę': output[outputPos++] = 'e'; changed = true; break;
          case 'Ě': output[outputPos++] = 'e'; changed = true; break;
          case 'ě': output[outputPos++] = 'e'; changed = true; break;
          case 'Ĝ': output[outputPos++] = 'g'; changed = true; break;
          case 'ĝ': output[outputPos++] = 'g'; changed = true; break;
          case 'Ğ': output[outputPos++] = 'g'; changed = true; break;
          case 'ğ': output[outputPos++] = 'g'; changed = true; break;
          case 'Ġ': output[outputPos++] = 'g'; changed = true; break;
          case 'ġ': output[outputPos++] = 'g'; changed = true; break;
          case 'Ģ': output[outputPos++] = 'g'; changed = true; break;
          case 'ģ': output[outputPos++] = 'g'; changed = true; break;
          case 'Ĥ': output[outputPos++] = 'h'; changed = true; break;
          case 'ĥ': output[outputPos++] = 'h'; changed = true; break;
          case 'Ħ': output[outputPos++] = 'h'; changed = true; break;
          case 'ħ': output[outputPos++] = 'h'; changed = true; break;
          case 'Ĩ': output[outputPos++] = 'i'; changed = true; break;
          case 'ĩ': output[outputPos++] = 'i'; changed = true; break;
          case 'Ī': output[outputPos++] = 'i'; changed = true; break;
          case 'ī': output[outputPos++] = 'i'; changed = true; break;
          case 'Ĭ': output[outputPos++] = 'i'; changed = true; break;
          case 'ĭ': output[outputPos++] = 'i'; changed = true; break;
          case 'Į': output[outputPos++] = 'i'; changed = true; break;
          case 'į': output[outputPos++] = 'i'; changed = true; break;
          case 'İ': output[outputPos++] = 'i'; changed = true; break;
          case 'ı': output[outputPos++] = 'i'; changed = true; break;
          case 'Ĳ': output[outputPos++] = 'i'; output[outputPos++] = 'j'; changed = true; break;
          case 'ĳ': output[outputPos++] = 'i'; output[outputPos++] = 'j'; changed = true; break;
          case 'Ĵ': output[outputPos++] = 'j'; changed = true; break;
          case 'ĵ': output[outputPos++] = 'j'; changed = true; break;
          case 'Ķ': output[outputPos++] = 'k'; changed = true; break;
          case 'ķ': output[outputPos++] = 'k'; changed = true; break;
          case 'Ĺ': output[outputPos++] = 'l'; changed = true; break;
          case 'ĺ': output[outputPos++] = 'l'; changed = true; break;
          case 'Ļ': output[outputPos++] = 'l'; changed = true; break;
          case 'ļ': output[outputPos++] = 'l'; changed = true; break;
          case 'Ľ': output[outputPos++] = 'l'; changed = true; break;
          case 'ľ': output[outputPos++] = 'l'; changed = true; break;
          case 'Ŀ': output[outputPos++] = 'l'; changed = true; break;
          case 'ŀ': output[outputPos++] = 'l'; changed = true; break;
          case 'Ł': output[outputPos++] = 'l'; changed = true; break;
          case 'ł': output[outputPos++] = 'l'; changed = true; break;
          case 'Ń': output[outputPos++] = 'n'; changed = true; break;
          case 'ń': output[outputPos++] = 'n'; changed = true; break;
          case 'Ņ': output[outputPos++] = 'n'; changed = true; break;
          case 'ņ': output[outputPos++] = 'n'; changed = true; break;
          case 'Ň': output[outputPos++] = 'n'; changed = true; break;
          case 'ň': output[outputPos++] = 'n'; changed = true; break;
          case 'Ŋ': output[outputPos++] = 'n'; changed = true; break;
          case 'ŋ': output[outputPos++] = 'n'; changed = true; break;
          case 'Ō': output[outputPos++] = 'o'; changed = true; break;
          case 'ō': output[outputPos++] = 'o'; changed = true; break;
          case 'Ŏ': output[outputPos++] = 'o'; changed = true; break;
          case 'ŏ': output[outputPos++] = 'o'; changed = true; break;
          case 'Ő': output[outputPos++] = 'ö'; changed = true; break;
          case 'ő': output[outputPos++] = 'ö'; changed = true; break;
          case 'Œ': output[outputPos++] = 'o'; output[outputPos++] = 'e'; changed = true; break;
          case 'œ': output[outputPos++] = 'o'; output[outputPos++] = 'e'; changed = true; break;
          case 'Ŕ': output[outputPos++] = 'r'; changed = true; break;
          case 'ŕ': output[outputPos++] = 'r'; changed = true; break;
          case 'Ŗ': output[outputPos++] = 'r'; changed = true; break;
          case 'ŗ': output[outputPos++] = 'r'; changed = true; break;
          case 'Ř': output[outputPos++] = 'r'; changed = true; break;
          case 'ř': output[outputPos++] = 'r'; changed = true; break;
          case 'Ś': output[outputPos++] = 's'; changed = true; break;
          case 'ś': output[outputPos++] = 's'; changed = true; break;
          case 'Ŝ': output[outputPos++] = 's'; changed = true; break;
          case 'ŝ': output[outputPos++] = 's'; changed = true; break;
          case 'Ş': output[outputPos++] = 's'; changed = true; break;
          case 'ş': output[outputPos++] = 's'; changed = true; break;
          case 'Š': output[outputPos++] = 's'; changed = true; break;
          case 'š': output[outputPos++] = 's'; changed = true; break;
          case 'Ţ': output[outputPos++] = 't'; changed = true; break;
          case 'ţ': output[outputPos++] = 't'; changed = true; break;
          case 'Ť': output[outputPos++] = 't'; changed = true; break;
          case 'ť': output[outputPos++] = 't'; changed = true; break;
          case 'Ŧ': output[outputPos++] = 't'; changed = true; break;
          case 'ŧ': output[outputPos++] = 't'; changed = true; break;
          case 'Ũ': output[outputPos++] = 'u'; changed = true; break;
          case 'ũ': output[outputPos++] = 'u'; changed = true; break;
          case 'Ū': output[outputPos++] = 'u'; changed = true; break;
          case 'ū': output[outputPos++] = 'u'; changed = true; break;
          case 'Ŭ': output[outputPos++] = 'u'; changed = true; break;
          case 'ŭ': output[outputPos++] = 'u'; changed = true; break;
          case 'Ů': output[outputPos++] = 'u'; changed = true; break;
          case 'ů': output[outputPos++] = 'u'; changed = true; break;
          case 'Ű': output[outputPos++] = 'y'; changed = true; break;
          case 'ű': output[outputPos++] = 'y'; changed = true; break;
          case 'Ų': output[outputPos++] = 'u'; changed = true; break;
          case 'ų': output[outputPos++] = 'u'; changed = true; break;
          case 'Ŵ': output[outputPos++] = 'v'; changed = true; break;
          case 'ŵ': output[outputPos++] = 'v'; changed = true; break;
          case 'Ŷ': output[outputPos++] = 'y'; changed = true; break;
          case 'ŷ': output[outputPos++] = 'y'; changed = true; break;
          case 'Ÿ': output[outputPos++] = 'y'; changed = true; break;
          case 'Ź': output[outputPos++] = 'z'; changed = true; break;
          case 'ź': output[outputPos++] = 'z'; changed = true; break;
          case 'Ż': output[outputPos++] = 'z'; changed = true; break;
          case 'ż': output[outputPos++] = 'z'; changed = true; break;
          case 'Ž': output[outputPos++] = 'z'; changed = true; break;
          case 'ž': output[outputPos++] = 'z'; changed = true; break;
          case 'ſ': output[outputPos++] = 's'; changed = true; break;
          default:
            if (c <= '\u017F') {  // \u017F == "LATIN SMALL LETTER LONG S".
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

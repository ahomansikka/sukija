/*
Copyright (©) 2009-2013 Hannu Väisänen

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
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

/**
 * A class that uses FinnishTokenizerImpl.
 */
public class FinnishTokenizer extends Tokenizer {

  /** A private instance of the JFlex-constructed scanner */
  private final FinnishTokenizerImpl scanner;

  /**
   * Creates a new instance of the {@link FinnishTokenizer}. Attaches the
   * <code>input</code> to a newly created JFlex scanner.
   */
  public FinnishTokenizer (Reader input)
  {
    super (input);
    this.scanner = new FinnishTokenizerImpl (input);
    termAtt = addAttribute(CharTermAttribute.class);
    offsetAtt = addAttribute(OffsetAttribute.class);
    posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    typeAtt = addAttribute(TypeAttribute.class);
  }

  // This tokenizer generates three attributes:
  // offset, positionIncrement and type.
  //
  private CharTermAttribute termAtt;
  private OffsetAttribute offsetAtt;
  private PositionIncrementAttribute posIncrAtt;
  private TypeAttribute typeAtt;

  /*
   * (non-Javadoc)
   *
   * @see org.apache.lucene.analysis.TokenStream#incrementToken()
   */
  @Override
  public final boolean incrementToken() throws IOException
  {
    final int tokenType = scanner.yylex();

    if (tokenType == FinnishTokenizerImpl.YYEOF) {
      return false;
    }

    posIncrAtt.setPositionIncrement (1);
    scanner.getText (termAtt);
    final int start = scanner.yychar();
    offsetAtt.setOffset (correctOffset(start), correctOffset(start+termAtt.length()));
    return true;
  }

  @Override
  public final void end()
  {
    // Set final offset,
    int finalOffset = correctOffset (scanner.yychar() + scanner.yylength());
    offsetAtt.setOffset (finalOffset, finalOffset);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.apache.lucene.analysis.TokenStream#reset()
   */
  public void reset() throws IOException
  {
    super.reset();
    scanner.yyreset (input);
  }
}

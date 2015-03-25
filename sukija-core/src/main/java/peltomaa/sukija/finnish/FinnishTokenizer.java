/*
Copyright (©) 2009-2015 Hannu Väisänen

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

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeFactory;

/**
 * A class that uses FinnishTokenizerImpl.
 */
public class FinnishTokenizer extends Tokenizer {

  /** A private instance of the JFlex-constructed scanner */
  private FinnishTokenizerImpl scanner;

  /**
   * Creates a new FinnishTokenizer.
   */
  public FinnishTokenizer()
  {
    super();
    init();
  }


  /** Creates a new FinnishTokenizer with a given
      {@link org.apache.lucene.util.AttributeSource.AttributeFactory}.
   */
  public FinnishTokenizer (AttributeFactory factory)
  {
    super (factory);
    init();
  }


  private void init()
  {
    this.scanner = new FinnishTokenizerImpl (this.input);
  }


  // This tokenizer generates three attributes:
  // offset, positionIncrement and type.
  //
  private final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  private final OffsetAttribute offsetAtt = addAttribute (OffsetAttribute.class);
  private final PositionIncrementAttribute posIncrAtt = addAttribute (PositionIncrementAttribute.class);
//  private final TypeAttribute typeAtt = addAttribute (TypeAttribute.class);

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
//System.out.println ("FinnishTokenizer: " + termAtt.toString() + " " + offsetAtt.startOffset() + " " + offsetAtt.endOffset());
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

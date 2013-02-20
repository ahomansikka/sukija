/*
Copyright (©) 2009-2012 Hannu Väisänen

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
//import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

/**
 * A class that uses HVTokenizerImpl.
 */
public class HVTokenizer extends Tokenizer {

  /** A private instance of the JFlex-constructed scanner */
  private final HVTokenizerImpl scanner;

  /**
   * Creates a new instance of the {@link HVTokenizer}. Attaches the
   * <code>input</code> to a newly created JFlex scanner.
   */
  public HVTokenizer (Reader input)
  {
    this.input = input;
    this.scanner = new HVTokenizerImpl (input);
    termAtt = addAttribute(CharTermAttribute.class);
    offsetAtt = addAttribute(OffsetAttribute.class);
    posIncrAtt = addAttribute(PositionIncrementAttribute.class);
//    typeAtt = addAttribute(TypeAttribute.class);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.apache.lucene.analysis.TokenStream#incrementToken()
   */
  @Override
  public final boolean incrementToken() throws IOException
  {
    clearAttributes();
    final int tokenType = scanner.yylex();

    if (tokenType == HVTokenizerImpl.YYEOF) {
      return false;
    }

    posIncrAtt.setPositionIncrement (1);
    scanner.getText (termAtt);
    final int start = scanner.yychar();
    offsetAtt.setOffset (correctOffset(start), correctOffset(start+termAtt.length()));
    if (LOG.isDebugEnabled()) LOG.debug ("HVTokenizer: " + termAtt.toString() + " " + offsetAtt.startOffset() + " " + offsetAtt.endOffset());
    return true;
  }

  // This tokenizer generates three attributes:
  // offset, positionIncrement and type.
  //
  private CharTermAttribute termAtt;
  private OffsetAttribute offsetAtt;
  private PositionIncrementAttribute posIncrAtt;
//  private TypeAttribute typeAtt;

  @Override
  public final void end()
  {
    // Set final offset.
    int finalOffset = correctOffset (scanner.yychar() + scanner.yylength());
    offsetAtt.setOffset (finalOffset, finalOffset);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.apache.lucene.analysis.TokenStream#reset()
   */
  @Override
  public void reset() throws IOException
  {
    super.reset();
    scanner.yyreset (input);
  }


  @Override
  public void reset (Reader reader) throws IOException
  {
    super.reset (reader);
    scanner.yyreset (reader);
  }

  private static final Logger LOG = LoggerFactory.getLogger (HVTokenizer.class);
}

/*
Copyright (©) 2009-2016, 2018 Hannu Väisänen

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

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeFactory;


/**
 * A class that uses HVTokenizerImpl.
 */
public final class HVTokenizer extends Tokenizer {

  /**
   * Creates a new HVTokenizer.
   */
  public HVTokenizer()
  {
    super();
    init();
  }


  /** Creates a new HVTokenizer with a given
      org.apache.lucene.util.AttributeSource.AttributeFactory.
   */
  public HVTokenizer (AttributeFactory factory)
  {
    super (factory);
    init();
  }


  private void init()
  {
    this.scanner = new HVTokenizerImpl (this.input);
  }


  /*
   * (non-Javadoc)
   *
   * @see org.apache.lucene.analysis.TokenStream#incrementToken()
   */
  @Override
  public boolean incrementToken() throws IOException
  {
    clearAttributes();
    final int tokenType = scanner.yylex();

    if (tokenType == HVTokenizerImpl.YYEOF) {
      return false;
    }

    scanner.getText (termAtt);
    flagsAtt.setFlags (tokenType);
    posIncrAtt.setPositionIncrement (1);

    final int start = scanner.yychar();
    offsetAtt.setOffset (correctOffset(start), correctOffset(start+termAtt.length()));


//    if (LOG.isDebugEnabled()) {
//      LOG.debug ("HVTokenizer: " + termAtt.toString() + " " + offsetAtt.startOffset() + " " + offsetAtt.endOffset());
//    }
//System.out.println ("HVTokenizer: " + termAtt.toString() + " " + offsetAtt.startOffset() + " " + offsetAtt.endOffset());
//System.out.println ("HVTokenizer:  " + termAtt.toString() + " " + flagsAtt.getFlags());

    return true;
  }


  @Override
  public final void end() throws IOException
  {
    super.end();
    // Set final offset.
    int finalOffset = correctOffset (scanner.yychar() + scanner.yylength());
    offsetAtt.setOffset (finalOffset, finalOffset);
  }


  @Override
  public void close() throws IOException
  {
    super.close();
    scanner.yyreset (input);
  }


  @Override
  public void reset() throws IOException
  {
    super.reset();
    scanner.yyreset (input);
  }


  /** A private instance of the JFlex-constructed scanner */
  private HVTokenizerImpl scanner;

//  private static final Logger LOG = LoggerFactory.getLogger (HVTokenizer.class);
  private final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  private final FlagsAttribute flagsAtt = addAttribute (FlagsAttribute.class);
  private final OffsetAttribute offsetAtt = addAttribute (OffsetAttribute.class);
  private final PositionIncrementAttribute posIncrAtt = addAttribute (PositionIncrementAttribute.class);
}

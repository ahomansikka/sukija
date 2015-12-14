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
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.puimula.libvoikko.TokenType;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.voikko.VoikkoUtils;


/** Tokenizer, joka käyttää Voikon tokens-funktiota.<p>

Asetetaan attribuutit CharTermAttribute, OffsetAttribute ja
PositionIncrementAttribute.<p>
*/
public class VoikkoTokenizer extends Tokenizer {
  private JFlexReader scanner;

  /** Creates a new VoikkoTokenizer.
   *
   * @param voikko   Voikko.
   * @param ignoreNL Hylkää sanat, joissa ei ole yhtään kirjainta (esim. "1234" tai "1234-").
   */
  public VoikkoTokenizer (Voikko voikko, boolean ignoreNL)
  {
    super();
    init (voikko, ignoreNL);
  }


  /** Creates a new VoikkoTokenizer with a given
   *  org.apache.lucene.util.AttributeSource.AttributeFactory.
   *
   * @param factory  Attribure factory.
   * @param voikko   Voikko.
   * @param ignoreNL Hylkää sanat, joissa ei ole yhtään kirjainta (esim. "1234" tai "1234-").
   */
  public VoikkoTokenizer (AttributeFactory factory, Voikko voikko, boolean ignoreNL)
  {
    super (factory);
    init (voikko, ignoreNL);
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

    if (list == null || index >= list.size()) {
      if (!read()) {
        return false;
      }
    }

    while (list.get(index).getType() != TokenType.WORD) {
      index++;
      if (index >= list.size()) {
        if (!read()) {
          return false;
        }
      }
    }

    final int start = scanner.yychar();
    termAtt.setEmpty().append (list.get(index).getText());
    posIncrAtt.setPositionIncrement (1);
    offsetAtt.setOffset (correctOffset(start), correctOffset(start+termAtt.length()));
    index++;
    return true;
  }


  @Override
  public final void end() throws IOException
  {
    super.end();
    // Set final offset.
    final int finalOffset = correctOffset (scanner.yychar() + scanner.yylength());
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
    index = 0;
 }


  private void init (Voikko voikko, boolean ignoreNL)
  {
    this.scanner = new JFlexReader (this.input);
    this.voikko = voikko;
    this.ignoreNL = ignoreNL;
  }


  private boolean read()
  {
    try {
      do {
        final int tokenType = scanner.yylex();
        if (tokenType == JFlexReader.YYEOF) {
          return false;
        }
        scanner.getText (termAtt);
//System.out.println ("Huu " + termAtt.toString() + " " + ignoreNL);
      }
      while (ignoreNL && ignoreWord (termAtt));

//System.out.println ("Haa " + termAtt.toString() + "\n");

      if (ignoreNL) {
        List<org.puimula.libvoikko.Token> tmp = voikko.tokens (termAtt.toString());
        list = new ArrayList<org.puimula.libvoikko.Token>();
        for (int i = 0; i < tmp.size(); i++) {
          if (!ignoreWord (tmp.get(i).getText())) {
            list.add (tmp.get(i));
          }
        }
      }
      else {
        list = voikko.tokens (termAtt.toString());
      }

      index = 0;
      return true;
    }
    catch (IOException e)
    {
      return false;
    }
  }


  private boolean ignoreWord (CharSequence word)
  {
    for (int i = 0; i < word.length(); i++) {
      if (Character.isLetter(word.charAt(i))) return false;
    }
    return true;
  }


  private final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  private final OffsetAttribute offsetAtt = addAttribute (OffsetAttribute.class);
  private final PositionIncrementAttribute posIncrAtt = addAttribute (PositionIncrementAttribute.class);

  private Voikko voikko;
  private List<org.puimula.libvoikko.Token> list;
  private int index = 0;
  private boolean ignoreNL;
}

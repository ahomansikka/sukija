/*
Copyright (©) 2015, 2017, 2020 Hannu Väisänen

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
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.puimula.libvoikko.Token;
import org.puimula.libvoikko.TokenType;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.voikko.VoikkoUtils;


/** Tokenizer, joka käyttää Voikon tokens-funktiota.<p>

Asetetaan attribuutit CharTermAttribute, FlagsAttribute, OffsetAttribute ja
PositionIncrementAttribute.<p>
*/
public class VoikkoTokenizer extends Tokenizer {
  /** Tehdään uusi VoikkoTokenizer.
   *
   * @param voikko   Voikko.
   * @param ignoreNL Hylätään sanat, joissa ei ole yhtään kirjainta (esim. "1234" tai "1234-").
   */
  public VoikkoTokenizer (Voikko voikko, boolean ignoreNL)
  {
    super();
    init (voikko, ignoreNL);
  }


  /** Tehdään uusi VoikkoTokenizer, joka käyttää annettuja attribuutteja.
   *
   * @param factory  Attribure factory.
   * @param voikko   Voikko.
   * @param ignoreNL Hylätään sanat, joissa ei ole yhtään kirjainta (esim. "1234" tai "1234-").
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
    if (list == null) {
      // Muutetaan syöte symboleiksi, kun tätä funktiota kutsutaan eka kerran.
      tokenize (input);
    }

    clearAttributes();

    while (index < list.size()) {
      t = list.get (index);
      if (t.getType() == TokenType.WORD) {
        String word = trim (t.getText());
        if (word.length() > 0) {
          termAtt.setEmpty().append (word);
          posIncrAtt.setPositionIncrement (1);
          offsetAtt.setOffset (correctOffset(t.getStartOffset()), correctOffset(t.getEndOffset()));
          if (word.indexOf("-") >= 0) {
            flagsAtt.setFlags (flagsAtt.getFlags() | Constants.COMPOUND_WORD);
          }
          else {
            flagsAtt.setFlags (flagsAtt.getFlags() | Constants.WORD);
          }
/*
          if (t.getText().compareTo(sb.substring(t.getStartOffset(),t.getEndOffset())) != 0) {
            throw new RuntimeException ("Virhe: " + t.getText() + " != " +  sb.substring(t.getStartOffset(),t.getEndOffset()));
          }
*/
          index++;
          return true;
        }
      }
      index++;
    }
    return false;
  }


  @Override
  public final void end() throws IOException
  {
    super.end();
    // Set final offset.
    final int finalOffset = correctOffset (t.getStartOffset() + t.getEndOffset());
    offsetAtt.setOffset (finalOffset, finalOffset);
  }


  @Override
  public void close() throws IOException
  {
    super.close();
  }


  @Override
  public void reset() throws IOException
  {
    super.reset();
    index = 0;
    list = null;
 }


  private void init (Voikko voikko, boolean ignoreNL)
  {
    this.voikko = voikko;
    this.ignoreNL = ignoreNL;
  }


  // Hylätään sana, jos siinä vain muita merkkejä kun kirjaimia
  // tai jos siinä on vääriä kirjaimia. Jälkimmäinen testi poistaa
  // ei-latinalaiset merkit, esimerkiksi kreikkalaiset, kyrilliset,
  // kiinalaiset, japanilaiset, korealaiset kirjainmerkit.
  //
  // Hyväksytään oikeiksi kirjaimiksi nämä Unicode-merkit:
  // A-Za-Z:        C0 Controls and Basic Latin.
  // À-ÖØ-öø-ÿ:     C1 Controls and Latin-1 Supplement.
  // \u0100-\u017F: Latin Extended-A. ("European Latin", m.m. šŠ ja žŽ.)
  //
  // Sana hylätään, jos siinä on yksikin väärä kirjain, joten
  // toinen testi (isLetter()) ei voi palauttaa suoraan arvoa false,
  // sillä sanassa voi olla oikeita kirjaimia ennen kuin siinä
  // on väärä kirjain.
  //
  private boolean ignoreWord (String word)
  {
    boolean ignore = true;
    for (int i = 0; i < word.length(); i++) {
      if (word.charAt(i) > '\u017F') return true; // Väärä kirjain => sana hylätään.
      if (Character.isLetter(word.charAt(i))) ignore = false;
    }
    return ignore;
  }


  /** Luetaan koko indeksoitava tiedosto ja
   *  muutetaan se Voikon symboleiksi muuttujaan 'list'.
   */
  private void tokenize (Reader input) throws IOException
  {
    int len;
    sb.setLength (0);
    while ((len = input.read (buffer)) > 0) {
      sb.append (buffer, 0, len);
    }
    list = voikko.tokens (sb.toString());
  }


  /** Poistetaan sanan alusta ja lopusta merkit,
   *  jotka eivät ole numeroita tai kirjaimia.
   *  Jos ignoreNL == true, hylätään sanat, joissa
   *  ei ole yhtään kirjainta.
   */
  private String trim (String word)
  {
    int len = word.length();
    int start = 0;
    int end = len;

    for ( ; start < len && !Character.isLetterOrDigit(word.charAt(start)); start++)
    {
    }
    for ( ; end >= start && !Character.isLetterOrDigit(word.charAt(end-1)); end--)
    {
    }

    if (start > 0 || end < len) {
      if (start < end) {
        word = word.substring (start, end);
      }
      else {
        return "";
      }
    }

    if (ignoreNL && ignoreWord (word)) {
      return "";
    }
    return word;
  }


  private final StringBuilder sb = new StringBuilder();
  private final char[] buffer = new char[8192];
  private final String data = null;
  private Token t;

  private final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  private final FlagsAttribute flagsAtt = addAttribute (FlagsAttribute.class);
  private final OffsetAttribute offsetAtt = addAttribute (OffsetAttribute.class);
  private final PositionIncrementAttribute posIncrAtt = addAttribute (PositionIncrementAttribute.class);

  private Voikko voikko;
  private List<Token> list = null;
  private int index = 0;
  private boolean ignoreNL;
}

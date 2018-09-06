/*
Copyright (©) 2014-2016, 2018 Hannu Väisänen

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

package peltomaa.sukija.util;

import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.puimula.libvoikko.*;
import peltomaa.sukija.attributes.BaseFormAttribute;
import peltomaa.sukija.attributes.OriginalWordAttribute;
import peltomaa.sukija.attributes.VoikkoAttribute;


/**
 * Abstract base class for Sukija filters.
   @see #filter
 */
public abstract class SukijaFilter extends TokenFilter {
  public SukijaFilter (TokenStream in, Voikko voikko)
  {
    super (in);
    this.voikko = voikko;
  }


  @Override
  public final boolean incrementToken() throws IOException
  {
//System.out.println ("SukijaFilter0 " + word + " " + hasAttribute(BaseFormAttribute.class));
//System.out.println ("SukijaFilter0 " + word + " " + hasAttribute(FlagsAttribute.class));
//System.out.println ("SukijaFilter0 " + word + " " + termAtt.toString());

    // Etsitään uusi analysoitava sana. Tässä silmukassa pyöritään,
    // kunnes metodi 'filter()' palauttaa iteraattorin luetun
    // sanan analyyseihin. Metodissa 'filter()' voidaan ohittaa
    // mielivaltainen määrä sanoja, jotka luetaan tässä silmukassa
    // kutsulla 'input.incrementToken()'.
    //
    while ((iterator == null) || (!iterator.hasNext())) {
//System.out.println ("SukijaFilterA " + word + " " + termAtt.toString() + " [" + originalWordAtt.getOriginalWord());
      if (!input.incrementToken()) {
         return false;
      }
      originalWordAtt.setOriginalWord (termAtt);
      word = termAtt.toString().toLowerCase();
//System.out.println ("SukijaFilter1 " + word + " " + termAtt.toString() + " [" + originalWordAtt.getOriginalWord());
      positionIncrement = 1;
      iterator = filter();
    }

//System.out.println ("SukijaFilter2 " + word + " " + termAtt.toString() + " [" + originalWordAtt.getOriginalWord());


    // Käydään läpi kaikki sanan analyysit.
    //
    if (iterator.hasNext()) {
      termAtt.setEmpty().append (iterator.next());
//System.out.println ("SukijaFilter3 " + word + " " + termAtt.toString() + " [" + originalWordAtt.getOriginalWord() + "]");
      posIncrAtt.setPositionIncrement (positionIncrement);
      positionIncrement = 0;
//System.out.println ("SukijaFilter4 " + word + " " + baseFormAtt.getBaseForms().toString());
//System.out.println ("SukijaFilter4 " + word + " " + voikkoAtt.getAnalysis().toString());
      return true;
    }
//System.out.println ("SukijaFilter6 " + word + " " + termAtt.toString() + " [" + originalWordAtt.getOriginalWord());
    return true;
  }


  protected abstract Iterator<String> filter();

  protected final BaseFormAttribute baseFormAtt = addAttribute  (BaseFormAttribute.class);
  protected final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  protected final FlagsAttribute flagsAtt = input.addAttribute (FlagsAttribute.class);
  protected final OriginalWordAttribute originalWordAtt = addAttribute  (OriginalWordAttribute.class);
  protected final PositionIncrementAttribute posIncrAtt = addAttribute (PositionIncrementAttribute.class);
  protected final VoikkoAttribute voikkoAtt = addAttribute (VoikkoAttribute.class);

  private int positionIncrement = 1;
  protected String word;
  protected Voikko voikko;
  protected Iterator<String> iterator;
}

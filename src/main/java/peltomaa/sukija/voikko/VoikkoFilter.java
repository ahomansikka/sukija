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

package peltomaa.sukija.voikko;

import java.io.IOException;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.puimula.libvoikko.*;
import peltomaa.sukija.attributes.VoikkoAttribute;


/**
 * Suodatin, joka muuttaa Voikon attribuutit ({@code org.puimula.libvoikko.Analysis})
 * Solr:n attribuuteiksi ({@link peltomaa.sukija.attributes.VoikkoAttribute}).<p>
 * Esimerkki, joka tulostaa Voikon attribuutit:
<pre>
    Reader reader = new FileReader (fileName);
    TokenStream t = new FinnishTokenizer();  // Tai joku muu Tokenizer.
    ((Tokenizer)t).setReader (reader);
    t = new VoikkoFilter (t, voikko);
    t.reset();
    while (t.incrementToken()) {
      VoikkoAttribute voikkoAtt = t.getAttribute (VoikkoAttribute.class);
      VoikkoUtils.printAnalysisResult (voikkoAtt, System.out);
    }
    t.end();
    t.close();
</pre>
 */
public final class VoikkoFilter extends TokenFilter {
  public VoikkoFilter (TokenStream in, Voikko voikko)
  {
    super (in);
    if (voikko == null) {
      throw new RuntimeException ("voikko == null");
    }
    this.voikko = voikko;
  }


  @Override
  public final boolean incrementToken() throws IOException
  {
    if (!input.incrementToken()) {
      return false;
    }
    voikkoAtt.setAnalysis (voikko.analyze (termAtt.toString()));
//System.out.println (termAtt.toString() + " " + voikkoAtt.getAnalysis().toString());
//    VoikkoUtils.printAnalysisResult (voikkoAtt, System.out);
    return true;
  }


  private final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  private final VoikkoAttribute voikkoAtt = addAttribute (VoikkoAttribute.class);
  private final Voikko voikko;
}

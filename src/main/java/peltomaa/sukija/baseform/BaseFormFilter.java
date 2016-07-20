/*
Copyright (©) 2015-2016 Hannu Väisänen

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

package peltomaa.sukija.baseform;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.puimula.libvoikko.*;
import peltomaa.sukija.attributes.BaseFormAttribute;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.util.SukijaFilter;
import peltomaa.sukija.voikko.VoikkoUtils;


/**
 * Erotetaan perusmuodot. Kaikki muut Voikon attribuutit häviävät.
 */
public final class BaseFormFilter extends SukijaFilter {
  public BaseFormFilter (TokenStream input, Voikko voikko, boolean successOnly)
  {
    super (input, voikko);
    this.successOnly = successOnly;
  }


  public BaseFormFilter (TokenStream input, Voikko voikko)
  {
    this (input, voikko, false);
  }


  @Override
  public Iterator<String> filter()
  {
    List<Analysis> analysis = voikko.analyze (termAtt.toString());

    if (analysis.size() ==  0) {
      baseFormAtt.addBaseForm (termAtt.toString());
      flagsAtt.setFlags (flagsAtt.getFlags() | Constants.UNKNOWN);
    }
    else {
      baseFormAtt.addBaseForms (VoikkoUtils.getBaseForms(analysis));
      flagsAtt.setFlags (flagsAtt.getFlags() | Constants.FOUND);
//System.out.println (analysis.toString());
    }
    return baseFormAtt.getBaseForms().iterator();
  }


  private final boolean successOnly;

  private final BaseFormAttribute baseFormAtt = addAttribute (BaseFormAttribute.class);

/*
  private final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  private final FlagsAttribute flagsAtt = addAttribute (FlagsAttribute.class);
  private final OffsetAttribute offsetAtt = addAttribute (OffsetAttribute.class);
  private final PositionIncrementAttribute posIncrAtt = addAttribute (PositionIncrementAttribute.class);
*/
}

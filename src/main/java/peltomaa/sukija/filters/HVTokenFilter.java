/*
Copyright (©) 2020 Hannu Väisänen

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

package peltomaa.sukija.filters;

import peltomaa.sukija.util.Constants;
import peltomaa.sukija.attributes.OriginalWordAttribute;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.compound.CompoundWordTokenFilterBase;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.TokenStream;


public class HVTokenFilter extends CompoundWordTokenFilterBase {

  public HVTokenFilter (TokenStream input)
  {
    super (input, CharArraySet.EMPTY_SET, 2, 2, 999, false);
  }


  @Override
  protected void decompose()
  {
    if (Constants.hasFlag (flagsAtt, Constants.EXTRA)) {
      final String s = replace (termAtt.toString());
      tokens.add (new CompoundToken (0, termAtt.toString().length()));
    }
  }


  private String replace (String s)
  {
    if (Constants.hasFlag (flagsAtt, Constants.BRACKET)) {
      s = s.replace("[","").replace("]","");
    }
    if (Constants.hasFlag (flagsAtt, Constants.LATEX_HYPHEN)) {
      s = Constants.RE_LATEX_HYPHEN.matcher(s).replaceAll("");
    }
    if (Constants.hasFlag (flagsAtt, Constants.LATEX_COMPOUND_WORD)) {
      s = Constants.RE_LATEX_COMPOUND_WORD.matcher(s).replaceAll("-");
    } 
    return s;
  }

  private final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  private final FlagsAttribute flagsAtt = addAttribute (FlagsAttribute.class);
  private final OffsetAttribute offsetAtt = addAttribute (OffsetAttribute.class);
  private final OriginalWordAttribute originalWordAtt = addAttribute (OriginalWordAttribute.class);
  private final PositionIncrementAttribute posIncAtt = addAttribute (PositionIncrementAttribute.class);
}

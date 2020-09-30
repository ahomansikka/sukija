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

import org.apache.lucene.analysis.TokenStream;
import peltomaa.sukija.util.Constants;


public class HVTokenFilter extends HVTokenFilterBase {

  public HVTokenFilter (TokenStream input)
  {
    super (input);
  }


  @Override
  protected void decompose()
  {
    if (Constants.hasFlag (flagsAtt, Constants.EXTRA)) {
      final String s = replace (termAtt.toString());
      if (s.toString().indexOf('-') > -1) {
        tokens.add (new NewToken (s, Constants.COMPOUND_WORD));
      }
      else {
        tokens.add (new NewToken (s, Constants.WORD));
      }
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
}

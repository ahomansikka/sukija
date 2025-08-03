/*
Copyright (©) 2020-2021, 2025 Hannu Väisänen

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

import java.io.IOException;
import peltomaa.sukija.util.Constants;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class LaTeXFilter extends TokenFilter {
  public LaTeXFilter (TokenStream in)
  {
    super (in);
  }


  @Override
  public final boolean incrementToken() throws IOException {
    if (input.incrementToken()) {
      String s = termAtt.toString();
if (LOG.isDebugEnabled()) LOG.debug ("A " + s + " " + Constants.toString(flagsAtt));
      boolean hasChanged = false;

      if (Constants.hasFlag (flagsAtt, Constants.BRACKET)) {
        s = s.replace("[","").replace("]","");
        Constants.removeFlags (flagsAtt, Constants.BRACKET);
        hasChanged = true;
      }
      if (Constants.hasFlag (flagsAtt, Constants.LATEX_HYPHEN)) {
        s = Constants.RE_LATEX_HYPHEN.matcher(s).replaceAll("");
        Constants.removeFlags (flagsAtt, Constants.LATEX_HYPHEN);
        hasChanged = true;
      }
      if (Constants.hasFlag (flagsAtt, Constants.LATEX_COMPOUND_WORD)) {
if (LOG.isDebugEnabled()) LOG.debug ("B " + s);
        s = Constants.RE_LATEX_COMPOUND_WORD.matcher(s).replaceAll("-");
        Constants.removeFlags (flagsAtt, Constants.LATEX_COMPOUND_WORD);
        hasChanged = true;
if (LOG.isDebugEnabled()) LOG.debug ("C " + s);
      }
      if (hasChanged) {
        termAtt.setEmpty().append (s);
if (LOG.isDebugEnabled()) LOG.debug ("D " + s);
      }
      return true;
    }
    else {
      return false;
    }
  }


  private final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  private final FlagsAttribute flagsAtt = addAttribute (FlagsAttribute.class);

  private static final Logger LOG = LoggerFactory.getLogger (LaTeXFilter.class);
  private static final long serialVersionUID = 1L;
}

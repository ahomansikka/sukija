/*
Copyright (©) 2014-2015 Hannu Väisänen

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
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


/**
 * Abstract base class for Sukija filters.
 *@see #filter
*/
public abstract class SukijaFilter extends TokenFilter {
  public SukijaFilter (TokenStream in)
  {
    super (in);
  }


  @Override
  public final boolean incrementToken() throws IOException
  {
    while ((iterator == null) || (!iterator.hasNext())) {
      if (!input.incrementToken()) {
         return false;
      }
      final String word = termAtt.toString();
      if (word == null || word.length() == 0) {
        continue;  // Tätä ei pitäisi koskaan tapahtua.
      }
      filter (word);
      positionIncrement = 1;
      savedState = captureState();
//System.out.println ("SukijaFilter1 " + word + " " + savedState.toString());
    }

    if (iterator.hasNext()) {
      restoreState (savedState);
      String baseWord = (String)iterator.next();
      termAtt.setEmpty();
      termAtt.append (baseWord);
      posIncrAtt.setPositionIncrement (positionIncrement);
      positionIncrement = 0;
//System.out.println ("SukijaFilter2 " + baseWord);
      return true;
    }
// System.out.println ("SukijaFilter false " + termAtt.toString()); System.exit (1);
    return true;
  }


  protected abstract void filter (String word);

  protected Iterator<String> iterator;
  protected int positionIncrement = 1;
  protected State savedState;
  protected final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  protected final PositionIncrementAttribute posIncrAtt = addAttribute (PositionIncrementAttribute.class);
}

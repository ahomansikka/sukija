/*
Copyrigh (©) 2010-2012 Hannu Väisänen

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

package peltomaa.sukija.morphology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
//import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


/**
 * Filter that uses MorphologyFilter to filter words.
 */
public class MorphologyFilter extends TokenFilter {

  /**
   * Create MorphologyFilter.
   *
   * @param in       TokenStream.
   */
  public MorphologyFilter (TokenStream in, Morphology morphology)
  {
    super (in);
    this.morphology = morphology;
  }


  /**
   * @return  Returns the next token in the stream, or null at EOS.
   */
  public final boolean incrementToken() throws IOException
  {
    if ((iterator == null) || (!iterator.hasNext())) {
      if (!input.incrementToken()) {
        return false;
      }
      final String word = termAtt.toString();
      if (word == null || word.length() == 0) {
        return false;
      }
      if (LOG.isDebugEnabled()) LOG.debug ("Token1 " + word);
      set.clear();
      positionIncrement = 1;
      morphology.analyzeLowerCase (word, set);
      iterator = set.iterator();
      savedState = captureState();
    }

    if (iterator.hasNext()) {
      restoreState (savedState);
      String baseWord = (String)iterator.next();
      termAtt.setEmpty();
      termAtt.append (baseWord);
      termAtt.setLength (baseWord.length());
      posIncrAtt.setPositionIncrement (positionIncrement);
      positionIncrement = 0;
      if (LOG.isDebugEnabled()) LOG.debug ("Token2 " + termAtt.toString()); // + " " + offsetAtt.startOffset() + " " + offsetAtt.endOffset());
      return true;
    }
    if (LOG.isDebugEnabled()) LOG.debug ("MorphologyFilter: You should never see this message!");
    return false;
  }


  private static final Logger LOG = LoggerFactory.getLogger (MorphologyFilter.class);

  private Iterator<String> iterator;
  private Set<String> set = new TreeSet<String>();
  private int positionIncrement = 1;
  private State savedState;

  private final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  private final PositionIncrementAttribute posIncrAtt = addAttribute (PositionIncrementAttribute.class);

  protected Morphology morphology;
}

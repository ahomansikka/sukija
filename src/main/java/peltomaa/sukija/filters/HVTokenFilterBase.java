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


// This code is based on org.apache.lucene.analysis.compound.CompoundWordTokenFilterBase.

import java.io.IOException;
import java.util.LinkedList;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.attributes.OriginalWordAttribute;


public abstract class HVTokenFilterBase extends TokenFilter {

  public HVTokenFilterBase (TokenStream input)
  {
    super (input);
    tokens = new LinkedList<NewToken>();
  }


  @Override
  public final boolean incrementToken() throws IOException
  {
    if (!tokens.isEmpty()) {
      assert current != null;
      NewToken token = tokens.removeFirst();
      restoreState (current);   // Keep all other attributes untouched.
      termAtt.setEmpty().append (token.text);
      offsetAtt.setOffset (token.startOffset, token.endOffset);
      posIncAtt.setPositionIncrement (0);
      flagsAtt.setFlags (token.flagsAttributeValues);
//System.out.println ("TokenA   " + termAtt.toString() + " " + Constants.toString(flagsAtt) + " " + originalWordAtt.getOriginalWord() + " " + tokens.size());
      return true;
    }

    current = null; // Not really needed, but for safety.
    if (input.incrementToken()) {
//System.out.println ("Token1   " + termAtt.toString() + " " + Constants.toString(flagsAtt) + " " + originalWordAtt.getOriginalWord() + " " + tokens.size());
      decompose();
      // Only capture the state if we really need it for producing new tokens.
      if (!tokens.isEmpty()) {
        current = captureState();
      }
      // Return original token.
//System.out.println ("Token2   " + termAtt.toString() + " " + Constants.toString(flagsAtt) + " " + originalWordAtt.getOriginalWord() + " " + tokens.size());
      return true;
    }
    else {
      return false;
    }
  }


  protected abstract void decompose();


  @Override
  public void reset() throws IOException {
    super.reset();
    tokens.clear();
    current = null;
  }


  protected class NewToken {
    public final String text;
    public final int startOffset, endOffset;  // Alkuperäisen, muuttamattoman sanan alku- ja loppukohta.
    public final int flagsAttributeValues;

    public NewToken (String text, int flagsAttributeValues)
    {
      this.text = text;
      this.flagsAttributeValues = flagsAttributeValues;
      this.startOffset = HVTokenFilterBase.this.offsetAtt.startOffset();
      this.endOffset = HVTokenFilterBase.this.offsetAtt.endOffset();
    }
  }

  protected final LinkedList<NewToken> tokens;
  protected final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  protected final FlagsAttribute flagsAtt = addAttribute (FlagsAttribute.class);
  protected final OffsetAttribute offsetAtt = addAttribute (OffsetAttribute.class);
  protected final OriginalWordAttribute originalWordAtt = addAttribute (OriginalWordAttribute.class);
  private final PositionIncrementAttribute posIncAtt = addAttribute (PositionIncrementAttribute.class);
  private State current;
}

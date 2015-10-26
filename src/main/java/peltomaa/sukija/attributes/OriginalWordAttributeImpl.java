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

package peltomaa.sukija.attributes;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeImpl;


public class OriginalWordAttributeImpl extends AttributeImpl implements OriginalWordAttribute, Cloneable {
  private String originalWord;

  public OriginalWordAttributeImpl() {}


  @Override
  public String getOriginalWord()
  {
    return originalWord;
  }


  @Override
  public void setOriginalWord (CharTermAttribute termAtt)
  {
    originalWord = termAtt.toString(); //new String (termAtt.buffer(), 0, termAtt.length());
  }


  @Override
  public void clear()
  {
    originalWord = null;
  }


  @Override
  public void copyTo (AttributeImpl target)
  {
    ((OriginalWordAttributeImpl)target).originalWord = originalWord;
  }


  @Override
  public boolean equals (Object other)
  {
    if (this == other) {
      return true;
    }
    
    if (other instanceof OriginalWordAttributeImpl) {
      final OriginalWordAttributeImpl o = (OriginalWordAttributeImpl)other;
      return (this.originalWord == null
              ? o.originalWord == null
              : this.originalWord.equals (originalWord));
    }

    return false;
  }


  @Override
  public int hashCode()
  {
    return (originalWord == null) ? 0 : originalWord.hashCode();
  }
}

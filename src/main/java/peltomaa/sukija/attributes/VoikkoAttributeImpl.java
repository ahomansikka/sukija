/*
Copyright (©) 2015-2017 Hannu Väisänen

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


import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;
import peltomaa.sukija.voikko.VoikkoUtils;
import org.puimula.libvoikko.Analysis;


public class VoikkoAttributeImpl extends AttributeImpl implements VoikkoAttribute, Cloneable {
  private List<Analysis> analysis;

  public VoikkoAttributeImpl() {}


  @Override
  public void setAnalysis (List<Analysis> analysis)
  {
    this.analysis = analysis;
  }


  @Override
  public List<Analysis> getAnalysis()
  {
    return analysis;
  }


  @Override
  public Analysis getAnalysis (int n)
  {
    return analysis.get (n);
  }


  @Override
  public void addAnalysis (List<Analysis> analysis)
  {
    if (this.analysis == null) {
      this.analysis = analysis;
    }
    else {
      this.analysis.addAll (analysis);
    }
  }


  public void addAnalysis (Analysis analysis)
  {
    if (this.analysis == null) {
      this.analysis = new ArrayList<Analysis>();
    }
    this.analysis.add (analysis);
  }


  @Override
  public void clear()
  {
    analysis = null;
  }


  @Override
  public boolean equals (Object other)
  {
    if (this == other) {
      return true;
    }
    
    if (other instanceof VoikkoAttributeImpl) {
      final VoikkoAttributeImpl o = (VoikkoAttributeImpl)other;
      return (this.analysis == null ? o.analysis == null : this.analysis.equals (o.analysis));
    }
    
    return false;
  }


  @Override
  public int hashCode()
  {
    return (analysis == null) ? 0 : analysis.hashCode();
  }


  /** Tämä kopioi myös kentät eli on deep copy. */
  @Override
  public void copyTo (AttributeImpl target)
  {
    // Deep copy.
    List<Analysis> cloned = null;
    if (analysis != null) {
      cloned = new ArrayList<Analysis> (analysis.size());
      for (int i = 0; i < analysis.size(); i++) {
        Analysis a = new Analysis();
        a.putAll (analysis.get(i));
        cloned.add (a);
      }
    }
    ((VoikkoAttributeImpl)target).setAnalysis (cloned);
  }


  @Override
  public VoikkoAttributeImpl clone()
  {
    VoikkoAttributeImpl cloned = new VoikkoAttributeImpl();
    this.copyTo (cloned);
    return cloned;
  }


  @Override
  public void reflectWith (AttributeReflector reflector)
  {
    if (analysis == null) {
      reflector.reflect (VoikkoAttribute.class, "voikkoAttribute", "null");
    }
    else {
      reflector.reflect (VoikkoAttribute.class, "voikkoAttribute", analysis.toString());
    }
  }
}

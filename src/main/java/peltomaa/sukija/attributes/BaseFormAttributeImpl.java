/*
Copyright (©) 2016, 2018 Hannu Väisänen

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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;


public class BaseFormAttributeImpl extends AttributeImpl implements BaseFormAttribute, Cloneable {
  private Set<String> baseForms;

  public BaseFormAttributeImpl() {baseForms = new HashSet<String>();}


  @Override
  public Set<String> getBaseForms() {return baseForms;}


  @Override
  public void addBaseForms (Set<String> baseForms)
  {
    this.baseForms.addAll (baseForms);
  }


  @Override
  public void addBaseForm (String baseForm)
  {
    this.baseForms.add (baseForm);
  }


  @Override
  public void clear()
  {
    baseForms.clear();
  }


  @Override
  public void copyTo (AttributeImpl target)
  {
    HashSet<String> cloned = null;
    if (baseForms != null) {
      cloned = new HashSet<String>();
      cloned.addAll (baseForms);
    }
    ((BaseFormAttributeImpl)target).baseForms = cloned;
  }


  @Override
  public BaseFormAttributeImpl clone()
  {
    BaseFormAttributeImpl cloned = new BaseFormAttributeImpl();
    this.copyTo (cloned);
    return cloned;
  }


  @Override
  public boolean equals (Object other)
  {
    if (this == other) {
      return true;
    }
    
    if (other instanceof BaseFormAttributeImpl) {
      final BaseFormAttributeImpl o = (BaseFormAttributeImpl)other;
      return (this.baseForms == null
              ? o.baseForms == null
              : this.baseForms.equals (baseForms));
    }
    return false;
  }


  @Override
  public int hashCode()
  {
    return (baseForms == null) ? 0 : baseForms.hashCode();
  }


  @Override
  public void reflectWith (AttributeReflector reflector)
  {
    reflector.reflect (BaseFormAttribute.class, "baseForms", baseForms.toString());
  }
}

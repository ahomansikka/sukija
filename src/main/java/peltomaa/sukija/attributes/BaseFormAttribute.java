/*
Copyright (©) 2016 Hannu Väisänen

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

import java.util.Iterator;
import java.util.Set;
import org.apache.lucene.util.Attribute;


public interface BaseFormAttribute extends Attribute {
  /** Palautetaan sanan perusmuodot. */
  public Set<String> getBaseForms();

  /** Lisätään perusmuotoja. */
  public void addBaseForms (Set<String> baseForms);

  /** Lisätään perusmuoto. */
  public void addBaseForm (String baseForm);

  /** Poistetaan kaikki perusmuodot. */
  public void clear();
}

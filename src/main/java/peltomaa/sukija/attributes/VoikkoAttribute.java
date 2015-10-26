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

import java.util.List;
import org.apache.lucene.util.Attribute;
import org.puimula.libvoikko.Analysis;


/** Voikon generoimat morfologiatiedot.
 */
public interface VoikkoAttribute extends Attribute {

  /** Asetetaan Voikon analyysin tulos. Listan alkioita
   *  ei kopioida eli tämä on shallow copy.
   */
  public void setAnalysis (List<Analysis> analysis);


  /** Palautetaan Voikon analyysin tulos. */
  public List<Analysis> getAnalysis();


  /** Palautetaan analyysin tulos numero n.<p>
    * Tämä vastaa kutsua {@code getAnalysis().get(n);}.
   */
  public Analysis getAnalysis (int n);
}

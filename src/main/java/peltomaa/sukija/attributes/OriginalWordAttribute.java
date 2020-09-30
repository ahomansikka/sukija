/*
Copyright (©) 2015, 2020 Hannu Väisänen

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
import org.apache.lucene.util.Attribute;


/**
 * Solr:ää varten attribuuttiin termAtt täytyy laittaa perusmuotoon
 * muutettu sana. {#link VoikkoFilter} ja {#SuggestionFilter} laittavat
 * alkuperäisen sanan tähän attribuuttiin.
 */
public interface OriginalWordAttribute extends Attribute {

  /** Palautetaan alkuperäinen, tiedostosta luettu sana. */
  public String getOriginalWord();

  /** Tallennetaan tiedostosta luettu sana. */
  public void setOriginalWord (CharTermAttribute termAtt);

  /** Tallennetaan tiedostosta luettu sana. */
  public void setOriginalWord (String originalWord);
}

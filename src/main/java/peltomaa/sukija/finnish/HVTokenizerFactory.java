/*
Copyright (©) 2009-2015, 2022 Hannu Väisänen

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

package peltomaa.sukija.finnish;

import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

/**
 * Factory for {@link HVTokenizer}. 
 * <pre class="prettyprint" >
 * &lt;fieldType name="text" class="solr.TextField"
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="peltomaa.sukija.finnish.HVTokenizerFactory"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre> 
 */
public class HVTokenizerFactory extends TokenizerFactory {
  /** Create new HVTokenizerFactory.
   */
  public HVTokenizerFactory (Map<String,String> args)
  {
    super (args);
  }

  public HVTokenizer create (AttributeFactory factory)
  {
    return new HVTokenizer (factory);
  }
}

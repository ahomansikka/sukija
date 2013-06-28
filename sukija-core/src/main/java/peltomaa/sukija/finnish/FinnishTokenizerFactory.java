/*
Copyright (©) 2009-2013 Hannu Väisänen

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
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource.AttributeFactory;

/**
 * Factory for {@link FinnishTokenizer}. 
 * <pre class="prettyprint" >
 * &lt;fieldType name="text" class="solr.TextField"
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="peltomaa.sukija.finnish.FinnishTokenizerFactory"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre> 
 */
public class FinnishTokenizerFactory extends TokenizerFactory {

  /** Creates a new FinnishTokenizerFactory.
   */
  public FinnishTokenizerFactory (Map<String,String> args)
  {
    super (args);
  }

  public FinnishTokenizer create (AttributeFactory factory, Reader reader)
  {
    return new FinnishTokenizer (factory, reader);
  }
}

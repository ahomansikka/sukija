/*
Copyright (©) 2012-2014 Hannu Väisänen

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

package peltomaa.sukija.malaga;

import java.util.Map;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.solr.util.PropertiesUtil;
import peltomaa.sukija.morphology.MorphologyFilter;
import peltomaa.sukija.suggestion.SuggestionFilter;


/**
 * Factory for {@link SuggestionFilter} or {@link SuccesfulFilter}. 
 * <pre class="prettyprint" >
 * &lt;fieldType name="text" class="solr.TextField"
 *   &lt;analyzer&gt;
 *     &lt;filter class="peltomaa.sukija.malagaMalagaMorphologySuggestionFilterFactory"
              dictionary="fi"
              suggestionFile="suggestion.txt"
              success="false"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre> 
 * <p>If success is true filter accepts only words that it recognizes.
 */
public class MalagaMorphologySuggestionFilterFactory extends TokenFilterFactory {
  /** Create a new MalagaMorphologySuggestionFilterFactory.
   */
  public MalagaMorphologySuggestionFilterFactory (Map<String,String> args)
  {
    super (args);
    malagaProjectFile = PropertiesUtil.substituteProperty ("malagaProjectFile", null);
    suggestionFile = PropertiesUtil.substituteProperty ("suggestionFile", null);
    success = getBoolean (args, "success", false);
  }


  @Override
  public TokenFilter create (TokenStream input)
  {
    return new SuggestionFilter (input, MalagaMorphology.getInstance (malagaProjectFile), suggestionFile, success);
  }


  private String malagaProjectFile;
  private String suggestionFile;
  private boolean success;
}

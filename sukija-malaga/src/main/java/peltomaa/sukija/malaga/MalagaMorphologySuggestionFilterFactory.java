/*
Copyright (©) 2012-2015 Hannu Väisänen

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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.solr.util.PropertiesUtil;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionFilter;
import peltomaa.sukija.suggestion.SuggestionParser;


/**
 * Factory for {@link MalagaMorphologySuggestionFilter}.
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
public class MalagaMorphologySuggestionFilterFactory extends MalagaMorphologyFilterFactory implements ResourceLoaderAware {
  /** Create a new MalagaMorphologySuggestionFilterFactory.
   */
  public MalagaMorphologySuggestionFilterFactory (Map<String,String> args)
  {
    super (args);
    suggestionFile = getValue (args, "suggestionFile");
    success = getBoolean (args, "success", false);
  }


  @Override
  public TokenFilter create (TokenStream input)
  {
    return new SuggestionFilter (input, morphology, suggestion, success);
  }


  @Override
  public void inform (ResourceLoader loader) throws IOException
  {
    LOG.info ("inform1 " + loader.getClass().getName());
    if (suggestion == null) {
      InputStream inputStream = loader.openResource (suggestionFile);
      morphology = getMorphology();
      try {
        SuggestionParser parser = new SuggestionParser (morphology, inputStream);
        suggestion = parser.getSuggestions();
      }
      catch (SuggestionParser.SuggestionParserException e)
      {
        LOG.error (e.getMessage());
        if (e.getCause() != null) LOG.error (e.getCause().getMessage());
      }
    }
    LOG.info ("inform2 " + loader.getClass().getName());
  }


  private String suggestionFile;
  private boolean success;
  private static final Logger LOG = LoggerFactory.getLogger (MalagaMorphologySuggestionFilterFactory.class);
  private Vector<Suggestion> suggestion;
}

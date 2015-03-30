/*
Copyright (©) 2013-2015 Hannu Väisänen

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

package peltomaa.sukija.voikko;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
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
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionFilter;
import peltomaa.sukija.suggestion.SuggestionParser;


/**
 * Factory for {@link SuggestionFilter} or {@link SuccesfulFilter}. 
 * <pre class="prettyprint" >
 * &lt;fieldType name="text" class="solr.TextField"
 *   &lt;analyzer&gt;
 *     &lt;filter class="peltomaa.sukija.voikko.VoikkoMorphologySuggestionFilterFactory"
              dictionary="fi"
              path="${user.home}/.voikko"
              libvoikkoPath="/usr/local/lib/libvoikko.so"
              libraryPath=/usr/local/lib/"
              suggestionFile="finnish-suggestion.xml"
              sucess="false"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre> 
 * <p>Arguments path, libvoikkoPath and libraryPath are optional.
 * If they are not here defaults are used.
 */
public class VoikkoMorphologySuggestionFilterFactory
  extends VoikkoMorphologyFilterFactory implements ResourceLoaderAware {

  /** Create a new VoikkoMorphologySuggestionFilterFactory.
   */
  public VoikkoMorphologySuggestionFilterFactory (Map<String,String> args)
  {
    super (args);

    suggestionFile = getValue (args, "suggestionFile");
    success = getBoolean (args, "success", false);

    LOG.info ("dictionary " + dictionary);
    LOG.info ("path " + path);
    LOG.info ("libvoikkoPath " + libvoikkoPath);
    LOG.info ("libraryPath " + libraryPath);
    LOG.info ("suggestionFile " + suggestionFile);
    LOG.info ("success " + success);
  }


  @Override
  public TokenFilter create (TokenStream input)
  {
    LOG.info ("create");
    LOG.info ("dictionary " + dictionary);
    LOG.info ("path " + path);
    LOG.info ("libvoikkoPath " + libvoikkoPath);
    LOG.info ("libraryPath " + libraryPath);
    LOG.info ("suggestionFile " + suggestionFile);
    LOG.info ("success " + success);

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
  private static final Logger LOG = LoggerFactory.getLogger (VoikkoMorphologySuggestionFilterFactory.class);
  private Vector<Suggestion> suggestion;
}

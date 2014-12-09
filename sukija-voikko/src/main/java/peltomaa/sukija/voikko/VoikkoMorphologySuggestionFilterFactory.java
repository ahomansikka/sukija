/*
Copyright (©) 2013-2014 Hannu Väisänen

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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.solr.util.PropertiesUtil;
import peltomaa.sukija.morphology.MorphologyFilter;
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
public class VoikkoMorphologySuggestionFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {
  /** Create a new VoikkoMorphologySuggestionFilterFactory.
   */
  public VoikkoMorphologySuggestionFilterFactory (Map<String,String> args)
  {
    super (args);
    dictionary     = get (args, "dictionary", "fi");
    path           = getValue (args, "path");
    libvoikkoPath  = getValue (args, "libvoikkoPath");
    libraryPath    = getValue (args, "libraryPath");
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
    if (dictionary == null) {
      LOG.error ("VoikkoMorphologySuggestionFilterFactory: dictionary == null.");
      return null;
    }
    else if (path == null) {
      return new SuggestionFilter (input, VoikkoMorphology.getInstance (dictionary), inputStream, success);
    }
    else if ((libvoikkoPath == null) && (libraryPath == null)) {
      return new SuggestionFilter (input, VoikkoMorphology.getInstance (dictionary, path), inputStream, success);
    }
    else if ((libvoikkoPath != null) && (libraryPath != null)) {
      return new SuggestionFilter (input, VoikkoMorphology.getInstance (dictionary, path, libvoikkoPath, libraryPath), inputStream, success);
    }
    else {
      LOG.error ("VoikkoMorphologySuggestionFilterFactory: some parameters are null.");
      return null;
    }
  }


  @Override
  public void inform (ResourceLoader loader) throws IOException
  {
    LOG.info ("inform1 " + loader.getClass().getName());
    inputStream = loader.openResource (suggestionFile);
    LOG.info ("inform2 " + loader.getClass().getName());
  }


  private String getValue (Map<String,String> args, String name)
  {
    return PropertiesUtil.substituteProperty (get(args,name), null);
  }


  private String dictionary;
  private String suggestionFile;
  private String path;
  private String libvoikkoPath;
  private String libraryPath;
  private boolean success;
  private static final Logger LOG = LoggerFactory.getLogger (VoikkoMorphologySuggestionFilterFactory.class);
  private InputStream inputStream;
}

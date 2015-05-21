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

package peltomaa.sukija.voikko;

import java.io.InputStream;
import java.io.IOException;
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
import peltomaa.sukija.morphology.MorphologyFilter;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionFilter;
import peltomaa.sukija.suggestion.SuggestionParser;


/**
 * Factory for {@link MorphologyFilter} or {@link SuggestionFilter} using Voikko morphology.
 * <pre class="prettyprint">
 * &lt;fieldType name="text" class="solr.TextField"
 *   &lt;analyzer&gt;
 *     &lt;filter class="peltomaa.sukija.voikko.VoikkoMorphologyFilterFactory"
              dictionary="fi"
              path="${user.home}/.voikko"
              libvoikkoPath="/usr/local/lib/libvoikko.so"
              libraryPath=/usr/local/lib/"
              suggestionFile="finnish-suggestion.xml"
              sucess="false"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre>
 * <p>Muuttujat path, libvoikkoPath ja libraryPath ovat valinnaisia.
 * Jos niitä ei ole, käytetään oletusarvoja.
 * <p>
 * Muuttujat suggestionFile ja suggest ovat valinnaisia.
 */
public class VoikkoMorphologyFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {
  /** Create a new VoikkoMorphologyFilterFactory.
   */
  public VoikkoMorphologyFilterFactory (Map<String,String> args)
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
    LOG.info ("VoikkoMorphologyFilterFactory.create");
    LOG.info ("dictionary " + dictionary);
    LOG.info ("path " + path);
    LOG.info ("libvoikkoPath " + libvoikkoPath);
    LOG.info ("libraryPath " + libraryPath);

    if (suggestion == null) {
      return new MorphologyFilter (input, morphology);
    }
    else {
      LOG.info ("suggestionFile " + suggestionFile);
      LOG.info ("success " + success);
      return new SuggestionFilter (input, morphology, suggestion, success);
    }
  }


  @Override
  public void inform (ResourceLoader loader) throws IOException
  {
    LOG.info ("inform1 " + loader.getClass().getName());
    morphology = getMorphology();
    if (suggestionFile != null && suggestion == null) {
      InputStream inputStream = loader.openResource (suggestionFile);
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


  private Morphology getMorphology()
  {
    if (dictionary == null) {
      LOG.error ("VoikkoMorphologyFilterFactory: dictionary == null.");
      return null;
    }
    else if (path == null) {
      return VoikkoMorphology.getInstance (dictionary);
    }
    else if ((libvoikkoPath == null) && (libraryPath == null)) {
      return VoikkoMorphology.getInstance (dictionary, path);
    }
    else if ((libvoikkoPath != null) && (libraryPath != null)) {
      return VoikkoMorphology.getInstance (dictionary, path, libvoikkoPath, libraryPath);
    }
    else {
      LOG.error ("VoikkoMorphologyFilterFactory: some parameters are null.");
      return null;
    }
  }


  protected String getValue (Map<String,String> args, String name)
  {
    return PropertiesUtil.substituteProperty (get(args,name), null);
  }


  private Morphology morphology;
  private String dictionary;
  private String path;
  private String libvoikkoPath;
  private String libraryPath;
  private String suggestionFile;
  private boolean success;
  private Vector<Suggestion> suggestion;
  private static final Logger LOG = LoggerFactory.getLogger (VoikkoMorphologyFilterFactory.class);
}

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
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionFilter;
import peltomaa.sukija.suggestion.SuggestionParser;
import org.puimula.libvoikko.*;


/**
 * Factory for {@link VoikkoFilter} or {@link SuggestionFilter} using Voikko morphology.
 * <pre class="prettyprint">
 * &lt;fieldType name="text" class="solr.TextField"
 *   &lt;analyzer&gt;
 *     &lt;filter class="peltomaa.sukija.voikko.VoikkoFilterFactory"
              language="fi"
              path="${user.home}/.voikko"
              libvoikkoPath="/usr/local/lib/libvoikko.so"
              libraryPath="/usr/local/lib/"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre>
 * <p>Muuttujat path, libvoikkoPath ja libraryPath ovat valinnaisia.
 * Jos niitä ei ole, käytetään oletusarvoja.
 * <p>
 */
public class VoikkoFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {
  /** Create a new VoikkoFilterFactory.
   */
  public VoikkoFilterFactory (Map<String,String> args)
  {
    super (args);
    language      = get (args, "language", "fi");
    path          = getValue (args, "path");
    libvoikkoPath = getValue (args, "libvoikkoPath");
    libraryPath   = getValue (args, "libraryPath");

    voikko = VoikkoUtils.getVoikko (language, path, libraryPath, libvoikkoPath);

    LOG.info ("language " + language);
    LOG.info ("path " + path);
    LOG.info ("libvoikkoPath " + libvoikkoPath);
    LOG.info ("libraryPath " + libraryPath);
  }


  @Override
  public TokenFilter create (TokenStream input)
  {
    LOG.info ("VoikkoFilterFactory.create");
    LOG.info ("language " + language);
    LOG.info ("path " + path);
    LOG.info ("libvoikkoPath " + libvoikkoPath);
    LOG.info ("libraryPath " + libraryPath);

    return new VoikkoFilter (input, voikko);
  }


  @Override
  public void inform (ResourceLoader loader) throws IOException
  {
  }


/*
  @Override
  protected void finalize() throws Throwable
  {
    voikko.terminate();
  }
*/

  private String getValue (Map<String,String> args, String name)
  {
    return PropertiesUtil.substituteProperty (get(args,name), null);
  }


  private Voikko voikko;
  private String language;
  private String path;
  private String libvoikkoPath;
  private String libraryPath;
  private static final Logger LOG = LoggerFactory.getLogger (VoikkoFilterFactory.class);
}

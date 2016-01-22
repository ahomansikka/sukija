/*
Copyright (©) 2012-2016 Hannu Väisänen

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
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionFilter;
import peltomaa.sukija.suggestion.SuggestionParser;
import peltomaa.sukija.util.SukijaFilterFactory;


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
public class VoikkoFilterFactory extends SukijaFilterFactory {
  /** Create a new VoikkoFilterFactory.
   */
  public VoikkoFilterFactory (Map<String,String> args)
  {
    super (args);
    createVoikko();
  }


  @Override
  public TokenFilter create (TokenStream input)
  {
    LOG.info ("VoikkoFilterFactory.create");

    return new VoikkoFilter (input, voikko);
  }
}

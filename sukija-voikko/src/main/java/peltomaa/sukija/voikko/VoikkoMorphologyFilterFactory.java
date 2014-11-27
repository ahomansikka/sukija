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

package peltomaa.sukija.voikko;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import peltomaa.sukija.morphology.MorphologyFilter;
import peltomaa.sukija.util.PropertiesUtil;


/**
 * Factory for {@link MorphologyFilter}. 
 * <pre class="prettyprint" >
 * &lt;fieldType name="text" class="solr.TextField"
 *   &lt;analyzer&gt;
 *     &lt;filter class="peltomaa.sukija.voikko.VoikkoMorphologyFilterFactory"
              dictionary="fi"
              path="${user.home}/.voikko"
              libvoikkoPath="/usr/local/lib/libvoikko.so"
              libraryPath=/usr/local/lib/"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre>
 * <p>Arguments path, libvoikkoPath and libraryPath are optional.
 * If they are not here defaults are used.
 */
public class VoikkoMorphologyFilterFactory extends TokenFilterFactory {
  /** Create a new VoikkoMorphologyFilterFactory.
   */
  public VoikkoMorphologyFilterFactory (Map<String,String> args)
  {
    super (args);
    dictionary    = PropertiesUtil.replacePropertyNameWithValue (args.get ("dictionary"));
    path          = PropertiesUtil.replacePropertyNameWithValue (args.get ("path"));
    libvoikkoPath = PropertiesUtil.replacePropertyNameWithValue (args.get ("libvoikkoPath"));
    libraryPath   = PropertiesUtil.replacePropertyNameWithValue (args.get ("libraryPath"));
  }


  @Override
  public TokenFilter create (TokenStream input)
  {
    if (dictionary == null) {
      LOG.error ("VoikkoMorphologyFilterFactory: dictionary == null.");
      LOG.info ("VoikkoMorphologyFilterFactory 1.");
      return null;
    }
    else if (path == null) {
      LOG.info ("VoikkoMorphologyFilterFactory 2.");
      return new MorphologyFilter (input, VoikkoMorphology.getInstance (dictionary));
    }
    else if ((libvoikkoPath == null) && (libraryPath == null)) {
      LOG.info ("VoikkoMorphologyFilterFactory 3.");
      return new MorphologyFilter (input, VoikkoMorphology.getInstance (dictionary, path));
    }
    else if ((libvoikkoPath != null) && (libraryPath != null)) {
      LOG.info ("VoikkoMorphologyFilterFactory 4.");
      return new MorphologyFilter (input, VoikkoMorphology.getInstance (dictionary, path, libvoikkoPath, libraryPath));
    }
    else {
      LOG.error ("VoikkoMorphologyFilterFactory: some parameters are null.");
      return null;
    }
  }


  private String dictionary;
  private String path;
  private String libvoikkoPath;
  private String libraryPath;
  private static final Logger LOG = LoggerFactory.getLogger (VoikkoMorphologyFilterFactory.class);
}

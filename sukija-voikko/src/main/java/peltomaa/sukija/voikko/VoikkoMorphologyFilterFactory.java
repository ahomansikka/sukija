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

import java.io.IOException;
import java.util.Map;
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

    LOG.info ("dictionary " + dictionary);
    LOG.info ("path " + path);
    LOG.info ("libvoikkoPath " + libvoikkoPath);
    LOG.info ("libraryPath " + libraryPath);
  }


  @Override
  public TokenFilter create (TokenStream input)
  {
    LOG.info ("VoikkoMorphologyFilterFactory.create");
    LOG.info ("dictionary " + dictionary);
    LOG.info ("path " + path);
    LOG.info ("libvoikkoPath " + libvoikkoPath);
    LOG.info ("libraryPath " + libraryPath);

    return new MorphologyFilter (input, morphology);
  }


  @Override
  public void inform (ResourceLoader loader) throws IOException
  {
    LOG.info ("inform1 " + loader.getClass().getName());
    morphology = getMorphology();
    LOG.info ("inform2 " + loader.getClass().getName());
  }


  protected Morphology getMorphology()
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


  protected String dictionary;
  protected String path;
  protected String libvoikkoPath;
  protected String libraryPath;
  protected Morphology morphology;
  private static final Logger LOG = LoggerFactory.getLogger (VoikkoMorphologyFilterFactory.class);
}

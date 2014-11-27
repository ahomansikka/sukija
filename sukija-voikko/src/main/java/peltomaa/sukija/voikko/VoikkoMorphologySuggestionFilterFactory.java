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

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import peltomaa.sukija.morphology.MorphologyFilter;
import peltomaa.sukija.util.PropertiesUtil;
import peltomaa.sukija.suggestion.SuggestionFilter;


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
              suggestionFile="suggestion.xml"
              sucess="false"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre> 
 * <p>Arguments path, libvoikkoPath and libraryPath are optional.
 * If they are not here defaults are used.
 */
public class VoikkoMorphologySuggestionFilterFactory extends TokenFilterFactory {
  /** Create a new VoikkoMorphologySuggestionFilterFactory.
   */
  public VoikkoMorphologySuggestionFilterFactory (Map<String,String> args)
  {
    super (args);
    dictionary     = PropertiesUtil.replacePropertyNameWithValue (args.get ("dictionary"));
    path           = PropertiesUtil.replacePropertyNameWithValue (args.get ("path"));
    libvoikkoPath  = PropertiesUtil.replacePropertyNameWithValue (args.get ("libvoikkoPath"));
    libraryPath    = PropertiesUtil.replacePropertyNameWithValue (args.get ("libraryPath"));
    suggestionFile = PropertiesUtil.replacePropertyNameWithValue (args.get ("suggestionFile"));
    success = Boolean.valueOf (PropertiesUtil.replacePropertyNameWithValue (args.get ("success")));
  }


  @Override
  public TokenFilter create (TokenStream input)
  {
    if (dictionary == null) {
      LOG.error ("VoikkoMorphologySuggestionFilterFactory: dictionary == null.");
      return null;
    }
    else if (path == null) {
      return new SuggestionFilter (input, VoikkoMorphology.getInstance (dictionary), suggestionFile, success);
    }
    else if ((libvoikkoPath == null) && (libraryPath == null)) {
      return new SuggestionFilter (input, VoikkoMorphology.getInstance (dictionary, path), suggestionFile, success);
    }
    else if ((libvoikkoPath != null) && (libraryPath != null)) {
      return new SuggestionFilter (input, VoikkoMorphology.getInstance (dictionary, path, libvoikkoPath, libraryPath), suggestionFile, success);
    }
    else {
      LOG.error ("VoikkoMorphologySuggestionFilterFactory: some parameters are null.");
      return null;
    }
  }


  private String dictionary;
  private String suggestionFile;
  private String path;
  private String libvoikkoPath;
  private String libraryPath;
  private boolean success;
  private static final Logger LOG = LoggerFactory.getLogger (VoikkoMorphologySuggestionFilterFactory.class);
}

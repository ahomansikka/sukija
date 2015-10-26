/*
Copyright (©) 2015 Hannu Väisänen

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

package peltomaa.sukija.baseform;

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
import org.puimula.libvoikko.*;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionFilter;
import peltomaa.sukija.suggestion.SuggestionParser;
import peltomaa.sukija.voikko.VoikkoUtils;


public class BaseFormFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {
  /** Tehdään uusi BaseFormFilterFactory.
   */
  public BaseFormFilterFactory (Map<String,String> args)
  {
    super (args);

    language       = get (args, "language", "fi");
    path           = getValue (args, "path");
    libvoikkoPath  = getValue (args, "libvoikkoPath");
    libraryPath    = getValue (args, "libraryPath");
    suggestionFile = getValue (args, "suggestionFile");
    successOnly    = getBoolean (args, "successOnly", false);

    LOG.info ("language " + language);
    LOG.info ("path " + path);
    LOG.info ("libraryPath " + libraryPath);
    LOG.info ("libvoikkoPath " + libvoikkoPath);
    LOG.info ("suggestionFile " + suggestionFile);
    LOG.info ("successOnly " + successOnly);
  }


  @Override
  public TokenFilter create (TokenStream input)
  {
    if (suggestion == null) {
      return new BaseFormFilter (input, voikko, successOnly);
    }
    else {
      return new SuggestionFilter (input, voikko, suggestion, successOnly);
    }
  }


  @Override
  public void inform (ResourceLoader loader) throws IOException
  {
    LOG.info ("inform1 " + loader.getClass().getName());
    voikko = VoikkoUtils.getVoikko (language, path, libraryPath, libvoikkoPath);

    if (suggestionFile != null && suggestion == null) {
      InputStream inputStream = loader.openResource (suggestionFile);
      try {
        SuggestionParser parser = new SuggestionParser (voikko, inputStream);
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


  private String getValue (Map<String,String> args, String name)
  {
    return PropertiesUtil.substituteProperty (get(args,name), null);
  }


  private Voikko voikko;
  private String language;
  private String path;
  private String libvoikkoPath;
  private String libraryPath;
  private String suggestionFile;
  private boolean successOnly;
  private Vector<Suggestion> suggestion = null;
  private static final Logger LOG = LoggerFactory.getLogger (BaseFormFilterFactory.class);
}

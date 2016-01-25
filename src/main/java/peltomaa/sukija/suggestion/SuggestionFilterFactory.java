/*
Copyright (©) 2016 Hannu Väisänen

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


package peltomaa.sukija.suggestion;

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import peltomaa.sukija.util.SukijaFilterFactory;


public class SuggestionFilterFactory extends SukijaFilterFactory implements ResourceLoaderAware {

  /** Tehdään uusi SuggestionFilterFactory.
   */
  public SuggestionFilterFactory (Map<String,String> args)
  {
    super (args);
    suggestionFile = getValue (args, "suggestionFile");
    successOnly    = getBoolean (args, "successOnly", false);
  }


  @Override
  public TokenFilter create (TokenStream input)
  {
    return new SuggestionFilter (input, voikko, suggestion, successOnly);
  }


  public void inform (ResourceLoader loader) throws IOException
  {
    LOG.info ("inform1 " + loader.getClass().getName());
    createVoikko();

    if (suggestionFile != null && suggestion == null) {
      InputStream inputStream = loader.openResource (suggestionFile);
      try {
        SuggestionParser parser = new SuggestionParser (getVoikko(), inputStream);
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


  protected String suggestionFile;
  protected boolean successOnly;
  protected Vector<Suggestion> suggestion = null;
}

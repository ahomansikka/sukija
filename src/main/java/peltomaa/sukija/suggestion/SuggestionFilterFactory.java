/*
Copyright (©) 2016, 2018, 2022 Hannu Väisänen

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
import org.apache.lucene.util.ResourceLoader;
import org.apache.lucene.util.ResourceLoaderAware;
import peltomaa.sukija.util.SukijaFilterFactory;


public class SuggestionFilterFactory extends SukijaFilterFactory implements ResourceLoaderAware {

  /** Tehdään uusi SuggestionFilterFactory.
   */
  public SuggestionFilterFactory (Map<String,String> args)
  {
    super (args);
    suggestionFile = getValue (args, "suggestionFile");
    LOG.info ("SuggestionFilterFactory " + suggestionFile);
  }


  @Override
  public TokenFilter create (TokenStream input)
  {
    if (voikko == null) throw new RuntimeException ("SuggestionFilterFactory: voikko == null.");
    return new SuggestionFilter (input, voikko, parser, successOnly);
  }


  public void inform (ResourceLoader loader) throws IOException
  {
    LOG.info ("SuggestionFilterFactory inform1 " + loader.getClass().getName());

    if (suggestionFile != null) {
      InputStream inputStream = loader.openResource (suggestionFile);
      try {
        parser = new SuggestionParser (getVoikko(), inputStream);
        LOG.info ("SuggestionFilterFactory inform2 " + loader.getClass().getName());
      }
      catch (SuggestionParser.SuggestionParserException e)
      {
        LOG.error (e.getMessage());
        if (e.getCause() != null) LOG.error (e.getCause().getMessage());
      }
    }
    LOG.info ("SuggestionFilterFactory inform3 " + loader.getClass().getName());
  }


  private String suggestionFile;
  private SuggestionParser parser;
}

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


public final class MultiSuggestionFilterFactory extends SuggestionFilterFactory {

  /** Tehdään uusi MultiSuggestionFilterFactory.
   */
  public MultiSuggestionFilterFactory (Map<String,String> args)
  {
    super (args);
    from = getValue (args, "from");
    to   = getValue (args, "to");
  }

  @Override
  public TokenFilter create (TokenStream input)
  {
    return new MultiSuggestionFilter (input, voikko, from, to, suggestionFile, successOnly);
  }


  private String from;
  private String to;
}

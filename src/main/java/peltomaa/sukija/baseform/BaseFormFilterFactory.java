/*
Copyright (©) 2015-2016, 2018, 2021-2022 Hannu Väisänen

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
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;;
import org.apache.lucene.util.ResourceLoader;
import org.apache.lucene.util.ResourceLoaderAware;
import org.apache.solr.common.util.PropertiesUtil;
import org.puimula.libvoikko.*;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionFilter;
import peltomaa.sukija.suggestion.SuggestionParser;
import peltomaa.sukija.util.SukijaFilterFactory;
import peltomaa.sukija.voikko.VoikkoUtils;


public class BaseFormFilterFactory extends SukijaFilterFactory {
  /** Tehdään uusi BaseFormFilterFactory.
   */
  public BaseFormFilterFactory (Map<String,String> args)
  {
    super (args);
  }


  @Override
  public TokenFilter create (TokenStream input)
  {
    if (voikko == null) throw new RuntimeException ("BaseFormFilterFactory: voikko == null.");
    return new BaseFormFilter (input, voikko, successOnly);
  }
}

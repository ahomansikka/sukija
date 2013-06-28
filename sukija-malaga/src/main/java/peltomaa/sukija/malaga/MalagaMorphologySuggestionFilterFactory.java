/*
Copyright (©) 2012-2013 Hannu Väisänen

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

package peltomaa.sukija.malaga;

import java.util.Map;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import peltomaa.sukija.morphology.MorphologyFilter;
import peltomaa.sukija.util.PropertiesUtil;
import peltomaa.sukija.suggestion.SuggestionFilter;


public class MalagaMorphologySuggestionFilterFactory extends TokenFilterFactory {
  /** Create a new MalagaMorphologySuggestionFilterFactory.
   */
  public MalagaMorphologySuggestionFilterFactory (Map<String,String> args)
  {
    super (args);
    malagaProjectFile = PropertiesUtil.replacePropertyNameWithValue (args.get ("malagaProjectFile"));
    suggestionFile = PropertiesUtil.replacePropertyNameWithValue (args.get ("suggestionFile"));
  }


  @Override
  public TokenFilter create (TokenStream input)
  {
    return new SuggestionFilter (input, MalagaMorphology.getInstance (malagaProjectFile), suggestionFile);
  }


  private String malagaProjectFile;
  private String suggestionFile;
}

/*
Copyright (©) 2020, 2022 Hannu Väisänen

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

package peltomaa.sukija.filters;


import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.TokenFilterFactory;


public class HVCompoundWordFilterFactory extends TokenFilterFactory {

  /** Tehdään uusi HVCompoundWordFilterFactory.
   */
  public HVCompoundWordFilterFactory (Map<String,String> args)
  {
    super (args);
  }

  @Override
  public HVCompoundWordFilter create (TokenStream input)
  {
    return new HVCompoundWordFilter (input);
  }
}

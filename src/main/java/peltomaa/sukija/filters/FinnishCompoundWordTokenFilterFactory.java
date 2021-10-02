/*
Copyright (©) 2021 Hannu Väisänen

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
import org.apache.lucene.analysis.util.TokenFilterFactory;
import peltomaa.sukija.util.SukijaFilterFactory;


public class FinnishCompoundWordTokenFilterFactory extends SukijaFilterFactory {
  public FinnishCompoundWordTokenFilterFactory (Map<String, String> args)
  {
    super (args);
    minWordSize = getInt (args, "minWordSize", FinnishCompoundWordTokenFilter.DEFAULT_MIN_WORD_SIZE);
    minSubwordSize = getInt (args, "minSubwordSize", FinnishCompoundWordTokenFilter.DEFAULT_MIN_SUBWORD_SIZE);
    maxSubwordSize = getInt (args, "maxSubwordSize", FinnishCompoundWordTokenFilter.DEFAULT_MAX_SUBWORD_SIZE);
    onlyLongestMatch = getBoolean (args, "onlyLongestMatch", false);
    if (!args.isEmpty()) {
      throw new IllegalArgumentException ("Tuntemattomia parameterja: " + args);
    }
  }


  @Override
  public TokenStream create(TokenStream input)
  {
    return new FinnishCompoundWordTokenFilter (input, minWordSize, minSubwordSize, maxSubwordSize, onlyLongestMatch);
  }


  private final int minWordSize;
  private final int minSubwordSize;
  private final int maxSubwordSize;
  private final boolean onlyLongestMatch;
}

/*
Copyright (©) 2012 Hannu Väisänen

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

package peltomaa.sukija.util;

import java.io.Reader;
import org.apache.lucene.analysis.MappingCharFilter;
import org.apache.lucene.analysis.NormalizeCharMap;


public class LatexUtil {
  private LatexUtil() {}

  public static final MappingCharFilter makeLatexCharFilter (Reader in)
  {
    NormalizeCharMap n = new NormalizeCharMap();
    n.add ("\\-", "");   // Esim. vuok\-ran\-an\-ta\-ja => vuokranantaja.
    n.add ("\"-", "-");  // Esim. linja"-auto => linja-auto.
    n.add ("\\v{s}", "š");
    n.add ("\\v{S}", "Š");
    n.add ("\\v{z}", "ž");
    n.add ("\\v{Z}", "Ž");

    return new MappingCharFilter (n, in);
  }
}

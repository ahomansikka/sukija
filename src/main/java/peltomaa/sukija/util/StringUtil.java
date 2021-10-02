/*
Copyright (©) 2009-2011, 2017, 2020-2021 Hannu Väisänen

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

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Set;


/** String utilities.<p>
 *
 * The methods of this class throw a {@code NullPointerException}
 * if any of the arguments is {@code null}.
 */
public class StringUtil {
  public static final boolean isVowel (char c)
  {
    switch (c) {
      case 'a':
      case 'e':
      case 'i':
      case 'o':
      case 'u':
      case 'y':
      case 'ä':
      case 'ö':
        return true;
      default:
        return false;
    }
  }


  /**
   * Poista sanan alusta ja lopusta merkit, jotka eivät ole kirjaimia eivätkä numeroita.
   */
  public static final String trimNotLetterOrDigit (String s)
  {
    int len = s.length();

    if (len == 0) return s;

    int start = 0;
    int end = len;
    for (start = 0; start < len && !Character.isLetterOrDigit (s.charAt(start)); start++)
    {
    }
    for (end = len; end >= start && !Character.isLetterOrDigit (s.charAt(end - 1)); end--)
    {
    }
    if (start > 0 || end < len) {
      if (start < end) {
        return s.substring (start, (end - start));
      }
    }
    return s;
  }


  /**
   * Liitä kaksi merkkijonoa yhteen. Laita väliin 'separator' mikäli 'u' loppuu
   * samaan ääntiöön, jolla 'v' alkaa. Isot ja pienet kirjaimet katsotaan
   * samaksi, esimerkiksi 'a' == 'A'.
   */
  public static final String joinIfVowel (String u, String separator, String v)
  {
    final char cu = Character.toLowerCase (u.charAt(u.length()-1));
    final char cv = Character.toLowerCase (v.charAt(0));

    if (isVowel(cu) && (cu == cv)) {
//System.out.println ("Ding " + u + " " + v);
      return (u + separator + v);
    }
    else {
//System.out.println ("Dong " + u + " " + v);
      return (u + v);
    }
  }



/* Testausta varten. (-:

  public static void main (String[] args)
  {
  }
*/
}

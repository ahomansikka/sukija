/*
Copyright (©) 2009-2015 Hannu Väisänen

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

import java.util.regex.Pattern;

/**
 * Utilities for regular expressions.
 */
public final class RegexUtil {
  private RegexUtil() {}

  /**
   * Convert a character sequence to regular expression string.<p>
   *
   * Expressions {@code %A %C %O %U %V %%} in {@code s} are replaced with these regular
   * expressions

<pre>
%A [aä]
%C [bcdfghjklmnpqrsštvwxzž]
%O [oö]
%U [uy]
%V [aeiouyäö]
%% %
</pre>


{@code C} matches all lowercase consonants,
{@code V} matches all lowercase vowels and
{@code aä}, {@code oö} and {@code uy} are back and front vowels matching each other.<p>

Note that in Finnish, letters {@code ä} and {@code ö} are not accented
forms of {@code a} and {@code o} but quite different letters like
{@code v} and {@code w} in English (which, by the way, are not two
different letters in Finnish but only two forms of the same letter. :-).

   * @param s    A character sequence.
   */
  public static final String makePatternString (String s)
  {
    String u = s;
    for (int i = 0; i < pattern.length; i++) {
      u = u.replace (pattern[i][0], pattern[i][1]);
    }
//System.out.println (s + " " + u);
    return u;
  }


  /**
   * Compile character sequence into {@code Pattern}.<p>
   *
   * This function calls {@link #makePatternString(String)}
   * and compiles the character string that function returns.
   *
   * @param s    A character sequence.
   */
  public static final Pattern makePattern (String s)
  {
    return Pattern.compile (makePatternString (s));

// Pattern.CANON_EQ does not work. )-:
//  return Pattern.compile (makePatternString (s, FI), Pattern.CANON_EQ);
  }


  private static final String pattern[][] = {
    {"%A", "[aä]"},
    {"%C", "[bcdfghjklmnpqrsštvwxzž]"},
    {"%O", "[oö]"},
    {"%U", "[uy]"},
    {"%V", "[aeiouyäö]"},
    {"%%", "%"}
  };
}

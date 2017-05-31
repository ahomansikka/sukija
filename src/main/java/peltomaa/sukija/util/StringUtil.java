/*
Copyright (©) 2009-2011, 2017 Hannu Väisänen

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
   * Count the number of chars in a character sequence.
   *
   * @param s  A character sequence.
   * @param c  Character that is counted.
   *
   * @return   Number of characters in a character sequence.
   */
  public static final int count (CharSequence s, char c)
  {
    int sum = 0;

    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == c) {
        sum++;
      }
    }
    return sum;
  }


  /**
   * Translate characters.<p>
   *
   * Each character {@code from[i]} in {@code sb} is translated to {@code to[i]}.<p>
   *
   * For example {@code translate (sb, "ab", "xy"} converts {@code abc} to {@code xyc}.
   */
  public static final void translate (StringBuffer sb, CharSequence from, CharSequence to)
  {
    for (int i=0; i < sb.length(); i++) {
      for (int j=0; j < from.length() && j < to.length(); j++) {
        if (sb.charAt(i) == from.charAt(j)) {
          sb.setCharAt (i, to.charAt(j));
        }
      }
    }
  }

  /**
   * Translate characters.<p>
   *
   * Each character {@code from[i]} in {@code s} is translated to {@code to[i]}.<p>
   *
   * For example {@code translate (sb, "ab", "xy"} converts {@code abc} to {@code xyc}.
   */
  public static final String translate (String s, CharSequence from, CharSequence to)
  {
    StringBuffer sb = new StringBuffer (s);
    translate (sb, from, to);
    return sb.toString();
  }


/*
  public static final String translate (String s, CharSequence[] from, CharSequence[] to)
  {
    StringBuffer b = new StringBuffer();
    boolean flag = true;

    for (int i=0; i < s.length(); i++) {
      for (int j=0; j < from.length() && j < to.length; j++) {
        if (s.charAt(i) == from.charAt(j)) {
          b.append (to[j]);
          flag = false;
        }
      }
      if (flag)
        b.append (s.charAt(i));
      flag = true;
    }

    return b.toString();
  }
*/


  /**
   * Replace each char sequence s1 with char sequence s2 in s and return the new char
   * sequence as a StringBuffer.
   *
   * @param s   Char sequence to be searched for replacements.
   * @param s1  Char sequence that is to be replaced.
   * @param s2  Char sequence that replaces each occurence of s1.
   *
   * @return  The new char sequence.
   */
  public static final StringBuffer replace (CharSequence s, CharSequence s1, CharSequence s2)
  {
    if (s1.length() == 0) {         // Check for empty char sequence.
      return new StringBuffer (s);
    }

    int start = 0;

    StringBuffer b = new StringBuffer();

    while (start < s.length()) {
      int j = search (s, start, s.length(), s1, 0, s1.length());
      if (j < 0) {
        break;
      }
      else {
        CharSequence u = s.subSequence (start, j);
        b.append (u);             // Append start of word.
        b.append (s2);            // Append replacement.
        start = j + s1.length();  // Continue searching after the previous match.
      }
    }

    if (start < s.length()) {
      b.append (s.subSequence (start, s.length())); // Append end of word.
    }
    return b;
  }


  /**
   * Erase from string buffer s all characters that are in char sequence p.<p>
   *
   * For example, {@code erase (s, "[]"} converts
   * {@code abc[def]gh[ijk]lm} to {@code abcdefghijklm}.<p>
   */
  public static final void erase (StringBuffer s, CharSequence p)
  {
    int n = 0;

    for (int i = 0; i < s.length(); i++) {
      if (indexOf (p, s.charAt(i)) == -1) {  // Char s.charAt(i) is not in p.
        s.setCharAt (n, s.charAt(i));        // Move it to the correct position in s.
        n++;
      }
    }
    s.delete (n, s.length());
  }


  /**
   * Erase from string buffer s all characters that are between from and to.<p>
   *
   * For example, {@code erase (s, '[', ']')} converts
   * {@code abc[def]gh[ijk]lm} to {@code abcghlm}.<p>
   *
   * If {@code s} has no character {@code from} only character {@code to} is deleted.
   * If {@code s} has no char {@code to}, all characters between {@code from}
   * and the end of {@code s} are deleted. 
   * 
   */
  public static final void erase (StringBuffer s, char from, char to)
  {
    int n = 0;
    int i = 0;

    while (i < s.length()) {
      if (s.charAt(i) == from) {
        while ((i < s.length()) && (s.charAt(i) != to)) {
          i++;
        }
      }
      else if (s.charAt(i) != to) {
        s.setCharAt (n, s.charAt(i));
        n++;
      }
      i++;
    }
    s.delete (n, s.length());
  }


  /**
   * Return the first occurence of {@code c} in {@code s.subSequence(start,end)}
   * or -1 if {@code c} is not found.
   */
  public static final int indexOf (CharSequence s, char c, int start, int end)
  {
    for (int i = start; i < end; i++) {
      if (s.charAt(i) == c) {
        return i;
      }
    }
    return -1;
  }


  /**
   * Return the first occurence of {@code c} in {@code s}
   * or -1 if {@code c} is not found.
   */
  public static final int indexOf (CharSequence s, char c)
  {
    return indexOf (s, c, 0, s.length());
  }


  /**
   * Search {@code v.subSequence(first2,last2)} in
   *        {@code u.subSequence(first1,last1)}
   * and return the index of first occurence of
   * {@code v.subSequence(first2,last2)}.
   * Return -1 if {@code v.subSequence(first2,last2)} is not found.
   */
  public static final int search (CharSequence u, int first1, int last1,
                                  CharSequence v, int first2, int last2)
  {
    // This code is modified from search() algorithm in GNU Standard
    // Template Library (from file stl_alog.h).

    // Test for empty ranges.
    //
    if (first1 == last1 || first2 == last2) return -1;

    // Test for pattern of length 1.
    //
    int p1 = first2 + 1;
    if (p1 == last2) return indexOf (u, v.charAt(first2), first1, last1);

    // General case.
    //
    int p;
    int current = first1;

    for (;;) {
      first1 = indexOf (u, v.charAt(first2), first1, last1);
      if (first1 == -1) return -1;

      p = p1;
      current = first1 + 1;
      if (current == last1) return -1;

      while (u.charAt(current) == v.charAt(p)) {
        if (++p == last2) return first1;

        if (++current == last1) return -1;
      }
      first1++;
    }
  }


  /**
   * Return the index of the first occurence of char sequence v
   * in char sequence u or -1 if v is not found.
   */
  public static final int indexOf (CharSequence u, CharSequence v)
  {
    return search (u, 0, u.length(), v, 0, v.length());
  }


  /**
   * Swap s.charAt(i) and s.charAt(j).
   */
  public static final void swap (StringBuffer s, int i, int j)
  {
    final char c = s.charAt (i);
    s.setCharAt (i, s.charAt (j));
    s.setCharAt (j, c);
  }


  /**
   * Join arrays.<p>
   *
   * For example, if {@code x = {"a", "b"}} and
   * {@code y = {"c", "d"}} then
   * {@code join (x, y)} returns
   * {@code {"a", "b", "c", "d"}}.
   */
  public static final String[] join (String[]... u)
  {
    int length = 0;
    for (int i = 0; i < u.length; i++) {
      length += u[i].length;
    }

    String[] p = new String[length];
    int n = 0;

    for (int i = 0; i < u.length; i++) {
      System.arraycopy (u[i], 0, p, n, u[i].length);
      n += u[i].length;
    }
    return p;
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


/*
  public static void main (String[] args)
  {
    StringBuffer u = new StringBuffer ("qwertyuiopåasdfgjklöäzxcvbnm");
    StringBuffer v = new StringBuffer ("bnmx");
    System.out.println (search (u, 0, u.length(), v, 0, v.length()));

    for (int i = 0; i < u.length(); i++) {
      System.out.println (search (u, 0, u.length(), u, i, u.length()));
    }
    for (int i = 0; i < u.length(); i++) {
      System.out.println (search (u, 0, u.length(), u, i, i+1));
    }

/*
    StringBuffer sb = new StringBuffer ("abc");
    translate (sb, "ab", "xy");
    System.out.println (sb.toString());

    sb.replace (0, sb.length(), "abc[def]gh[ijk]lm");
    erase (sb, "[]");
    System.out.println (sb.toString());


    sb.replace (0, sb.length(), "abc[def]gh[ijk]lm");
    erase (sb, '[', ']');
    System.out.println (sb.toString());

    sb.replace (0, sb.length(), "abcdefghijklm");
    erase (sb, '[', ']');
    System.out.println (sb.toString());

    sb.replace (0, sb.length(), "[def]gh[ijk]lm");
    erase (sb, '[', ']');
    System.out.println (sb.toString());

    sb.replace (0, sb.length(), "abc[def]gh[ijk]");
    erase (sb, '[', ']');
    System.out.println (sb.toString());

    sb.replace (0, sb.length(), "abcdef]ghijklm");
    erase (sb, '[', ']');
    System.out.println (sb.toString());

    sb.replace (0, sb.length(), "abc[defghijklm");
    erase (sb, '[', ']');
    System.out.println (sb.toString());
  }
*/
}

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

package peltomaa.sukija.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Set;


// Katso https://stackoverflow.com/questions/3656762/n-gram-generation-from-a-sentence


public final class NgramUtils {
  private NgramUtils() {}


  public static Collection<String> ngram (Collection<String> result, String s)
  {
    final String[] u = DASH.split (s);
    for (int i = 1; i <= u.length; i++) {
      result.addAll (ngram (u, i));
    }
    return result;
  }


  public static Collection<String> ngram (Collection<String> result, String[] s, int n)
  {
    for (int i = 0; i < s.length-n+1; i++) {
      StringBuilder sb = new StringBuilder();
      for (int k = 0; k < n; k++) {
        if (k > 0) {
        sb.append (f(sb,s[i+k]));
//          sb.append ("-");
        }
        sb.append (s[i+k]);
      }
//System.out.println ("unique_ngram " + n + " " + sb.toString());
      result.add (sb.toString());
    }
    return result;
  }


  public static List<String> ngram (String s)
  {
    final String[] u = DASH.split (s);
    final List<String> list = ngram (u, 1);
    for (int i = 2; i <= u.length; i++) {
      list.addAll (ngram (u, i));
    }
    return list;
  }


  public static List<String> ngram (String[] s, int n)
  {
    List<String> list = new ArrayList<String>();

    for (int i = 0; i < s.length-n+1; i++) {
      StringBuilder sb = new StringBuilder();
      for (int k = 0; k < n; k++) {
        if (k > 0) {
        sb.append (f(sb,s[i+k]));
//          sb.append ("-");
        }
        sb.append (s[i+k]);
      }
//System.out.println ("unique_ngram " + n + " " + sb.toString());
      list.add (sb.toString());
    }
    return list;
  }



  /** Palautetaan kaikki ngrammit.<p>

      linja-auto-opisto => linja, auto, opisto, linja-auto, auto-opisto, linja-auto-opisto
  */
  public static Set<String> unique_ngram (String s)
  {
    final String[] u = DASH.split (s);
    final Set<String> set = unique_ngram (u, 1);

//  for (int i = 2; i <= u.length-1; i++) {
    for (int i = 2; i <= u.length; i++) {
      set.addAll (unique_ngram (u, i));
    }
    return set;
  }


  /** Palautetaan kaikki n:n pituiset ngrammit.<p>

      unique_ngram ("linja-auto-opisto",1) =>
      linja-auto-opisto => linja, auto, opisto
  */
  public static Set<String> unique_ngram (String[] s, int n)
  {
    Set<String> set = new HashSet<String>();

    for (int i = 0; i < s.length-n+1; i++) {
      StringBuilder sb = new StringBuilder();
      for (int k = 0; k < n; k++) {
        if (k > 0) {
        sb.append (f(sb,s[i+k]));
//          sb.append ("-");
        }
        sb.append (s[i+k]);
      }
//System.out.println ("unique_ngram " + n + " " + sb.toString());
      set.add (sb.toString());
    }
    return set;
  }


  private static final String f (CharSequence u, CharSequence v)
  {
    if ((u.charAt(u.length()-1) == v.charAt(0)) && StringUtil.isVowel(v.charAt(0))) {
      return "-";
    }
    else {
      return "";
    }
  }


  private static final Pattern DASH = Pattern.compile ("-");


  public static void main (String[] args)
  {
    try {
//      final String s = "Tämä-on-koe-merkki-jono.";
      final String s = "linja-auto-opisto";
      System.out.println (s + " 0 " + s.length());

      Set<String> set = unique_ngram (s);
      for (String u : set) {
        final int n = s.indexOf (u);
        System.out.println (u + " " + n + " " + u.length());
      }
    }
    catch (Throwable t)
    {
      t.printStackTrace (System.out);
    }
  }
}

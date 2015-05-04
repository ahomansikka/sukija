/*
Copyright (©) 2014-2015 Hannu Väisänen

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

package peltomaa.sukija.hyphen;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.lucene.analysis.TokenStream;
import peltomaa.sukija.util.SukijaFilter;


/**
Poistetaan sanasta tarpeettomat yhdysviivat.<p>

Esimerkiksi "poissa-olo" => poissaolo, mutta "linja-auto" => linja-auto.<p>

Lisäksi sana katkaistaan yhdysviivan kohdalta ja jatkossa yritetään muuttaa
perusmuotoon jokainen osa erikseen.<p>

Esimerkiksi "poissa-olo" => poissa-olo, poissaolo, poissa, olo<p>
"linja-auto" => linja-auto, linja, auto<p>
"Maija-niminen" => Maija-niminen, Maijaniminen, Maija, niminen

*/
public final class HyphenFilter extends SukijaFilter {
  public HyphenFilter (TokenStream in, String regex, String replacement)
  {
    super (in);
    this.pattern = Pattern.compile (regex);
    this.replacement = replacement;
    if (LOG.isDebugEnabled()) LOG.debug ("Tehdään luokka " + getClass().getName() + ".");
  }


  public HyphenFilter (TokenStream in)
  {
    this (in, "-+|\"-+|–+|''-+|[.]-+", "-");
  }


  protected void filter (String word)
  {
    v.clear();
    v.add (word);

    final String[] s = pattern.split (word);
//    if (LOG.isDebugEnabled()) LOG.debug (word + " " + s.length);

    if (s.length > 1) {
      final String w = pattern.matcher(word).replaceAll(replacement);
//      System.out.println (word + " " + w);
      v.addAll (Arrays.asList(s));
      if (!word.equals(w)) v.add (w);
      final String h = dehyphen (s);
      if (h != null) v.add (h);
//      for (String u : v) System.out.println ("    " + u);
    }  
    iterator = v.iterator();
    if (LOG.isDebugEnabled() && v.size() > 1) LOG.debug (word + " " + v.toString());
  }


  /** Poistetaan sanasta tarpeettomat väliviivat.
   */
  private String dehyphen (String[] s)
  {
    boolean flag = false;

    sb.delete (0, sb.length());
    for (int i = 0; i < s.length; i++) {
      sb.append (s[i]);
      if (i < s.length-1) {
        if ((s[i].charAt(s[i].length()-1) == s[i+1].charAt(0)) && isVowel(s[i+1].charAt(0))) {
          sb.append (replacement);
//System.out.println ("Blong " + i + " " + sb.toString());
        }
        else {
//System.out.println ("Bling " + i + " " + sb.toString());
          flag = true;
        }
      }
    }
    return (flag ? sb.toString() : null);
  }


  private boolean isVowel (char c)
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


  private static final Logger LOG = LoggerFactory.getLogger (HyphenFilter.class);
  private final Pattern pattern;
  private final String replacement;
  private Vector<String> v = new Vector<String>();
  private StringBuilder sb = new StringBuilder(500);
}

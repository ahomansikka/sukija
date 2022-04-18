/*
Copyright (©) 2015-2016, 2018, 2020-2022 Hannu Väisänen

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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttributeImpl;


public final class Constants {
  private Constants() {}

  /** Sanaa ei tunnistettu eikä tuntemattomia sanoja indeksoida. */
  public static final int NOT_INDEXED = 0;

  // Arvojen täytyy olla kakkosen potensseja: 2, 4, 8, 16, 32, ...


  /** Tavallinen sana. */
  public static final int WORD = 2;

  /** Sana, jossa on yhdysviiva joko oikein tai väärin, esim. linja-auto tai kuu-kausi. */
  public static final int COMPOUND_WORD = 4;

  /** LaTeXissa määritelty yhdyssana, esim. linja"-auto. */
  public static final int LATEX_COMPOUND_WORD  = 8;

  /** Sana, jossa on lateksin taivutuskomento, esim. vuok\-ra. */
  public static final int LATEX_HYPHEN = 16;

  /** Sana, jossa on hakasulut, esim. sitte[n]. */
  public static final int BRACKET = 32;

  /** Sana, jonka vfst-morfologia tunnisti. */
  public static final int FOUND = 64;

  /** Sana, jonka joku Suggestion-luokista tunnisti. */
  public static final int SUGGEST = 128;

  /** Sana, jote ei ole tunnistettu. */
  public static final int UNKNOWN = 256;

  /** Sama kuin HVTokenizer.jflex-tiedostossa oleva LATEX_HYPHEN, vaikka näyttää erilaiselta.
   */
  public static final Pattern RE_LATEX_HYPHEN = Pattern.compile ("\\\\-");


  /** Sama kuin HVTokenizer.jflex-tiedostossa oleva LATEX_COMPOUND_WORD, vaikka näyttää erilaiselta.
   */
  public static final Pattern RE_LATEX_COMPOUND_WORD = Pattern.compile ("\"--?|''-|'-|--");


  public static final boolean hasFlag (FlagsAttribute flagsAtt, int flag)
  {
//System.out.println ("hasFlag " + flag + " " + flagsAtt.getFlags() + " " + (flagsAtt.getFlags() & flag));
    return ((flagsAtt.getFlags() & flag) != 0);
  }

  public static final boolean hasAllFlags (FlagsAttribute flagsAtt, int... flags)
  {
    for (int i = 0; i < flags.length; i++) {
      if (hasFlag (flagsAtt, flags[i])) return false;
    }
    return true;
  }

  public static final boolean hasAnyFlag (FlagsAttribute flagsAtt, int... flags)
  {
    for (int i = 0; i < flags.length; i++) {
      if (hasFlag (flagsAtt, flags[i])) return true;
    }
    return false;
  }


  public static final void addFlags (FlagsAttribute flagsAtt, int... flags)
  {
    for (int i = 0; i < flags.length; i++) {
      flagsAtt.setFlags (flagsAtt.getFlags() | flags[i]);
    }
  }


  public static final void removeFlags (FlagsAttribute flagsAtt, int... flags)
  {
    for (int i = 0; i < flags.length; i++) {
      flagsAtt.setFlags (flagsAtt.getFlags() & ~flags[i]);
    }
  }


  public static final void clearFlags (FlagsAttribute flagsAtt)
  {
    flagsAtt.setFlags (flagsAtt.getFlags() & 0);
  }


  public static final String toString (int n)
  {
    final FlagsAttribute flagsAtt = new FlagsAttributeImpl();
    addFlags (flagsAtt, n);
    return toString (flagsAtt);
  }


  public static final String toString (FlagsAttribute flagsAtt)
  {
//System.out.println ("Constants.toString " + flagsAtt.getFlags());
    final StringBuilder sb = new StringBuilder();
    sb.append (toString (flagsAtt, WORD)).append (" ");
    sb.append (toString (flagsAtt, COMPOUND_WORD)).append (" ");
    sb.append (toString (flagsAtt, LATEX_COMPOUND_WORD)).append (" ");
    sb.append (toString (flagsAtt, LATEX_HYPHEN)).append (" ");
    sb.append (toString (flagsAtt, BRACKET)).append (" ");
    sb.append (toString (flagsAtt, FOUND)).append (" ");
    sb.append (toString (flagsAtt, SUGGEST)).append (" ");
    sb.append (toString (flagsAtt, UNKNOWN)).append (" ");
    final String s = SPACES.matcher(sb.toString()).replaceAll (" ");
    return s.substring (0, s.length()-1);
  }


  public static final String flagToString (int n)
  {
    final String s = flagMap.get (n);
    if (s == null) {
      throw new RuntimeException ("Constants: ei lippua " + n + ".");
    }
    else {
      return s;
    }
  }


  private static final String toString (FlagsAttribute flagsAtt, int flag)
  {
    return (hasFlag (flagsAtt, flag) ? flagToString(flag) : "");
  }


  private static final Map<Integer,String> flagMap = new HashMap<Integer,String>();
  static {
    flagMap.put (WORD, "WORD");
    flagMap.put (COMPOUND_WORD, "COMPOUND_WORD");
    flagMap.put (LATEX_COMPOUND_WORD, "LATEX_COMPOUND_WORD");
    flagMap.put (LATEX_HYPHEN, "LATEX_HYPHEN");
    flagMap.put (BRACKET, "BRACKET");
    flagMap.put (FOUND,   "FOUND");
    flagMap.put (SUGGEST, "SUGGEST");
    flagMap.put (UNKNOWN, "UNKNOWN");
  }


  private static final Pattern SPACES = Pattern.compile ("\\s+");
}

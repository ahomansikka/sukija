/*
Copyright (©) 2015-2016, 2018 Hannu Väisänen

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


public final class Constants {
  private Constants() {}

  // Arvojen täytyy olla kakkosen potensseja: 2, 4, 8, 16, 32, ...
  //
  public static final int WORD    =   2; // Tiedostosta luettu tavallinen sana.
  public static final int HYPHEN  =   4; // Sana, jossa on väliviiva (esim. linja-auto).
  public static final int BRACKET =   8; // Sana, jossa on hakasulut (esim. sitte[n]).
  public static final int FOUND   =  16; // Sana, jonka Voikko tunnisti muutoksitta.
  public static final int SUGGEST =  32; // Sana, jonka joku Suggestion-luokista tunnisti.
  public static final int UNKNOWN =  64; // Sana, jota morfologia tai filtteri ei ole tunnistanut.
  public static final int DASH    = 128; // Sana, jossa on LaTeXin taivutuskomento "\-", esim. vuok\-ra.

  public static final boolean hasFlag (FlagsAttribute flagsAtt, int flag)
  {
    return ((flagsAtt.getFlags() & flag) != 0);
  }


  public static final void removeFlags (FlagsAttribute flagsAtt, int... flags)
  {
    for (int i = 0; i < flags.length; i++) {
      flagsAtt.setFlags (flagsAtt.getFlags() & ~flags[i]);
    }
  }


  public static final String toString (FlagsAttribute flagsAtt)
  {
//System.out.println ("Constants.toString " + flagsAtt.getFlags());
    final StringBuilder sb = new StringBuilder();
    sb.append (toString (flagsAtt, WORD)).append (" ");
    sb.append (toString (flagsAtt, HYPHEN)).append (" ");
    sb.append (toString (flagsAtt, BRACKET)).append (" ");
    sb.append (toString (flagsAtt, FOUND)).append (" ");
    sb.append (toString (flagsAtt, SUGGEST)).append (" ");
    sb.append (toString (flagsAtt, UNKNOWN)).append (" ");
    sb.append (toString (flagsAtt, DASH)).append (" ");
    final String s = sb.toString();
    return s.substring (0, s.length()-1);
  }


  private static final String valueToString (int n)
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
    return (hasFlag (flagsAtt, flag) ? valueToString(flag) : "");
  }


  private static final Map<Integer,String> flagMap = new HashMap<Integer,String>();
  static {
    flagMap.put (WORD,    "WORD");
    flagMap.put (HYPHEN,  "HYPHEN");
    flagMap.put (BRACKET, "BRACKET");
    flagMap.put (FOUND,   "FOUND");
    flagMap.put (SUGGEST, "SUGGEST");
    flagMap.put (UNKNOWN, "UNKNOWN");
    flagMap.put (DASH,    "DASH");
  }
}

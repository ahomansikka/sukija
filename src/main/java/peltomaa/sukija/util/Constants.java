/*
Copyright (©) 2015 Hannu Väisänen

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
  public static final int WORD = 2;
  public static final int HYPHEN = 4;  // Linja-auto.
  public static final int BRACKET = 8; // []
  public static final int UNKNOWN = 16; // Sana, jota morfologia tai filtteri ei ole tunnistanut.

  public static final boolean hasFlag (FlagsAttribute flagsAtt, int flag)
  {
    return ((flagsAtt.getFlags() & flag) != 0);
  }

  public static final Pattern HYPHEN_REGEX = Pattern.compile ("-+|\"-+|–+|''-+|'-+|[.]-+");


  public static final String toString (FlagsAttribute flagsAtt)
  {
    final int n = flagsAtt.getFlags();
    final StringBuilder sb = new StringBuilder();
    sb.append (toString (flagsAtt, WORD)).append (" ");
    sb.append (toString (flagsAtt, HYPHEN)).append (" ");
    sb.append (toString (flagsAtt, BRACKET)).append (" ");
    sb.append (toString (flagsAtt, UNKNOWN)).append (" ");
    final String s = sb.toString();
    return s.substring (0, s.length()-1);
  }


  public static final String toString (int n)
  {
    final String s = flagMap.get (n);
    return ((s == null) ? "" : s);
  }


  private static final String toString (FlagsAttribute flagsAtt, int flag)
  {
    return (hasFlag (flagsAtt, flag) ? toString(flag) : "");
  }


  private static final Map<Integer,String> flagMap = new HashMap<Integer,String>();
  static {
    flagMap.put (WORD,    "WORD");
    flagMap.put (HYPHEN,  "HYPHEN");
    flagMap.put (BRACKET, "BRACKET");
    flagMap.put (UNKNOWN, "UNKNOWN");
  }
}

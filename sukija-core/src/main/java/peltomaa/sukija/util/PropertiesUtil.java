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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.ResourceBundle;

public class PropertiesUtil {
  private PropertiesUtil() {}

  public static final void configure (ResourceBundle rb) throws IOException
  {
//    systemProperties.store (new OutputStreamWriter(System.out), null);
    Set<String> keySet = rb.keySet();
    Iterator<String> i = keySet.iterator();
    while (i.hasNext()) {
      String u = i.next();
      String s = replacePropertyNameWithValue (rb.getString (u));
//      System.out.println (u + " " + rb.getString (u) + " " + s);
      if (systemProperties.getProperty(u) == null) {
        System.getProperties().setProperty (u, s);
      }
    }
//    System.out.println (replacePropertyNameWithValue ("${huuhaa}/dingdong"));
//    System.out.println ("=====================");
//    systemProperties.store (new OutputStreamWriter(System.out), null);
//    System.exit(1);
  }


  /** Get property that has a key 'key' as a String array.
   *  Return null if there no such property.
   *  First, this function calls <code>System.getProperties().getProperty(key)</code>
   *  and, if the return value is not null, then converts
   *  it to String array by calling <code>String.split(regex)</code>.<p>
   *
   *  Then this function replaces all property names (e.g. ${user.home}) with
   *  their values and returns the result.<p>
   *
   *  This function is used if property value is a list of strings
   *  e.g. a list of files or directories to be indexed.
   */
  public static final String[] getPropertyArray (String key, String regex)
  {
    final String s = System.getProperties().getProperty (key);
    if (s == null) return null;
    String v[] = s.split (regex);
    String u[] = new String[v.length];
    for (int i = 0; i < v.length; i++) {
      u[i] = replacePropertyNameWithValue (v[i]);
    }
    return u;
  }


  /** Esim "${user.home}" korvataan "user.home"n arvolla.
   */
  public static final String replacePropertyNameWithValue (String s)
  {
    Matcher m = pattern.matcher (s);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      final String value = systemProperties.getProperty (m.group (1));
      if (value != null) m.appendReplacement (sb, value);
    }
    m.appendTail (sb);
    return sb.toString();
  }

  // Jos merkkijono on "${user.home}/.sukija", tämä tunnistaa osan "${user.home}"
  // ja ulommaiset sulut (group(1)) osan "user.home".
  private static final Pattern pattern = Pattern.compile ("[$][{]((\\p{L}|[-.0-9])+)[}]");
  private static final Properties systemProperties = System.getProperties();
}

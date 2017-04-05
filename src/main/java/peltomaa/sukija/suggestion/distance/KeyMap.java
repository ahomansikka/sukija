/*
Copyright (©) 2017 Hannu Väisänen

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

package peltomaa.sukija.suggestion.distance;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;


class KeyMap extends HashMap<String,HashSet<StringMap>> {
  public KeyMap (int keyLength)
  {
    this.keyLength = keyLength;
  }


  public void putValue (String key, StringMap value)
  {
    if (!containsKey (key)) {
      HashSet<StringMap> set = new HashSet<StringMap>();
      set.add (value);
      put (key, set);
    }
    else {
      get(key).add (value);
    }
  }


  public static final KeyMap newInstance (String file, int keyLength) throws FileNotFoundException, IOException
  {
    StringMap stringMap = new StringMap();
    stringMap.read (file);

    KeyMap keyMap = new KeyMap (keyLength);

    for (String s : stringMap.keySet()) {
      final String key = keyMap.makeKey (s);
      keyMap.putValue (key, new StringMap (s, stringMap.get(s)));
//System.out.println (key + " " + s + " " + smap.get(s).getClass().getName());
    }
    return keyMap;
  }


  public String makeKey (String s)
  {
    if (s.length() < keyLength) {
      return s;
    }
    else {
      return s.substring (0, keyLength);
    }
  }


  private int keyLength;
}

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
import java.util.Iterator;
import java.util.Set;
import org.apache.lucene.search.spell.StringDistance;


public class Distance {
  public Distance (StringDistance stringDistance, String fileName, int keyLength, double threshold)
    throws FileNotFoundException, IOException
  {
    this.stringDistance = stringDistance;
    this.keyMap = KeyMap.newInstance (fileName, keyLength);
    this.threshold = threshold;
  }


  public String bestMatch (String word)
  {
    final String key = keyMap.makeKey (word);

    if (keyMap.containsKey (key)) {
      return match (key, word);
    }
    else {
      return null;
    }
  }


  private String match (String key, String word)
  {
    Iterator<StringMap> i = keyMap.get(key).iterator();
    Entry entry = new Entry();

    while (i.hasNext()) {
      StringMap m = i.next();
      for (String baseForm : m.keySet()) {
        bestMatch (entry, baseForm, word, m.get(baseForm));
      }
    }
    if (entry.distance >= threshold) {
      return entry.baseForm;
    }
    else {
      return null;
    }
  }


  private static class Entry {
    public float distance;
    public String string;
    public String baseForm;
    public Entry (float d, String s, String b) {distance = d; string = s; baseForm =  b;}
    public Entry() {distance = 0; string = ""; baseForm = "";}
    public String toString() {return String.format ("%f %s %s", distance, string, baseForm);}
  }


  private void bestMatch (Entry entry, String baseForm, String word, Set<String> set)
  {
    for (String u : set) {
      final float d = stringDistance.getDistance (word, u);
//      System.out.println ("\n" + word + " " + (new Entry (d, u, baseForm)) + " maxDistance");
      if (d > entry.distance) {
        entry.distance = d;
        entry.string = u;
        entry.baseForm = baseForm;
      }
    }
//    System.out.println (word + " " + entry + " maxDistance xxx");
  }


  private StringDistance stringDistance;
  private KeyMap keyMap;
  private double threshold;
}

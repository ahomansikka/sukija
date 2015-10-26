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

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


// Vertaa
// http://stackoverflow.com/questions/426878/is-there-any-way-to-do-n-level-nested-loops-in-java


public class CharCombinator implements Iterator<String> {
  private Map<Character,String> map;
  private String[] s;
  private boolean hasNextCombination = true;
  private int[] indices;
  private int[] ranges;
  private StringBuilder sb = new StringBuilder (100);
  private static final int MAX_SIZE = 10000;
  private final int size;
  private boolean firstCall = true;


  public CharCombinator (String word, String from, String to)
  {
    setMap (from, to);

    s = new String[word.length()];

    for (int i = 0; i < word.length(); i++) {
      s[i] = makeString (word.charAt(i));
//      System.out.println ("s= " + i + " " + s[i]);
    }

    ranges = makeRanges (s);

    indices = new int[ranges.length];
    this.size = size();
    hasNextCombination = this.size < MAX_SIZE;
    sb.append (word);
  }


  public boolean hasNext()
  {
    if (firstCall)
      return true;
    else
      return hasNextCombination;
  }


  public String next()
  {
    if (firstCall) {
      firstCall = false;
//System.out.println ("next " + sb.toString());
      return sb.toString();
    }
    else {
      advanceIndices();
      sb.delete (0, sb.length());
      for (int i = 0; i < indices.length; i++) {
        sb.append (s[i].charAt(indices[i]));
      }
      final String u = sb.toString();
//System.out.println ("Böö " + u);
      hasNextCombination = !allMaxed();
      return u;
    }
  }


  public void remove()
  {
    throw new UnsupportedOperationException ("CharCombinator");
  }


  public Iterator<String> iterator() {return this;}


  public void reset()
  {
    for (int i = 0; i < indices.length; i++) indices[i] = 0;
  }


  private int size()
  {
    int p = 1;
    for (int i = 0; i < ranges.length; i++) {
      p = p * ranges[i];
      if (p >= MAX_SIZE) return MAX_SIZE; // Turha jatkaa laskemista.
      if (p < 0) return MAX_SIZE; // Ylivuoto!
    }
    return p;
  }


  public static final String FROM = "bdgkptvwoöaaååää";
  public static final String TO   = "ptkgbdwvöoåäaäaå";


  private void setMap (String from, String to)
  {
    if (from.length() != to.length()) {
      throw new IllegalArgumentException ("from.length() != to.length()");
    }

    map = new TreeMap<Character,String>();

    for (int i = 0; i < from.length(); i++) {
      if (map.containsKey (from.charAt(i))) {
        final String s = map.get (from.charAt(i));
        if (s.indexOf (to.charAt(i)) >= 0) {
          throw new IllegalArgumentException ("Merkki " + to.charAt(i) + " on jo.");
        }
        map.put (from.charAt(i), String.format ("%s%c", s, to.charAt(i)));
      }
      else {
        map.put (from.charAt(i), to.substring(i,i+1));
      }
    }
  }


  private String makeString (Character ch)
  {
    if (map.containsKey (ch)) {
      return String.format ("%c%s", ch, map.get(ch));
    }
    else {
      return String.format ("%c", ch);
    }
  }


  private int[] makeRanges (String[] s)
  {
    int[] u = new int[s.length];
    for (int i = 0; i < s.length; i++) {
      u[i] = s[i].length();
    }
    return u;
  }


  // Advances 'indices' to the next in sequence.
  //
  private void advanceIndices()
  {
    for (int i = indices.length-1; i >= 0; i--) {
//System.out.println ("size " + size + " " + i + " " + ranges.length + " " + indices[i] + " " + ranges[i] + " " + sb.charAt(i));
      if (indices[i]+1 == ranges[i]) {
        indices[i] = 0;
      }
      else {
        indices[i]++;
        break;
      }
    }
  }


  // Tests if indices are in final position.
  //
  private boolean allMaxed()
  {
    for (int i = indices.length-1; i >= 0; i--) {
      if (indices[i] != ranges[i]-1) {
        return false;
      }
    }
    return true;
  }
}

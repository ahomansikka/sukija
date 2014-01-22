/*
Copyright (©) 2013-2014 Hannu Väisänen

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

package peltomaa.sukija.suggestion;

import peltomaa.sukija.morphology.Morphology;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Try to recognise a word without a prefix. If succesful, return
 * prefix + recognised word.
 *
 * For example, if prefix is "aasian" and word is "aasianleijonaa",
 * try to recognise "leijonaa". If succesful, return "aasianleijona".
 */
public class PrefixSuggestion extends Suggestion {
  public PrefixSuggestion (Morphology morphology, String prefix)
  {
    super (morphology);
//    pattern1 = Pattern.compile (prefix);
    prefixSet = new HashSet<String>();
    prefixSet.add (prefix);
    minLength = prefix.length();
    maxLength = minLength;
  }


  public PrefixSuggestion (Morphology morphology, List<String> prefix)
  {
    super (morphology);
//    makePatterns (prefix);
    prefixSet = new HashSet<String> (prefix);
    minLength = 1000000;
    maxLength = 0;
    for (String s: prefixSet) {
      if (minLength > s.length()) minLength = s.length();
      if (maxLength < s.length()) maxLength = s.length();
    }
  }


  public boolean suggest (String word)
  {
    reset();

    for (int i = Math.min (maxLength, word.length()); i >= minLength; i--) {
      final String p = word.substring (0, i);
      if (prefixSet.contains (p)) {
        set.clear();
        if (analyse (word.substring(i), set)) {
          result.clear();
          for (String s: set) {
            result.add (p + s);
          }
          return true;
        }
      }
    }
/*
    Matcher m = pattern1.matcher (word);
    if (m.lookingAt()) {
      if (m.end() == word.length()) return suggest (word, pattern2);
      set.clear();
      String p = word.substring (0, m.end());
//System.out.println (" " + p);
      if (analyse (word.substring (m.end()), set)) {
        result.clear();
        for (String s: set) {
          result.add (p + s);
        }
        return true;
      }
    }
    else if (pattern2 != null) {
      return suggest (word, pattern2);
    }
*/
/*
    for (String p : prefix) {
      if (word.startsWith (p)) {
        set.clear();
        if (analyse (word.substring (p.length()), set)) {
          result.clear();
          for (String s: set) {
            result.add (p + s);
          }
          return true;
        }
      }
    }
*/
    return false;
  }


  private boolean suggest (String word, Pattern pattern)
  {
    Matcher m = pattern.matcher (word);
    if (m.lookingAt()) {
      set.clear();
      String p = word.substring (0, m.end());
      if (analyse (word.substring (m.end()), set)) {
        result.clear();
        for (String s: set) {
          result.add (p + s);
        }
        return true;
      }
    }
    return false;
  }


  private List<String>[] makeListArray (List<String> s)
  {
    Collections.sort (s);
    ArrayList[] u = new ArrayList[2];
    u[0] = new ArrayList();
    u[1] = new ArrayList();

    for (int i = 0; i < s.size(); i++) {
//System.out.println (i + " " + s.get(i));
      if (i < s.size()-1 && s.get(i+1).startsWith (s.get(i))) {
        u[0].add (s.get(i+1));
        u[1].add (s.get(i));
//System.out.println (i + " " + s.get(i+1) + " " + s.get(i));
        i++;
      }
      else {
//System.out.println (i + " " + s.get(i) + " xxxx");
        u[0].add (s.get(i));
      }
    }
    return u;
  }


  private String makePatternString (List<String> s)
  {
    StringBuilder sb = new StringBuilder (10*s.size());
    sb.append ('(');
    for (int i = 0; i < s.size(); i++) {
      sb.append (s.get(i));
      if (i < s.size()-1) {
        sb.append ('|');
      }
    }
    sb.append (')');
    return sb.toString();
  }


  private void makePatterns (List<String> v)
  {
    List<String>[] list = makeListArray (v);

    pattern1 = Pattern.compile (makePatternString (list[0]));

    if (list[1].size() > 0) {
      pattern2 = Pattern.compile (makePatternString (list[1]));
    }
  }


  private Set<String> set = new TreeSet<String>();
  private Pattern pattern1;
  private Pattern pattern2;

  private Set<String> prefixSet;
  private int minLength;
  private int maxLength;
}

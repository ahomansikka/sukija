/*
Copyright (©) 2009-2011 Hannu Väisänen

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

import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;


public class RegexCombinator {
  public RegexCombinator (Vector<Pair<String,String>> data)
  {
    this.pattern = makePattern (data);
    this.data = data;
//System.out.println (pattern.pattern());
  }


  public RegexCombinator (String[] array)
  {
    this (Pair.makePair (array));
  }


  private Vector<Pair<String,String>> data;


  private static Pattern makePattern (Vector<Pair<String,String>> v)
  {
    StringBuffer sb = new StringBuffer();

    for (int i = 0; i < v.size(); i++) {
      sb.append ("(?:");
      sb.append (RegexUtil.makePatternString (v.get(i).first));
      sb.append (")");
      if (i < v.size()-1) sb.append ('|');
    }
    return Pattern.compile (sb.toString());
//  return Pattern.compile (sb.toString(), Pattern.CANON_EQ);  // Does not work. )-:
  }

  private Pattern pattern;

  private static void getCombination (Vector<Vector<String>> v,
                                      int begin,
                                      int end,
                                      Vector<String> work,
                                      Set<String> result)
  {
    if (begin < end) {
      for (int i = 0; i < v.get(begin).size(); i++) {
        work.add (v.get(begin).get(i));
        getCombination (v, begin+1, end, work, result);
        work.remove (work.size()-1);
      }
    }
    else if (begin == end) {
      StringBuffer s = new StringBuffer();
      for (int i = 0; i < work.size(); i++) {
        s.append (work.get(i));
      }
      result.add (s.toString());
    }
  }


  public Set<String> makeCombination (CharSequence s)
  {
    Vector<String> v = new Vector<String>();
    Vector<Integer> index = new Vector<Integer>();
    int start = 0;
    boolean found = false;

    Matcher m = pattern.matcher (s);

// System.out.println ("makeCombination1 " + s.toString() + " " + pattern.pattern());

    // Split char sequence s into vector of strings.
    //
    while (m.find()) {
      found = true;
      MatchResult r = m.toMatchResult();
// System.out.println ("makeCombination2 " + s.toString() + " " + r.groupCount());

      // Group zero is the entire match and we don't want that, so we start at 1.
      //
      for (int i = 1; i <= r.groupCount(); i++) {
        if (r.start(i) >= 0) {
// System.out.println ("makeCombination3 " + i + " " + s.subSequence (start, r.start(i)).toString());
          v.add (s.subSequence (start, r.start(i)).toString()); // Add chars between capturing groups.
          index.add (-1);                // We are not interested where they are.
          v.add (r.group(i));            // Add matched capturing group.
          index.add (i-1);               // And save their position.
          start = r.end(i);
        }
      }
    }

// System.out.println ("makeCombinationä");

    if (!found) return null;

// System.out.println ("makeCombinationö");

    if (start < s.length()) {
      v.add (s.subSequence (start, s.length()).toString());  // Add end of the char sequence.
      index.add (-1);
    }

    Vector<Vector<String>> vec = new Vector<Vector<String>> (v.size());

    // Put the original char sequence into vector vec.
    //
    for (int i = 0; i < v.size(); i++) {
      vec.add (new Vector<String>());
      vec.get(i).add (v.get (i));
    }


    // Put replacement strings into vector vec in correct positions.
    //
    for (int i = 0; i < v.size(); i++) {
      if (index.get(i) >= 0) {
        vec.get(i).add (data.get(index.get(i)).second);
      }
    }

    Vector<String> work = new Vector<String>();
    Set<String> result = new TreeSet<String>();

    getCombination (vec, 0, v.size(), work, result);
/*
System.out.println ("A");
for (int i = 0; i < v.size(); i++) {
  System.out.print (i);
  for (int j = 0; j < vec.get(i).size(); j++) {
    System.out.print (" " + vec.get(i).get(j));
  }
  System.out.println ("");
}
System.out.println ("B");
Iterator<String> i = result.iterator();
while (i.hasNext()) {
  System.out.println (i.next());
}
System.out.println ("C");
*/
    return result;
  }


/*
For example, if s is "aammbbbmmccnnd" and regexp is "(mm)|(nn)" we split s into
vector of 7 elements {"aa", "mm", "bbb", "mm", "cc", "nn", "d"}.
Then we replace the capturing groups "mm" and "nn" with their replacements strings,
e.g. "X" and "Y", and take all the combinations with and without the replacements.
In this case the result is

aaXbbbXccYd
aaXbbbXccnnd
aaXbbbmmccYd
aaXbbbmmccnnd
aammbbbXccYd
aammbbbXccnnd
aammbbbmmccYd
aammbbbmmccnnd
*/
/*
  public static void main (String[] args)
  {
    try {
      Vector<SuggestionData> v = new Vector<SuggestionData>();
      v.add (new SuggestionData ("(mm)", "X"));  // Replacement for "mm" is "X".
      v.add (new SuggestionData ("(nn)", "Y"));  // Replacement for "nn" is "Y".

      RegexCombinator rc = new RegexCombinator (v, RegexUtil.FI);
      Set<String> result = rc.makeCombination ("aammbbbmmccnnd");

      Iterator<String> i = result.iterator();
      while (i.hasNext()) {
       System.out.println (i.next());
      }

      test2();
    }
    catch (Throwable t)
    {
      System.out.println (t.getMessage());
    }
  }


  private static void test2()
  {
    String[] array = {"(mm)", "X", "(nn)", "Y"};
    RegexCombinator rc = new RegexCombinator (array, RegexUtil.FI);
    Set<String> result = rc.makeCombination ("aammbbbmmccnnd");

    Iterator<String> i = result.iterator();
    while (i.hasNext()) {
      System.out.println (i.next());
    }
  }
*/
}

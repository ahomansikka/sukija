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

package peltomaa.sukija.suggestion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.Vector;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.util.RegexUtil;


public class SuggestionParser {
  public SuggestionParser (Morphology morphology, Reader reader) throws IOException
  {
    BufferedReader file = new BufferedReader (reader);
    String line;
    while ((line = file.readLine()) != null) {
      if (!comment_or_whitespace.matcher(line).matches()) {
        parse (line, morphology);
      }
    }
  }

  Vector<Suggestion> getSuggestions() {return v;}


  private void parse (String line, Morphology morphology)
  {
    Matcher m = command.matcher (line);
    if (m.matches()) {
      parse (m.group(1), argument(m.group(2)), argument(m.group(3)), morphology);
    }
    else if (length3.matcher(line).matches()) {
//      System.out.println ("Length3");
      v.add (new Length3Suggestion (morphology));
    }
    else if (apostrophe.matcher(line).matches()) {
 //     System.out.println ("Apostrophe");
      v.add (new ApostropheSuggestion (morphology));
    }
    else {
      throw new RuntimeException ("SuggestionParser: Incorrect line: " + line);
    }
  }


  private final void parse (String name, String argument1, String argument2, Morphology morphology)
  {
//System.out.println (name + " " + argument1 + " " + argument2);

    if (name.compareTo ("Char") == 0) {
      v.add (new CharSuggestion (morphology, argument1, argument2));
    }
    else if (name.compareTo ("CharCombination") == 0) {
      v.add (new CharCombinationSuggestion (morphology, argument1, argument2));
    }
    else if (name.compareTo ("Regex") == 0) {
      v.add (new RegexSuggestion (morphology, argument1, argument2));
    }
    else if (name.compareTo ("RegexCombination") == 0) {
//      v.add (new RegexCombinationSuggestion (morphology, argument1, argument2));
    }
  }


  private String argument (String s)
  {
    if (!s.startsWith("\"")) throw new RuntimeException ("String '" + s + "' does not start with '\"'.");
    if (!s.endsWith("\""))   throw new RuntimeException ("String '" + s + "' does not end with '\"'.");
    return s.substring (1, s.length()-1);
  }


  private static final String KEYWORD = "(Char|CharCombination|Regex|RegexCombination)";
  private static final String IGNORE = "\\s*(#.*$)*";

  private static final Pattern comment_or_whitespace = Pattern.compile (IGNORE);
  private static final Pattern command = Pattern.compile ("\\s*" + KEYWORD + "\\s+(\\S+)\\s+(\\S+)" + IGNORE);
  private static final Pattern length3 = Pattern.compile ("\\s*Length3" + IGNORE);
  private static final Pattern apostrophe = Pattern.compile ("\\s*Apostrophe" + IGNORE);

  private Vector<Suggestion> v = new Vector<Suggestion>();
}

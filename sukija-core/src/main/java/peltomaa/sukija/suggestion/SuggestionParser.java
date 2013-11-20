/*
Copyright (©) 2012-2013 Hannu Väisänen

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
import java.io.FileReader;
import java.io.Reader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.Scanner;
import java.util.Vector;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.util.RegexUtil;


public class SuggestionParser {
  public SuggestionParser (Morphology morphology, Reader reader) throws IOException
  {
    BufferedReader file = new BufferedReader (reader);
    String line;
    while ((line = file.readLine()) != null) {
      if (!COMMENT_OR_WHITESPACE.matcher(line).matches()) {
        parseLine (line, morphology);
      }
    }
  }


  public Vector<Suggestion> getSuggestions() {return v;}


  private void parseLine (String line, Morphology morphology)
  {
    Scanner scanner = new Scanner (line);

    if (scanner.hasNext (KEYWORD)) {
      parseCommand (scanner, morphology);
    }
  }


  private void parseCommand (Scanner scanner, Morphology morphology)
  {
    String command = scanner.next();

    switch (command) {
      case "Apostrophe":
        v.add (new ApostropheSuggestion (morphology));
        break;
      case "Char":
        v.add (new CharSuggestion (morphology, argument(scanner.next()), argument(scanner.next())));
        break;
      case "CharCombination":
        v.add (new CharCombinationSuggestion (morphology, argument(scanner.next()), argument(scanner.next())));
        break;
      case "Length3":
        v.add (new Length3Suggestion (morphology));
        break;
      case "Prefix":
        v.add (new PrefixSuggestion (morphology, parseList (scanner)));
        break;
      case "Regex":
        v.add (new RegexSuggestion (morphology, argument(scanner.next()), argument(scanner.next())));
        break;
      case "RegexCombination":
//        v.add (new RegexCombinationSuggestion (morphology, argument(scanner.next()), argument(scanner.next())));
        break;
    }
    scanner.skip (COMMENT_OR_WHITESPACE);
  }


  private Vector<String> parseList (Scanner scanner)
  {
    Vector<String> argument = new Vector<String>();
    while (scanner.hasNext()) {
      final String s = scanner.next();
      if (s.startsWith ("#")) {
        break;
      }
      argument.add (s);
    }
    return argument;
  }


  private String argument (String s)
  {
    if (!s.startsWith("\"")) throw new RuntimeException ("String '" + s + "' does not start with '\"'.");
    if (!s.endsWith("\""))   throw new RuntimeException ("String '" + s + "' does not end with '\"'.");
    return s.substring (1, s.length()-1);
  }


  private static final String IGNORE = "\\s*(#.*$)*";
  private static final Pattern COMMENT_OR_WHITESPACE = Pattern.compile (IGNORE);
  private static final Pattern KEYWORD = Pattern.compile ("Apostrophe|Char|CharCombination|Length3|Prefix|Regex|RegexCombination");

  private Vector<Suggestion> v = new Vector<Suggestion>();
}

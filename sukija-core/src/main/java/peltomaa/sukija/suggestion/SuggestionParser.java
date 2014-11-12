/*
Copyright (©) 2012-2014 Hannu Väisänen

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

import java.util.Arrays;
import java.util.List;
import java.io.OutputStream;
import java.util.regex.Pattern;
import java.util.Vector;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.util.JAXBUtil;
import peltomaa.sukija.schema.*;


public class SuggestionParser {
  public SuggestionParser (Morphology morphology, String xmlFile) throws SuggestionParserException
  {
    this (morphology, xmlFile, XSD_FILE);
  }


  public SuggestionParser (Morphology morphology, String xmlFile, String xsdFile) throws SuggestionParserException
  {
    try {
      si = JAXBUtil.unmarshal (xmlFile, xsdFile, CONTEXT_PATH);
      parseSuggestions (morphology, si.getSuggestion());
    }
    catch (SuggestionParserException e) {
      throw e;
    }
    catch (Throwable t)
    {
      throw new SuggestionParserException (t);
    }
  }


  public SuggestionParser (Morphology morphology, java.io.Reader reader)
  {
  }


  /** Tulostetaan asetukset.
   */
  public void print (OutputStream out) throws JAXBException
  {
    JAXBUtil.marshal ((new ObjectFactory()).createSuggestions(si), CONTEXT_PATH, out);
  }


  public Vector<Suggestion> getSuggestions() {return v;}


  private void parseSuggestions (Morphology morphology, List<SuggestionInput.Suggestion> s) throws SuggestionParserException
  {
    for (int i = 0; i < s.size(); i++) {
      final List<String> argument = s.get(i).getArgument();
      final String name = s.get(i).getName();
      switch (name) {
        case "Apostrophe":
          checkArguments (name, argument.size() == 0);
          v.add (new ApostropheSuggestion (morphology));
          break;
        case "Char":
          checkArguments (name, argument.size() == 2);
          v.add (new CharSuggestion (morphology, argument.get(0), argument.get(1)));
          break;
        case "CharCombination":
          checkArguments (name, argument.size() == 2);
          v.add (new CharCombinationSuggestion (morphology, argument.get(0), argument.get(1)));
          break;
        case "Hyphen":
          checkArguments (name, argument.size() == 1);
          v.add (new HyphenSuggestion (morphology, toBoolean(argument.get(0))));
          break;
        case "Length3":
          checkArguments (name, argument.size() == 0);
          v.add (new Length3Suggestion (morphology));
          break;
        case "Prefix":
          checkArguments (name, argument.size() >= 1);
          v.add (new PrefixSuggestion (morphology, toList(argument)));
          break;
        case "Regex":
          checkArguments (name, argument.size() >= 2);
          v.add (new RegexSuggestion (morphology, parse1(name,argument), toBoolean(argument.get(argument.size()-1))));
          break;
        case "RegexCombination":
//          v.add (new RegexCombinationSuggestion (morphology, argument(scanner.next()), argument(scanner.next())));
          break;
        case "String":
          checkArguments (name, argument.size() >= 1);
          v.add (new StringSuggestion (morphology, parse2 (name, argument)));
          break;
      }
    }
  }


  private void checkArguments (String name, boolean expr) throws SuggestionParserException
  {
    if (!expr) throw new SuggestionParserException (name);
  }


  private List<String> toList (List<String> s)
  {
    List<String> u = new Vector<String>();
    for (int i = 0; i < s.size(); i++) {
      u.addAll (Arrays.asList (WHITESPACE.split(s.get(i))));
    }
    return u;
  }


  private List<String> parse1 (String name, List<String> argument) throws SuggestionParserException
  {
    List<String> u = new Vector<String>();
    for (int i = 0; i < argument.size()-1; i++) {
      String[] p = WHITESPACE.split (argument.get(i));
      if (p.length == 1) {
        u.add (p[0]);
        u.add ("");
      }
      else if (p.length == 2) {
        u.addAll (Arrays.asList (p));
      }
      else {
        throw new SuggestionParserException (name + " " + p.length + ": virheellinen parametrilista: " + argument.toString());
      }
    }
    return u;
  }


  private String[][] parse2 (String name, List<String> argument) throws SuggestionParserException
  {
    String[][] s = new String[argument.size()][];
    for (int i = 0; i < argument.size(); i++) {
      String[] p = WHITESPACE.split (argument.get(i));
      if (p.length >= 2) {
        s[i] = p;
      }
      else {
        throw new SuggestionParserException (name + ": virheellinen parametrilista: " + argument.toString());
      }
    }
    return s;
  }


  private boolean toBoolean (String s)
  {
    return Boolean.valueOf (s);
  }


  public class SuggestionParserException extends Exception {
    private SuggestionParserException (String message)
    {
      super (message);
    }
    private SuggestionParserException (String message, Throwable cause)
    {
      super (message, cause);
    }
    private SuggestionParserException (Throwable cause)
    {
      super (cause);
    }
  }


  private Vector<Suggestion> v = new Vector<Suggestion>();
  private static final String CONTEXT_PATH = "peltomaa.sukija.schema";
  private static final String XSD_FILE = "/peltomaa/sukija/schema/SuggestionInput.xsd";
  private static final Pattern WHITESPACE = Pattern.compile ("\\s+");
  private SuggestionInput si;
}

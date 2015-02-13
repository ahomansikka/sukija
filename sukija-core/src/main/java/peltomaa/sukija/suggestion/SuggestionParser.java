/*
Copyright (©) 2012-2015 Hannu Väisänen

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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.xml.bind.JAXBElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.util.JAXBUtil;
import peltomaa.sukija.schema.*;


public class SuggestionParser {
  public SuggestionParser (Morphology morphology, InputStream is) throws SuggestionParserException
  {
    try {
      LOG.info ("Aloitetaan.");
      si = JAXBUtil.unmarshal (is, XSD_FILE, CONTEXT_PATH, this.getClass().getClassLoader());
      parseSuggestions (morphology, si.getSuggestion());

//      print (System.out);
    }
    catch (Throwable t)
    {
      LOG.info (t.getMessage());
      throw new SuggestionParserException (t);
    }
  }


  public SuggestionParser (Morphology morphology, String xmlFile) throws SuggestionParserException
  {
    this (morphology, xmlFile, XSD_FILE);
  }


  public SuggestionParser (Morphology morphology, String xmlFile, String xsdFile) throws SuggestionParserException
  {
    try {
      LOG.info ("SuggestionParser: " + xmlFile + " " + xsdFile);

      si = JAXBUtil.unmarshal (xmlFile, xsdFile, CONTEXT_PATH, this.getClass().getClassLoader());
      parseSuggestions (morphology, si.getSuggestion());

//      print (System.out);
    }
    catch (Throwable t)
    {
      LOG.info (t.getMessage());
      throw new SuggestionParserException (t);
    }
  }


  /** Tulostetaan asetukset.
   */
  public void print (OutputStream out) throws SuggestionParserException
  {
    try {
      JAXBUtil.marshal ((new ObjectFactory()).createSuggestions(si), CONTEXT_PATH, out, this.getClass().getClassLoader());
    }
    catch (Throwable t)
    {
      LOG.info (t.getMessage());
      throw new SuggestionParserException (t);
    }
  }


  /** Tulostetaan asetukset.
   */
  public void print (Writer out) throws SuggestionParserException
  {
    try {
      JAXBUtil.marshal ((new ObjectFactory()).createSuggestions(si), CONTEXT_PATH, out, this.getClass().getClassLoader());
    }
    catch (Throwable t)
    {
      LOG.info (t.getMessage());
      throw new SuggestionParserException (t);
    }
  }


  public Vector<Suggestion> getSuggestions() {return v;}


  private void parseSuggestions (Morphology morphology, List<Object> s) // throws SuggestionParserException
  {
    for (int i = 0; i < s.size(); i++) {
//      System.out.println (i + " " + s.get(i).getClass().getName());

      // Tehdään Suggestion-luokat tässä, jotta Input-luokkien sisäistä rakennetta
      // ei tarvitse viedä Suggestion-luokkien sisälle. Sen lisäksi, jos Input-luokkia
      // muutetaan, muutokset Java-koodiin tarvitsee tehdä vain yhteen paikkaan, tähän.
      // Input-luokat tehdään automaagisesti xsd-tiedostoista komennolla xjc.
      //
      switch (s.get(i).getClass().getName()) {
        case "peltomaa.sukija.schema.CharInput":
          {
            final CharInput input = (CharInput)s.get(i);
            v.add (new CharSuggestion (morphology, input.getFrom(), input.getTo()));
          }
          break;
        case "peltomaa.sukija.schema.CompoundWordEndInput":
          {
            final CompoundWordEndInput input = (CompoundWordEndInput)s.get(i);
            v.add (new CompoundWordEndSuggestion (morphology, fromList2(input.getInput())));
          }
          break;
        case "peltomaa.sukija.schema.EraseInput":
          {
            final EraseInput input = (EraseInput)s.get(i);
            v.add (new EraseSuggestion (morphology, input.getFrom().charAt(0), input.getTo().charAt(0)));
          }
          break;
        case "peltomaa.sukija.schema.PrefixInput":
          {
            final PrefixInput input = (PrefixInput)s.get(i);
            v.add (new PrefixSuggestion (morphology, input.getPrefix(), input.isSavePrefix(), input.isSaveWord()));
          }
          break;
        case "peltomaa.sukija.schema.RegexInput":
          {
            final RegexInput input = (RegexInput)s.get(i);
            v.add (new RegexSuggestion (morphology, fromListA(input.getInput()), input.isTryAll()));
          }
          break;
        case "peltomaa.sukija.schema.StartInput":
          {
            final StartInput input = (StartInput)s.get(i);
            v.add (new StartSuggestion (morphology, input.getMinLength().intValue(), input.getMaxLength().intValue(), input.isBaseFormOnly()));
          }
          break;
        case "peltomaa.sukija.schema.StringInput":
          {
            final StringInput input = (StringInput)s.get(i);
            v.add (new StringSuggestion (morphology, toArray2(input.getInput())));
          }
          break;
      }
    }
//System.exit(1);
  }


  private static final List<String> fromListA (List<JAXBElement<List<String>>> input)
  {
    List<String> u = new Vector<String>();
    for (int i = 0; i < input.size(); i++) {
      switch (input.get(i).getValue().size()) {
        case 1: 
          u.add (input.get(i).getValue().get(0));
          u.add ("");
          break;
        case 2:
          u.addAll (input.get(i).getValue());
          break;
        default:
          // Tätä ei pitäisi koskaan tapahtua.
          LOG.error ("fromListA: listan pituus on väärä: " + input.get(i).getValue().size());
          throw new RuntimeException ("fromListA: listan pituus on väärä: " + input.get(i).getValue().size());
      }
    }
    return u;
  }


  private static final List<String> fromList2 (List<JAXBElement<List<String>>> input)
  {
    List<String> u = new Vector<String>();
    for (int i = 0; i < input.size(); i++) {
      u.addAll (input.get(i).getValue());
    }
    return u;
  }


  private static final String[][] toArray2 (List<JAXBElement<List<String>>> input)
  {
    String[][] s = new String[input.size()][];
    for (int i = 0; i < input.size(); i++) {
      s[i] = input.get(i).getValue().toArray (new String[0]);
    }
    return s;
  }


  public static class SuggestionParserException extends Exception {
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


  private static final Logger LOG = LoggerFactory.getLogger (SuggestionParser.class);
  private Vector<Suggestion> v = new Vector<Suggestion>();
  private static final String CONTEXT_PATH = "peltomaa.sukija.schema";
  private static final String XSD_FILE = "/peltomaa/sukija/schema/SuggestionInput.xsd";
  private SuggestionInput si;
}

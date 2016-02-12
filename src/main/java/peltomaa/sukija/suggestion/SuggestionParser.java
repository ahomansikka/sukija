/*
Copyright (©) 2012-2016 Hannu Väisänen

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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Vector;
import javax.xml.bind.JAXBElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peltomaa.sukija.util.JAXBUtil;
import peltomaa.sukija.util.RegexUtil;
import peltomaa.sukija.schema.*;
import org.puimula.libvoikko.Voikko;


public class SuggestionParser {
  public SuggestionParser (Voikko voikko, InputStream is) throws SuggestionParserException
  {
    if (parsed) return;

    try {
      LOG.info ("Aloitetaan.");
      si = JAXBUtil.unmarshal (is, XSD_FILE, SCHEMA_LOCATION, CONTEXT_PATH, this.getClass().getClassLoader());
      parseSuggestions (voikko, si.getSuggestion());
      parsed = true;

      print (System.out);
    }
    catch (Throwable t)
    {
      LOG.error ("a " + t.getCause().getClass().getName() + " " + t.getCause().getMessage());
      if (t.getCause() != null) LOG.error ("b " + t.getCause().getClass().getName() + " " + t.getCause().getMessage());
      throw new SuggestionParserException (t);
    }
  }


  public SuggestionParser (Voikko voikko, String xmlFile) throws SuggestionParserException
  {
    this (voikko, xmlFile, XSD_FILE);
  }


  public SuggestionParser (Voikko voikko, String xmlFile, String xsdFile) throws SuggestionParserException
  {
    if (parsed) return;

    try {
      LOG.info ("SuggestionParser: " + xmlFile + " " + xsdFile);

      si = JAXBUtil.unmarshal (xmlFile, xsdFile, SCHEMA_LOCATION, CONTEXT_PATH, this.getClass().getClassLoader());
//      JAXBUtil.marshal ((new ObjectFactory()).createSuggestions(si), CONTEXT_PATH, System.out, this.getClass().getClassLoader()); System.exit(1);
      parseSuggestions (voikko, si.getSuggestion());
      parsed = true;

//      print (System.out);
    }
    catch (Throwable t)
    {
      if (t.getMessage() != null) LOG.error ("c " + t.getMessage());
      if (t.getCause() != null) LOG.error ("d " + t.getCause().getMessage());
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
      LOG.error ("e " + t.getMessage());
      if (t.getCause() != null) LOG.error ("f " + t.getCause().getMessage());
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
      LOG.error ("g " + t.getMessage());
      if (t.getCause() != null) LOG.error ("h " + t.getCause().getMessage());
      throw new SuggestionParserException (t);
    }
  }


  public Suggestion[] getSuggestions() {return v;}


  private void parseSuggestions (Voikko voikko, List<Object> s) throws FileNotFoundException, IOException
  {
    v = new Suggestion[s.size()];

    for (int i = 0; i < s.size(); i++) {
//      System.out.println ("SuggestionParser: " + i + " " + s.get(i).getClass().getName());

      // Tehdään Suggestion-luokat tässä, jotta Input-luokkien sisäistä rakennetta
      // ei tarvitse viedä Suggestion-luokkien sisälle. Sen lisäksi, jos Input-luokkia
      // muutetaan, muutokset Java-koodiin tarvitsee tehdä vain yhteen paikkaan, tähän.
      // Input-luokat tehdään automaagisesti xsd-tiedostoista komennolla xjc.
      //
      switch (s.get(i).getClass().getName()) {
        case "peltomaa.sukija.schema.ApostropheInput":
          {
            v[i] = new ApostropheSuggestion (voikko);
          }
          break;
        case "peltomaa.sukija.schema.CharInput":
          {
            final CharInput input = (CharInput)s.get(i);
            v[i] = new CharSuggestion (voikko, input.getFrom(), input.getTo());
          }
          break;
        case "peltomaa.sukija.schema.CompoundWordEndInput":
          {
            final CompoundWordEndInput input = (CompoundWordEndInput)s.get(i);
            v[i] = new CompoundWordEndSuggestion (voikko, makePattern (input.getInput()),
                                                  makeReplacement (input.getInput()),
                                                  input.isAddStart(), input.isAddBaseFormOnly(),
                                                  input.isAddEnd());
          }
          break;
/*
        case "peltomaa.sukija.schema.EraseInput":
          {
            final EraseInput input = (EraseInput)s.get(i);
            v[i] = new EraseSuggestion (voikko, input.getFrom().charAt(0), input.getTo().charAt(0));
          }
          break;
*/
        case "peltomaa.sukija.schema.PrefixInput":
          {
            final PrefixInput input = (PrefixInput)s.get(i);
            v[i] = new PrefixSuggestion (voikko, input.getPrefix(), input.isSavePrefix(), input.isSaveWord());
          }
          break;
        case "peltomaa.sukija.schema.RegexInput":
          {
            final RegexInput input = (RegexInput)s.get(i);
            v[i] = new RegexSuggestion (voikko, makePattern (input.getInput()),
                                        makeReplacement (input.getInput()), input.isTryAll());
          }
          break;
        case "peltomaa.sukija.schema.StartInput":
          {
            final StartInput input = (StartInput)s.get(i);
            v[i] = new StartSuggestion (voikko, input.getMinLength().intValue(),
                                        input.getMaxLength().intValue(), input.isBaseFormOnly(),
                                        input.isTryAll());
          }
          break;
        case "peltomaa.sukija.schema.StringDistanceInput":
          {
            final StringDistanceInput input = (StringDistanceInput)s.get(i);
            v[i] = new StringDistanceSuggestion (voikko, input.getFileName(),
                                                 input.getDistanceClass().value(),
                                                 input.getParameter(), input.getThreshold());
          }
          break;
        case "peltomaa.sukija.schema.VoikkoAttributeInput":
          {
            final VoikkoAttributeInput input = (VoikkoAttributeInput)s.get(i);
            v[i] = new VoikkoAttributeSuggestion (voikko, makePattern (input.getInput()), makeReplacement (input.getInput()),
                                                  input.getAttribute(),
                                                  input.getRegex(), input.isTryAll());
          }
          break;
        default:
          throw new RuntimeException ("SuggestionParser: tuntematon luokka: s.get(i).getClass().getName()");
      }
    }
//System.exit(1);
  }


  private static final Pattern[] makePattern (List<JAXBElement<List<String>>> input)
  {
    Pattern[] p = new Pattern[input.size()];
    for (int i = 0; i < input.size(); i++) {
      p[i] = RegexUtil.makePattern (input.get(i).getValue().get(0));
    }
    return p;
  }


  private static final String[] makeReplacement (List<JAXBElement<List<String>>> input)
  {
    String[] s = new String[input.size()];
    for (int i = 0; i < input.size(); i++) {
      switch (input.get(i).getValue().size()) {
        case 1:
          s[i] = "";
          break;
        case 2:
          s[i] = input.get(i).getValue().get(1);
          break;
        default:
          // Tätä ei pitäisi koskaan tapahtua.
          throw new RuntimeException ("Listan pituus on väärä: " + input.get(i).getValue().size());
      }
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
  private Suggestion[] v;
  private static final String CONTEXT_PATH = "peltomaa.sukija.schema";
  private static final String XSD_FILE = "SuggestionInput.xsd";
//  private static final String XSD_FILE = "peltomaa/sukija/schema/SuggestionInput.xsd";
  private static final String SCHEMA_LOCATION = "peltomaa/sukija/schema";
  private SuggestionInput si;
  private boolean parsed = false;
}

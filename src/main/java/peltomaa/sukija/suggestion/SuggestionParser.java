/*
Copyright (©) 2012-2018, 2020-2023 Hannu Väisänen

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
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Set;
import jakarta.xml.bind.JAXBElement;
import org.ahocorasick.trie.*;
import org.ahocorasick.trie.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peltomaa.sukija.util.XjcIO;
import peltomaa.sukija.util.RegexUtil;
import peltomaa.sukija.schema.*;
import org.puimula.libvoikko.Voikko;


public class SuggestionParser {
  public SuggestionParser (Voikko voikko, InputStream is) throws SuggestionParserException
  {
    try {
      LOG.info ("SuggestionParser 1: Aloitetaan.");
      io = new XjcIO<SuggestionInput> ("/SuggestionInput.xsd", SuggestionInput.class, ObjectFactory.class);
      si = io.read (is);
      parseSuggestions (voikko, si.getApostropheOrIcharOrCompoundWordEnd());

//      print (System.out);
    }
    catch (Throwable t)
    {
//      LOG.error ("a " + t.getCause().getClass().getName() + " " + t.getCause().getMessage());
      if (t.getCause() != null) LOG.error ("b " + t.getCause().getClass().getName() + " " + t.getCause().getMessage());
      t.printStackTrace (System.out);
      throw new SuggestionParserException (t);
    }
  }


  public SuggestionParser (Voikko voikko, String xmlFile) throws SuggestionParserException
  {
    this (voikko, xmlFile, XSD_FILE);
  }


  public SuggestionParser (Voikko voikko, String xmlFile, String xsdFile) throws SuggestionParserException
  {
    try {
      LOG.info ("SuggestionParser 2: " + xmlFile + " " + xsdFile + " " + SCHEMA_LOCATION);

      io = new XjcIO<SuggestionInput> ("/SuggestionInput.xsd", SuggestionInput.class, ObjectFactory.class);
      LOG.info ("SuggestionParser 2b.");
      si = io.read (xmlFile);
      parseSuggestions (voikko, si.getApostropheOrIcharOrCompoundWordEnd());
//      print (System.out);
    }
    catch (Throwable t)
    {
      if (t.getMessage() != null) LOG.error ("c " + t.getMessage() + "\n");
      if (t.getCause() != null) LOG.error ("d " + t.getCause().getMessage() + "\n");
      t.printStackTrace (System.out);
      throw new SuggestionParserException (t);
    }
  }


  /** Tulostetaan asetukset.
   */
  public void print (OutputStream out) throws SuggestionParserException
  {
    try {
      io.write (si, out);
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
      io.write (si, out);
    }
    catch (Throwable t)
    {
      LOG.error ("g " + t.getMessage());
      if (t.getCause() != null) LOG.error ("h " + t.getCause().getMessage());
      throw new SuggestionParserException (t);
    }
  }


  public Suggestion[] getSuggestions() {return v;}

  public String getFrom() {return (si.getChar() == null) ? "" : si.getChar().getFrom();}
  public String getTo()   {return (si.getChar() == null) ? "" : si.getChar().getTo();}


  private void parseSuggestions (Voikko voikko, List<Object> s) throws FileNotFoundException, IOException
  {
    v = new Suggestion[s.size()];
//System.out.println ("parseSuggestions " + s.size());
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
            final ApostropheInput input = (ApostropheInput)s.get(i);
            v[i] = new ApostropheSuggestion (voikko, input.getCharList());
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

            v[i] = new CompoundWordEndSuggestion (voikko, makePayloadTrie (input.getInput(), false),
                                                  input.isAddStart(), input.isAddBaseFormOnly(),
                                                  input.isAddEnd());
          }
          break;
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
        case "peltomaa.sukija.schema.SplitInput":
          {
            final SplitInput input = (SplitInput)s.get(i);
            v[i] = new SplitSuggestion (voikko);
          }
          break;
        case "peltomaa.sukija.schema.StartInput":
          {
            final StartInput input = (StartInput)s.get(i);
            v[i] = new StartSuggestion (voikko, input.getMinLength().intValue(),
                                        input.getMaxLength().intValue(), input.isBaseFormOnly(),
                                        input.isHyphen(),
                                        input.isTryAll());
          }
          break;
        case "peltomaa.sukija.schema.StringDistanceInput":
          {
            final StringDistanceInput input = (StringDistanceInput)s.get(i);
            v[i] = new StringDistanceSuggestion (voikko,
                                                 input.getFileName(),
                                                 input.getDistanceClass().value(),
                                                 input.getParameter(),
                                                 input.getKeyLength().intValue(),
                                                 input.getThreshold());
          }
          break;
        case "peltomaa.sukija.schema.StringInput":
          {
            final StringInput input = (StringInput)s.get(i);
            v[i] = new StringSuggestion (voikko, makePayloadTrie (input.getInput(), true));
          }
          break;
        case "peltomaa.sukija.schema.VoikkoAttributeInput":
          {
            final VoikkoAttributeInput input = (VoikkoAttributeInput)s.get(i);
            v[i] = new VoikkoAttributeSuggestion (voikko,
                                                  new VoikkoAttributeParameter (input));
          }
          break;
       case "peltomaa.sukija.schema.VoikkoSpellingInput":
          {
            final VoikkoSpellingInput input = (VoikkoSpellingInput)s.get(i);
            v[i] = new VoikkoSpellingSuggestion (voikko, input.getN().intValue());
          }
          break;
        default:
          // Tätä ei pitäisi koskaan tapahtua. Jos tapahtuu,
          // lienen unohtanut break-lauseen jostakin.
          throw new RuntimeException ("SuggestionParser: tuntematon luokka: " + s.get(i).getClass().getName());
      }
    }
//System.exit(1);
  }


  private static final PayloadTrie<String> makePayloadTrie (List<JAXBElement<List<String>>> input, boolean ignoreOverlaps)
  {
    final Set<String> set = new HashSet<String>();
    final PayloadTrie.PayloadTrieBuilder<String> payloadTrieBuilder = PayloadTrie.<String>builder();
    if (ignoreOverlaps) {
      payloadTrieBuilder.ignoreOverlaps();
    }

    for (int i = 0; i < input.size(); i++) {
      if (input.get(i).getValue().size() == 2) {
        final String KEY = input.get(i).getValue().get(0);
        if (set.contains (KEY)) {
          throw new RuntimeException ("Kaksi samaa avainkentän arvoa " + KEY + ".");
        }
        else {
          payloadTrieBuilder.addKeyword (KEY, input.get(i).getValue().get(1));
        }
      }
      else {
        throw new RuntimeException ("Listassa pitää olla 2 alkiota, on "
                                    + input.get(i).getValue().size() + ".");
      }
    }
    return payloadTrieBuilder.build();
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
    private static final long serialVersionUID = 1L;
  }


  private static final Logger LOG = LoggerFactory.getLogger (SuggestionParser.class);
  private Suggestion[] v;
  private static final String CONTEXT_PATH = "peltomaa.sukija.schema";
  private static final String XSD_FILE = "SuggestionInput.xsd";
//  private static final String XSD_FILE = "peltomaa/sukija/schema/SuggestionInput.xsd";
  private static final String SCHEMA_LOCATION = "peltomaa/sukija/schema";
  private SuggestionInput si;
  private final XjcIO<SuggestionInput> io;
}

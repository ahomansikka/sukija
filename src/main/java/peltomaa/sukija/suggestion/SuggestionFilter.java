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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.Vector;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.puimula.libvoikko.*;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.util.SukijaFilter;
import peltomaa.sukija.voikko.VoikkoUtils;


public class SuggestionFilter extends SukijaFilter {

  /**
   * @param input          Virta, jota suodatetaan.
   * @param voikko         Voikko.
   * @param suggestionFile Tiedosto, josta korjausehdotukset luetaan.
   * @param successOnly    Jos 'true', suodatin päästää läpi vain ne sanat, jotka tunnistetaan,
   *                       jos false, myös tunnistamattomat sanat päästetään läpi.
   */
  public SuggestionFilter (TokenStream input, Voikko voikko, String suggestionFile, boolean successOnly)
  {
    super (input, voikko);
    try {
      LOG.info ("SuggestionFilter: aloitetaan (1).");
//System.out.println ("SuggestionFilter: aloitetaan (1).");
      parser = new SuggestionParser (voikko, suggestionFile);
      suggestion = parser.getSuggestions();
      this.successOnly = successOnly;
    }
    catch (SuggestionParser.SuggestionParserException e)
    {
      LOG.error ("SuggestionFilter(1): " + e.getMessage());
      if (e.getCause() != null) {
        LOG.error ("SuggestionFilter(2): " + e.getCause().getClass().getName() + " " + e.getCause().getMessage());
      }
      e.printStackTrace (System.out);
      throw new RuntimeException (e.getMessage());
    }
    catch (Throwable t)
    {
      throw new RuntimeException (t);
    }
  }


  /*
   * @param input          Virta, jota suodatetaan.
   * @param voikko         Voikko.
   * @param is             Virta, josta korjausehdotukset luetaan.
   * @param successOnly    Jos 'true', suodatin päästää läpi vain ne sanat, jotka tunnistetaan,
   *                       jos false, myös tunnistamattomat sanat päästetään läpi.
   */
  public SuggestionFilter (TokenStream input, Voikko voikko, InputStream is, boolean successOnly)
  {
    super (input, voikko);
    try {
      LOG.info ("SuggestionFilter: aloitetaan (3).");
      if (parser == null) parser = new SuggestionParser (voikko, is);
      suggestion = parser.getSuggestions();
      this.successOnly = successOnly;
    }
    catch (SuggestionParser.SuggestionParserException e)
    {
      LOG.error ("SuggestionFilter: " + e.getMessage());
      if (e.getCause() != null) LOG.error ("SuggestionFilter: " + e.getCause().getClass().getName() + " " + e.getCause().getMessage());
    }
    catch (Throwable t)
    {
      throw new RuntimeException (t);
    }
  }


  /*
   * @param input          Virta, jota suodatetaan.
   * @param voikko         Voikko.
   * @param suggestion     Korjaushedotukset.
   * @param successOnly    Jos 'true', suodatin päästää läpi vain ne sanat, jotka tunnistetaan,
   *                       jos false, myös tunnistamattomat sanat päästetään läpi.
   */
  public SuggestionFilter (TokenStream input, Voikko voikko, Suggestion[] suggestion, boolean successOnly)
  {
    super (input, voikko);
    try {
      this.suggestion = suggestion;
      this.successOnly = successOnly;
      if (LOG.isDebugEnabled()) LOG.debug ("SuggestionFilter: creating class " + getClass().getName() + ".");
    }
    catch (Throwable t)
    {
      throw new RuntimeException (t);
    }
  }


  @Override
  protected Iterator<String> filter()
  {
//System.out.println ("Word 0 " + word + " " + termAtt.toString() + " " + Constants.toString(flagsAtt));

    if (Constants.hasFlag (flagsAtt, Constants.HYPHEN)) {
      final Set<String> set1 = suggest (word);

//System.out.println ("Word 1 " + word + set1.toString());

      final String s = Constants.HYPHEN_REGEX.matcher(word).replaceAll ("");
      final Set<String> set2 = suggest (s);

//System.out.println ("Word 2 " + s + " " + set2.toString());


      final String[] p = Constants.HYPHEN_REGEX.split (word);
      final HashSet<String> pset = new HashSet<String>();

      for (int i = 0; i < p.length; i++) {
        Set<String> result = suggest (p[i]);
        if (result != null) {
          pset.addAll (result);
        }
      }
      if (set1 != null) pset.addAll (set1);
      if (set2 != null) pset.addAll (set2);
      return (pset.size() == 0 ? null : pset.iterator());
    }
    else {
      Set<String> s = suggest (word);
      if (s != null) return s.iterator();
    }
    return null;
  }


  protected Suggestion[] getSuggestions()
  {
//System.out.println ("SuggestionFilter.getSuggestions");
    return suggestion;
  }

  protected boolean getSuccessOnly() {return successOnly;}


  private Set<String> suggest (String word)
  {
//System.out.println ("Word a " + word);
    List<Analysis> list = voikko.analyze (word);
    if (list.size() > 0) {
      flagsAtt.setFlags (flagsAtt.getFlags() | Constants.FOUND);
      voikkoAtt.setAnalysis (list);
      baseFormAtt.addBaseForms (VoikkoUtils.getBaseForms (list));
//System.out.println ("Word b " + word + " " + Constants.toString(flagsAtt) + " " + VoikkoUtils.getBaseForms(list).toString());
      return baseFormAtt.getBaseForms();
    }
    else {
//System.out.println ("Word c " + word + " " + Constants.toString(flagsAtt));
      boolean suggestionResult = SuggestionUtils.getSuggestions (suggestion, word, voikkoAtt, baseFormAtt);
      if (suggestionResult) {
        flagsAtt.setFlags (flagsAtt.getFlags() | Constants.SUGGEST);
//System.out.println ("Word d " + word + " " + Constants.toString(flagsAtt) + " " + baseFormAtt.getBaseForms().toString());
        return baseFormAtt.getBaseForms();
      }
    }

    if (SuggestionUtils.analyze (voikko, word, voikkoAtt, baseFormAtt, flagsAtt, suggestion, parser.getFrom(), parser.getTo())) {
      flagsAtt.setFlags (flagsAtt.getFlags() | Constants.SUGGEST);
//System.out.println ("Word 5 " + word + " " + Constants.toString(flagsAtt) + " " + baseFormAtt.getBaseForms().toString());
      return baseFormAtt.getBaseForms();
    }
    else if (getSuccessOnly()) {
      return null;
    }
    else {
//System.out.println ("Word 6 " + word + " " + Constants.toString(flagsAtt));
      flagsAtt.setFlags (flagsAtt.getFlags() | Constants.UNKNOWN);
//System.out.println ("Word 7 " + word + " " + Constants.toString(flagsAtt));
      baseFormAtt.addBaseForm (word.toLowerCase());
      return baseFormAtt.getBaseForms();
    }
  }


  private static final Logger LOG = LoggerFactory.getLogger (SuggestionFilter.class);

  private Iterator<String> iterator;
  private boolean suggestionResult;

  private SuggestionParser parser;
  private Suggestion[] suggestion;
  private boolean successOnly;
}

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
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.puimula.libvoikko.*;
import peltomaa.sukija.attributes.OriginalWordAttribute;
import peltomaa.sukija.attributes.VoikkoAttribute;
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
System.out.println ("SuggestionFilter: aloitetaan (1).");
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
    }
  }


  /**
   * Sama kuin {@code SuggestionFilter (input, voikko, suggestionFile, false)}.<p>
   *
   * @param input          Virta, jota suodatetaan.
   * @param voikko         Voikko.
   * @param suggestionFile Tiedosto, josta korjausehdotukset luetaan.
   */
  public SuggestionFilter (TokenStream input, Voikko voikko, String suggestionFile)
  {
    this (input, voikko, suggestionFile, false);
    LOG.info ("SuggestionFilter: aloitetaan (2).");
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
  }


  /*
   * @param input          Virta, jota suodatetaan.
   * @param voikko         Voikko.
   * @param suggestion     Korjaushedotukset.
   * @param successOnly    Jos 'true', suodatin päästää läpi vain ne sanat, jotka tunnistetaan,
   *                       jos false, myös tunnistamattomat sanat päästetään läpi.
   */
  public SuggestionFilter (TokenStream input, Voikko voikko, Vector<Suggestion> suggestion, boolean successOnly)
  {
    super (input, voikko);
    this.suggestion = suggestion;
    this.successOnly = successOnly;
    if (LOG.isDebugEnabled()) LOG.debug ("SuggestionFilter: creating class " + getClass().getName() + ".");
  }


  @Override
  protected Iterator<String> filter()
  {
    if (VoikkoUtils.analyze (voikko, word, baseForms)) {
//System.out.println ("Filter1 " + word + " " + baseForms.toString());
      flagsAtt.setFlags (flagsAtt.getFlags() | Constants.WORD);
      return baseForms.iterator();
    }
    else {
//System.out.println ("Filter2 " + word);
      return suggest (word);
    }
  }


  protected Vector<Suggestion> getSuggestions()
  {
//System.out.println ("SuggestionFilter.getSuggestions");
    return suggestion;
  }

  protected boolean getSuccessOnly() {return successOnly;}


  /** Try suggestions.
   */
  private Iterator<String> suggest (String word)
  {
//System.out.println ("Suggest1 " + word);

    suggestionResult = SuggestionUtils.getSuggestions (suggestion, word);

    if (suggestionResult == null) { /* Ei korjausehdotuksia. */
      if (LOG.isDebugEnabled()) LOG.debug ("Suggest " + word);
//System.out.println ("Suggest2 " + word);
      if (successOnly) {
        return null;
      }
      else {
        flagsAtt.setFlags (flagsAtt.getFlags() | Constants.UNKNOWN);
        return baseForms.iterator();
      }
    }
    else {
      if (LOG.isDebugEnabled()) LOG.debug ("Suggest " + word + " " + Arrays.toString (suggestionResult.toArray (new String[0])));
//System.out.println ("Suggest3 " + word + " " + Arrays.toString (suggestionResult.toArray (new String[0])));
      flagsAtt.setFlags (flagsAtt.getFlags() | Constants.WORD);
      return suggestionResult.iterator();
    }
  }


  private static final Logger LOG = LoggerFactory.getLogger (SuggestionFilter.class);

  private Iterator<String> iterator;
  private Set<String> baseForms = new HashSet<String>();
  private Collection<String> suggestionResult;

  private SuggestionParser parser;
  private Vector<Suggestion> suggestion;
  private boolean successOnly;
}

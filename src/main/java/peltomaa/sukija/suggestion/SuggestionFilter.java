/*
Copyright (©) 2012-2018, 2020-2021 Hannu Väisänen

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.puimula.libvoikko.*;
import static peltomaa.sukija.util.Constants.*;
import peltomaa.sukija.util.AnalysisUtils;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.util.StringUtil;
import peltomaa.sukija.util.SukijaFilter;
import peltomaa.sukija.voikko.VoikkoUtils;
import peltomaa.sukija.suggestion.ahocorasick.AhoCorasickCorrector;


public final class SuggestionFilter extends SukijaFilter {

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
      parser = new SuggestionParser (voikko, suggestionFile); 
      suggestion = parser.getSuggestions();
      this.successOnly = successOnly;
    }
    catch (SuggestionParser.SuggestionParserException e)
    {
      e.printStackTrace (System.out);
      LOG.error ("SuggestionFilter(1): " + e.getMessage());
      if (e.getCause() != null) {
        LOG.error ("SuggestionFilter(2): " + e.getCause().getClass().getName() + " " + e.getCause().getMessage());
      }
      throw new RuntimeException (e.getMessage());
    }
    catch (Throwable t)
    {
      throw t;
//      throw new RuntimeException (t);
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
      LOG.info ("SuggestionFilter: aloitetaan (2).");
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
   * @param parser         Objekti, joka jäsentää korjausehdotukset.
   * @param successOnly    Jos 'true', suodatin päästää läpi vain ne sanat, jotka tunnistetaan,
   *                       jos false, myös tunnistamattomat sanat päästetään läpi.
   */
  public SuggestionFilter (TokenStream input, Voikko voikko, SuggestionParser parser, boolean successOnly)
  {
    super (input, voikko);
    try {
      this.parser = parser;
      this.successOnly = successOnly;
      this.suggestion = parser.getSuggestions();
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
     OffsetAttribute offsetAtt = input.getAttribute (OffsetAttribute.class);

if (LOG.isDebugEnabled()) LOG.debug("Word-f A " + word + " " + termAtt.toString()
                                     + " " + offsetAtt.startOffset() + " " + offsetAtt.endOffset()
                                     + " " + Constants.toString (flagsAtt));

    if (hasFlag (flagsAtt, LATEX_HYPHEN)) {
      word = word.replace ("\\-", "");
    }

    final int n = word.lastIndexOf ('-');
    if (n > 0) {
      Constants.addFlags (flagsAtt, Constants.COMPOUND_WORD);
    }
    else {
      Constants.removeFlags (flagsAtt, Constants.COMPOUND_WORD);
    }
   
if (LOG.isDebugEnabled()) LOG.debug ("Word-f B " + word + " " + termAtt.toString() + " " + Constants.toString (flagsAtt));

    if (hasFlag (flagsAtt, Constants.COMPOUND_WORD)) {
      if (AnalysisUtils.analyze (voikko, word, voikkoAtt, baseFormAtt, flagsAtt)) {
        return baseFormAtt.getBaseForms().iterator();
      }
      else {
        // Väärin käytetty yhdys-viiva.
        // Jos yhdys-sanaa ei tunnisteta, muutetaan sen viimeisen yhdysviivan jälkeinen osa
        // perusmuotoon ja liitetään alkuosa siihen yhdysviivan kanssa ja ilman.
        //
if (LOG.isDebugEnabled()) LOG.debug ("Word-f C " + word + " " + termAtt.toString() + " " + Constants.toString (flagsAtt));
        final String START = word.substring (0, n);
        final String END   = word.substring (n+1);
if (LOG.isDebugEnabled()) LOG.debug ("Word-f D " + word + " " + termAtt.toString() + " " + Constants.toString (flagsAtt) + " [" + START + "] " + END);
        Set<String> baseForms = new HashSet<String>();
        Set<String> result = suggest (END);
if (LOG.isDebugEnabled()) LOG.debug ("Word-f E " + END + " " + result.toString());
        if (result != null) {
          for (String u : result) {
            baseForms.add (START + "-" + u);
            baseForms.add (START + u);
          }
          return baseForms.iterator();
        }
      }
    }
    else {
      Set<String> s = suggest (word);
      if (s != null) {
        return s.iterator();
      }
    }
    return null;
  }


  private Set<String> suggest (String word)
  {
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s a " + word + " " + Constants.toString(flagsAtt));
    List<Analysis> list = voikko.analyze (word);
    if (list.size() > 0) {
if (LOG.isDebugEnabled()) LOG.debug ("       " + list.toString());
      flagsAtt.setFlags (flagsAtt.getFlags() | FOUND);
      voikkoAtt.setAnalysis (list);
      baseFormAtt.addBaseForms (AhoCorasickCorrector.getCorrections (list));
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s b " + word + " " + Constants.toString(flagsAtt) + " bf= " + VoikkoUtils.getBaseForms(list).toString());
      return baseFormAtt.getBaseForms();
    }
    else {
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s c " + word + " " + Constants.toString(flagsAtt) + " bf= " + baseFormAtt.getBaseForms().toString());
      Set<String> set = new HashSet<String>();
      boolean suggestionResult = SuggestionUtils.getSuggestions (suggestion, word, voikkoAtt, set);
      if (suggestionResult) {
        flagsAtt.setFlags (flagsAtt.getFlags() | SUGGEST);
        baseFormAtt.addBaseForms (set);
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s d " + word + " " + Constants.toString(flagsAtt) + " bf= " + baseFormAtt.getBaseForms().toString());
        return baseFormAtt.getBaseForms();
      }
    }

    if (parser == null) {
      throw new RuntimeException ("SuggestionFilter: parser == null");
    }

    if (AnalysisUtils.analyze (voikko, word, voikkoAtt, baseFormAtt, flagsAtt, suggestion, parser.getFrom(), parser.getTo())) {
      flagsAtt.setFlags (flagsAtt.getFlags() | SUGGEST);
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s e " + word + " " + Constants.toString(flagsAtt) + " bf= " + baseFormAtt.getBaseForms().toString());
      return baseFormAtt.getBaseForms();
    }
    else if (successOnly) {
      return null;
    }
    else {
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s f " + word + " " + Constants.toString(flagsAtt));
      flagsAtt.setFlags (flagsAtt.getFlags() | UNKNOWN);
      baseFormAtt.addBaseForm (word.toLowerCase());
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s g " + word + " " + Constants.toString(flagsAtt) + " bf= " + baseFormAtt.getBaseForms().toString());
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

/*
Copyright (©) 2012-2018 Hannu Väisänen

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.Vector;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.puimula.libvoikko.*;
import static peltomaa.sukija.util.Constants.*;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.util.StringUtil;
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
//      LOG.info ("SuggestionFilter: aloitetaan (1).");
//System.out.println ("SuggestionFilter: aloitetaan (1).");
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
//      LOG.info ("SuggestionFilter: aloitetaan (3).");
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
//System.out.println ("Word 0 " + word + " " + termAtt.toString() + " " + Constants.toString (flagsAtt));

    if (hasFlag (flagsAtt, DASH)) {
      word = word.replace ("\\-", "");
    }
//System.out.println ("Word a " + word);

    if (hasFlag (flagsAtt, HYPHEN)) {
//System.out.println ("Word 1 " + word);
      final Set<String> set1 = suggest (HYPHEN_REGEX.matcher(word).replaceAll("-"));

//System.out.println ("Word 2 " + word + " " + set1.toString());

      final Set<String> set2 = try_word_without_hyphen (word);

//System.out.println ("Word 3 " + word + " " + set2.toString());

      final String[] p = HYPHEN_REGEX.split (word);
      final HashSet<String> pset = new HashSet<String>();

      for (int i = 0; i < p.length; i++) {
        Set<String> result = suggest (p[i]);
        if (result != null) {
//System.out.println ("Word 4 " + p[i] + " " + result.toString() + " " + Constants.toString (flagsAtt));
          pset.addAll (result);
        }
      }
      if (set1 != null) pset.addAll (set1);
      if (set2 != null) pset.addAll (set2);

      if (pset.size() > 0) {
//System.out.println ("pset1 " + word + " " + Constants.toString(flagsAtt) + " " + pset.toString());
        if (hasFlag (flagsAtt, FOUND)) {
          removeFlags (flagsAtt, SUGGEST, UNKNOWN);
        }
        else if (hasFlag (flagsAtt, SUGGEST)) {
          removeFlags (flagsAtt, UNKNOWN);
        }
//System.out.println ("pset2 " + word + " " + Constants.toString(flagsAtt) + " " + pset.toString());
        return pset.iterator();
      }
    }
    else {
//System.out.println ("Word Z " + word + " " + termAtt.toString() + " " + Constants.toString (flagsAtt));
      Set<String> s = suggest (word);
      if (s != null) return s.iterator();
    }
//System.out.println ("Word Ö " + word + " " + termAtt.toString() + " " + Constants.toString (flagsAtt));
    return null;
  }


  private Set<String> suggest (String word)
  {
//System.out.println ("Word a " + word + " " + Constants.toString(flagsAtt));
    List<Analysis> list = voikko.analyze (word);
    if (list.size() > 0) {
//System.out.println ("       " + list.toString());
      flagsAtt.setFlags (flagsAtt.getFlags() | FOUND);
      voikkoAtt.setAnalysis (list);
      baseFormAtt.addBaseForms (VoikkoUtils.getBaseForms (list));
//System.out.println ("Word b " + word + " " + Constants.toString(flagsAtt) + " " + VoikkoUtils.getBaseForms(list).toString());
      return baseFormAtt.getBaseForms();
    }
    else {
//System.out.println ("Word c " + word + " " + Constants.toString(flagsAtt) + " " + baseFormAtt.getBaseForms().toString());
      Set<String> set = new HashSet<String>();
      boolean suggestionResult = SuggestionUtils.getSuggestions (suggestion, word, voikkoAtt, set);
      if (suggestionResult) {
        flagsAtt.setFlags (flagsAtt.getFlags() | SUGGEST);
        baseFormAtt.addBaseForms (set);
//System.out.println ("Word d " + word + " " + Constants.toString(flagsAtt) + " " + baseFormAtt.getBaseForms().toString());
        return baseFormAtt.getBaseForms();
      }
    }

    if (parser == null) {
      throw new RuntimeException ("SuggestionFilter: parser == null");
    }

    if (SuggestionUtils.analyze (voikko, word, voikkoAtt, baseFormAtt, flagsAtt, suggestion, parser.getFrom(), parser.getTo())) {
      flagsAtt.setFlags (flagsAtt.getFlags() | SUGGEST);
//System.out.println ("Word 5 " + word + " " + Constants.toString(flagsAtt) + " " + baseFormAtt.getBaseForms().toString());
      return baseFormAtt.getBaseForms();
    }
    else if (successOnly) {
      return null;
    }
    else {
//System.out.println ("Word 6 " + word + " " + Constants.toString(flagsAtt));
      flagsAtt.setFlags (flagsAtt.getFlags() | UNKNOWN);
//System.out.println ("Word 7 " + word + " " + Constants.toString(flagsAtt));
      baseFormAtt.addBaseForm (word.toLowerCase());
      return baseFormAtt.getBaseForms();
    }
  }


  private Set<String> try_word_without_hyphen (String word)
  {
    final Matcher m = HYPHEN_REGEX.matcher (word);
    final StringBuffer sb = new StringBuffer();

    while (m.find()) {
//System.out.println ("A " + m.start() + " " + m.end() + " " + m.group());
      m.appendReplacement (sb, separator (word, m.start(), m.end()));
    }
    m.appendTail (sb);
//System.out.println ("B " + sb.toString());
//    return suggest (sb.toString());
    final Set<String> s = suggest (sb.toString());
//System.out.println ("C " + s.toString());
    return s;
  }


  private String separator (String word, int start, int end)
  {
    if (word.charAt(start-1) == word.charAt(end) &&
        StringUtil.isVowel(word.charAt(start-1))) {
      return "-";
    }
    else {
      return "";
    }
  }


  private static final Logger LOG = LoggerFactory.getLogger (SuggestionFilter.class);
  private static final Pattern HYPHEN_REGEX = Pattern.compile ("-+|\"-+|–+|''-+|'-+|[.]-+");

  private Iterator<String> iterator;
  private boolean suggestionResult;

  private SuggestionParser parser;
  private Suggestion[] suggestion;
  private boolean successOnly;
}

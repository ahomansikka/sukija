/*
Copyright (©) 2012-2018, 2020-2022 Hannu Väisänen

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.puimula.libvoikko.*;
import static peltomaa.sukija.util.Constants.*;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.util.NgramUtils;
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
      LOG.info ("SuggestionFilter: aloitetaan (1) parser.");
      suggestion = parser.getSuggestions();
      this.successOnly = successOnly;
    }
    catch (SuggestionParser.SuggestionParserException e)
    {
      e.printStackTrace (System.out);
      LOG.error ("SuggestionFilter(1): " + e.getMessage());
      if (e.getCause() != null) {
e.getCause().printStackTrace (System.out);
        LOG.error ("SuggestionFilter(2): " + e.getCause().getClass().getName() + " " + e.getCause().getMessage());
      }
e.printStackTrace (System.out);
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

 if (LOG.isDebugEnabled()) LOG.info ("Word-f A " + word + " " + termAtt.toString()
                                      + " " + offsetAtt.startOffset() + " " + offsetAtt.endOffset()
                                      + " " + Constants.toString (flagsAtt)
                                      + " " + originalWordAtt.getOriginalWord());
/*
    if (hasFlag (flagsAtt, LATEX_HYPHEN)) {
      word = word.replace ("\\-", "");
    }


    final int n = word.lastIndexOf ('-');
    if (n > 0) {
      Constants.addFlags (flagsAtt, Constants.COMPOUND_WORD);
    }
    else {
if (n == 0) {System.out.println ("HHHHHHHHHHHHHHH " + word); System.exit(1);}
      Constants.removeFlags (flagsAtt, Constants.COMPOUND_WORD);
    }
   
if (LOG.isDebugEnabled()) LOG.debug ("Word-f B " + word + " " + termAtt.toString() + " " + Constants.toString (flagsAtt));
1*/

    if (hasFlag (flagsAtt, Constants.COMPOUND_WORD)) {
if (LOG.isDebugEnabled()) LOG.debug ("Word-f C " + word + " " + termAtt.toString() + " " + Constants.toString (flagsAtt));
      final Set<String> result = analyzeCompoundWord (word);
      if (hasFlag (flagsAtt, FOUND)) Constants.removeFlags (flagsAtt, UNKNOWN, SUGGEST);
      if (hasFlag (flagsAtt, SUGGEST)) Constants.removeFlags (flagsAtt, UNKNOWN);
      baseFormAtt.addBaseForms (result);
if (LOG.isDebugEnabled()) {for (String s : result) {LOG.debug ("Word-f D " + word + " " + termAtt.toString() + " " + Constants.toString (flagsAtt) + " " + s);}}
      return result.iterator();
    }
    else {
if (LOG.isDebugEnabled()) LOG.debug ("Word-f E " + word + " " + termAtt.toString() + " " + Constants.toString (flagsAtt));
      final Set<String> s = suggest (word);
      if (s != null) {
        if (hasFlag (flagsAtt, FOUND)) Constants.removeFlags (flagsAtt, UNKNOWN, SUGGEST);
        if (hasFlag (flagsAtt, SUGGEST)) Constants.removeFlags (flagsAtt, UNKNOWN);
        return s.iterator();
      }
    }
    return null;
  }


  private Set<String> suggest (String word)
  {
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s a " + word + " " + Constants.toString(flagsAtt));
    final List<Analysis> list = voikko.analyze (word);
    if (list.size() > 0) {
if (LOG.isDebugEnabled()) LOG.debug ("  W1   " + list.size() + " " + Constants.toString(flagsAtt));
      flagsAtt.setFlags (flagsAtt.getFlags() | FOUND);
if (LOG.isDebugEnabled()) LOG.debug ("  W2   " + list.size() + " " + Constants.toString(flagsAtt));
      voikkoAtt.setAnalysis (list);
      baseFormAtt.addBaseForms (AhoCorasickCorrector.getCorrections (list));
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s b " + word + " " + Constants.toString(flagsAtt) + " bf= " + VoikkoUtils.getBaseForms(list).toString());
      return baseFormAtt.getBaseForms();
    }
    else {
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s c " + word + " " + Constants.toString(flagsAtt) + " bf= " + baseFormAtt.getBaseForms().toString());
      List<Analysis> list2 = new ArrayList<Analysis>();
      boolean suggestionResult = SuggestionUtils.getSuggestions (suggestion, word, list2, baseFormAtt, voikkoAtt);
      if (suggestionResult) {
        flagsAtt.setFlags (flagsAtt.getFlags() | SUGGEST);
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s d " + word + " " + Constants.toString(flagsAtt) + " bf= " + baseFormAtt.getBaseForms().toString());
        return baseFormAtt.getBaseForms();
      }
    }

    if (parser == null) {
      throw new RuntimeException ("SuggestionFilter: parser == null");
    }

    final List<Analysis> listP = new ArrayList<Analysis>();
    if (SuggestionUtils.getSuggestions (voikko, suggestion, word, listP, baseFormAtt, voikkoAtt, parser.getFrom(), parser.getTo())) {
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


/*******************************************************************************************/

  private int suggest2 (String word)
  {
    analysisList.clear();
    Set<String> extraBaseForms = new HashSet<String>();

if (LOG.isDebugEnabled()) LOG.debug ("  Word-s a " + word + " " + Constants.toString(flagsAtt));
    analysisList.addAll (voikko.analyze (word));
    if (analysisList.size() > 0) {
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s b " + word);
      return FOUND;
    }
    else {
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s c " + word);
      boolean suggestionResult = SuggestionUtils.getSuggestions (suggestion, word, analysisList, extraBaseForms);
      if (suggestionResult) {
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s d " + word);
        return SUGGEST;
      }
    }

    if (parser == null) {
      throw new RuntimeException ("SuggestionFilter: parser == null");
    }

    if (SuggestionUtils.getSuggestions (voikko, suggestion, word, analysisList, extraBaseForms, parser.getFrom(), parser.getTo())) {
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s e " + word);
      return SUGGEST;
    }
    else if (successOnly) {
      return NOT_INDEXED;
    }
    else {
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s f " + word + " " + VoikkoUtils.newBaseForm (word.toLowerCase()));
      analysisList.add (VoikkoUtils.newBaseForm (word.toLowerCase()));
if (LOG.isDebugEnabled()) LOG.debug ("  Word-s g " + word + " " + analysisList + " " + analysisList.size());
      return UNKNOWN;
    }
  }

/*******************************************************************************************/

  /** Yhdyssana, jossa on yhdysviiva oikein tai väärin, esim. linja-autossa, kuu-kausi.
   *  Jaetaan sana osiin yhdysviivan kohdalta, tunnistetaan erikseen ja yhdistetään
   *  yhdysviivan kanssa ja ilman.
   */
  private Set<String> analyzeCompoundWord (String word)
  {
if (LOG.isDebugEnabled()) LOG.debug ("analyzeCompoundWord0 " + word + " " + baseFormAtt.getBaseForms());

    final Set<String> result = new HashSet<String>();
    final Set<String> ngrams = NgramUtils.unique_ngram (word);
    final String[] array = PATTERN.split (word);
    final List<Set<String>> output = new ArrayList<Set<String>>();

if (LOG.isDebugEnabled()) {
  LOG.debug ("analyzeCompoundWord1 " + java.util.Arrays.toString(array));
  LOG.debug ("analyzeCompoundWord2 " + ngrams);
  for (String s : ngrams) LOG.debug ("analyzeCompoundWordA " + word + " " + s);
}

    for (String s : ngrams) {
      final Set<String> set = analyzeWord (s);
      output.add (set);
if (LOG.isDebugEnabled()) LOG.debug ("analyzeCompoundWordB " + word + " " + s + " " + set);
    }

if (LOG.isDebugEnabled()) {
  LOG.debug ("analyzeCompoundWordC " + word + " " + ngrams.size() + " " + output.size()); 
  for (Set<String> s : output) LOG.debug ("analyzeCompoundWordD " + word + " " + s);
}

    for (Set<String> u : output) {
      for (String v : u) {
        result.add (v);
if (LOG.isDebugEnabled()) LOG.debug ("analyzeCompoundWordD " + v + " | " + word);
      }
    }
if (LOG.isDebugEnabled()) for (Set<String> s : output) LOG.debug ("analyzeCompoundWordE " + word + " " + result + " " + result.size());
    return result;
  }


  private Set<String> analyzeWord (String word)
  {
if (LOG.isDebugEnabled()) LOG.debug ("analyzeWordA " + word);
    final int n = word.lastIndexOf ("-");
    if (n == -1) {
if (LOG.isDebugEnabled()) LOG.debug ("analyzeWord0 " + word + " " +  analyzeOneWord (word));
      return analyzeOneWord (word);
    }
    else {
      final Set<String> result = new HashSet<String>();
      final String[] array = PATTERN.split (word);
      final List<Set<String>> output = new ArrayList<Set<String>>();
      for (String s : array) {
        final Set<String> u = analyzeOneWord (s);
        output.add (u);
if (LOG.isDebugEnabled()) LOG.debug ("analyzeWord1 " + word + " " + u + " " + array.length);
      }
if (LOG.isDebugEnabled()) LOG.debug ("analyzeWord2 " + word + " " + output + " " + output.size());

      final Set<String> all = combineAll (output);

if (LOG.isDebugEnabled()) LOG.debug ("analyzeWord3 " + word + " " + all);
      return all;
    }
  }


  private Set<String> analyzeOneWord (String word)
  {
    if (word.lastIndexOf("-") > 0) throw new IllegalArgumentException ("Sanassa '" + word + "' on yhdysviiva, mutta ei pitäisi.");

if (LOG.isDebugEnabled()) LOG.debug ("analyzeOneWordA " + word);

    final List<Analysis> list = voikko.analyze (word);
    if (list.size() > 0) {
      Constants.addFlags (flagsAtt, FOUND);
      Constants.removeFlags (flagsAtt, UNKNOWN, SUGGEST);
      voikkoAtt.setAnalysis (list);
if (LOG.isDebugEnabled()) LOG.debug ("analyzeOneWordB " + word + " " + VoikkoUtils.getBaseForms(list) + " " + Constants.toString(flagsAtt));
      return AhoCorasickCorrector.getCorrections (list);
    }
    else {
      final int n = suggest2 (word);
if (LOG.isDebugEnabled()) LOG.debug ("analyzeOneWordC " + word + " " + Constants.toString(n) + " " + analysisList);
      Set<String> set = new HashSet<String>();
      if (n == NOT_INDEXED) return set;
      Constants.addFlags (flagsAtt, n);
      voikkoAtt.setAnalysis (analysisList);
      set.addAll (VoikkoUtils.getBaseForms (analysisList));
      return set;
    }
  }


  private Set<String> combineAll (List<Set<String>> list)
  {
if (LOG.isDebugEnabled()) LOG.debug ("combineAll0 " + list + " " + list.size());

    final Set<String> resultSet = new HashSet<String>();
    final List<Set<String>> resultList = new ArrayList<Set<String>>();

    if (list.size() == 1) {
if (LOG.isDebugEnabled()) LOG.debug ("combineAll1 " + list + " " + list.size());
      return list.get(0);
    }
    else if (list.size() == 2) {
if (LOG.isDebugEnabled()) LOG.debug ("combineAll2 " + list + " " + list.size());
      return combine (list.get(0), list.get(1));
    }
    else if (list.size() == 3) {
      //
      // 0-1-2 => 0-1 1-2 0-1-2
      //
if (LOG.isDebugEnabled()) LOG.debug ("combineAll3 " + list + " " + list.size());
      final Set<String> set1 = combine (list.get(0), list.get(1));
      final Set<String> set2 = combine (list.get(1), list.get(2));
      final Set<String> set3 = combine (set1, list.get(2));
      final Set<String> set4 = addAll (set1, set2, set3);
if (LOG.isDebugEnabled()) LOG.debug ("combineAll4 " + set4 + " " + set4.size());
      return set4;
    }
    else if (list.size() == 4) {
      //
      // 0-1-2-3 => 0-1  1-2  2-3  0-1-2  1-2-3  0-1-2-3
      //
if (LOG.isDebugEnabled()) LOG.debug ("combineAll5 " + list + " " + list.size());
      final Set<String> set1 = combine (list.get(0), list.get(1));
      final Set<String> set2 = combine (list.get(1), list.get(2));
      final Set<String> set3 = combine (list.get(2), list.get(3));
      final Set<String> set4 = combine (set1,        list.get(2));
      final Set<String> set5 = combine (set2,        list.get(3));
      final Set<String> set6 = combine (list.get(0), set5);
      final Set<String> set7 = addAll (set1, set2, set3, set4, set5, set6);
if (LOG.isDebugEnabled()) LOG.debug ("combineAll6 " + set7 + " " + set7.size());
      return set7;
    }
    else {
      final Set<String> set = new HashSet<String>();
      for (Set<String> s : list) {
        set.addAll (s);
      }
if (LOG.isDebugEnabled()) LOG.debug ("combineAll7 " + set + " " + set.size());
      return set;
    }
  }


  private Set<String> combine (Set<String> set1, Set<String> set2)
  {
    final Set<String> out = new HashSet<String>();

    for (String u : set1) {
      for (String v : set2) {
        if (NgramUtils.f(u,v).compareTo("-") != 0) out.add (u + v);
        out.add (u + "-" + v);
      }
    }
if (LOG.isDebugEnabled()) LOG.debug ("combineAllÖ " + out);
    return out;
  }


  @SafeVarargs
  private Set<String> addAll (Set<String>... list)
  {
    final Set<String> out = new HashSet<String>();
    for (Set<String> set : list) out.addAll (set);
    return out;
  }


  private static final Logger LOG = LoggerFactory.getLogger (SuggestionFilter.class);
  private static final Pattern PATTERN = Pattern.compile("-");

  private Iterator<String> iterator;
  private boolean suggestionResult;
  private List<Analysis> analysisList = new ArrayList<Analysis>();

  private SuggestionParser parser;
  private Suggestion[] suggestion;
  private boolean successOnly;
}

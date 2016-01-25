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


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.lucene.analysis.TokenStream;
import org.puimula.libvoikko.*;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.util.SukijaFilter;
import peltomaa.sukija.voikko.VoikkoUtils;


public final class MultiSuggestionFilter extends SuggestionFilter {
  /**
   * @param input          Virta, jota suodatetaan.
   * @param voikko         Voikko.
   * @param from
   * @param to
   * @param suggestionFile Tiedosto, josta korjausehdotukset luetaan.
   * @param successOnly    Jos 'true', suodatin päästää läpi vain ne sanat, jotka tunnistetaan,
   *                       jos false, myös tunnistamattomat sanat päästetään läpi.
   */
  public MultiSuggestionFilter (TokenStream input, Voikko voikko, String from, String to,
                                String suggestionFile, boolean successOnly)
  {
    super (input, voikko, suggestionFile, successOnly);
    init (from, to);
  }


  /*
   * @param input          Virta, jota suodatetaan.
   * @param voikko         Voikko.
   * @param from
   * @param to
   * @param suggestion     Korjaushedotukset.
   * @param successOnly    Jos 'true', suodatin päästää läpi vain ne sanat, jotka tunnistetaan,
   *                       jos false, myös tunnistamattomat sanat päästetään läpi.
   */
  public MultiSuggestionFilter (TokenStream input, Voikko voikko, String from, String to,
                                Vector<Suggestion> suggestion, boolean successOnly)
  {
    super (input, voikko, suggestion, successOnly);
    init (from, to);
  }


  @Override
  protected Iterator<String> filter()
  {
    if (VoikkoUtils.analyze (voikko, word, baseForms)) {
      flagsAtt.setFlags (flagsAtt.getFlags() | Constants.WORD);
//System.out.println ("Word 1 " + word + " " + baseForms.toString());
      return baseForms.iterator();
    }
    else {
//System.out.println ("Word 2 " + word);
      suggestionResult = SuggestionUtils.getSuggestions (getSuggestions(), word);
      if (suggestionResult != null) {
        flagsAtt.setFlags (flagsAtt.getFlags() | Constants.WORD);
//System.out.println ("Word 3 " + word + " " + suggestionResult.toString());
        return suggestionResult.iterator();
      }
    }

    if (SuggestionUtils.analyze (word, baseForms, multiSuggestion, from, to)) {
//System.out.println ("Word 4 " + word + " " + baseForms.toString());
      flagsAtt.setFlags (flagsAtt.getFlags() | Constants.WORD);
      return baseForms.iterator();
    }
    else if (getSuccessOnly()) {
      return null;
    }
    else {
//System.out.println ("Word 5 " + word);
      flagsAtt.setFlags (flagsAtt.getFlags() | Constants.UNKNOWN);
      return baseForms.iterator();
    }
  }


  private void init (String from, String to)
  {
    if (from == null || to == null) throw new IllegalArgumentException ("Parametri 'from' tai 'to' on null.");
    if (from.length() != to.length()) throw new IllegalArgumentException ("Parametrit 'from' ja 'to' ovat eripituiset.");

    this.from = from;
    this.to = to;
    multiSuggestion = SuggestionUtils.removeSuggestions (getSuggestions(), "CharSuggestion");
  }


  private String from;
  private String to;
  private static final Logger LOG = LoggerFactory.getLogger (MultiSuggestionFilter.class);

  private Iterator<String> iterator;
  private Set<String> baseForms = new HashSet<String>();
  private Collection<String> suggestionResult;
  private SuggestionParser parser;
  Vector<Suggestion> multiSuggestion;
}

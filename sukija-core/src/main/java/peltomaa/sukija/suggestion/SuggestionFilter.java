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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import org.apache.lucene.analysis.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.util.SukijaFilter;



public class SuggestionFilter extends SukijaFilter {
  /**
   * @param in
   * @param morphology     Käytetty morfologia (Malaga tai Voikko).
   * @param suggestionFile Tiedosto, josta korjausehdotukset luetaan.
   * @param successOnly    Jos 'true', suodatin päästää läpi vain ne sanat, jotka tunnistetaan,
   *                       jos false, myös tunnistamattomat sanat päästetään läpi.
   */
  public SuggestionFilter (TokenStream in, Morphology morphology, String suggestionFile, boolean successOnly)
  {
    super (in);
    try {
      LOG.info ("SuggestionFilter: aloitetaan.");
      parser = new SuggestionParser (morphology, suggestionFile);
      suggestion = parser.getSuggestions();
      this.morphology = morphology;
      this.successOnly = successOnly;
      if (LOG.isDebugEnabled()) LOG.debug ("SuggestionFilter: creating class " + getClass().getName() + ".");
    }
    catch (SuggestionParser.SuggestionParserException e)
    {
      LOG.error ("SuggestionFilter: " + e.getMessage());
    }
  }


  /**
   * Sama kuin {@code SuggestionFilter (in, morphology, suggestionFile, false)}.<p>
   *
   * @param in
   * @param morphology     Käytetty morfologia (Malaga tai Voikko).
   * @param suggestionFile Tiedosto, josta korjausehdotukset luetaan.
   */
  public SuggestionFilter (TokenStream in, Morphology morphology, String suggestionFile)
  {
    this (in, morphology, suggestionFile, false);
  }


  public SuggestionFilter (TokenStream in, Morphology morphology, InputStream is, boolean successOnly)
  {
    super (in);
    try {
      LOG.info ("SuggestionFilter: aloitetaan.");
      parser = new SuggestionParser (morphology, is);
      suggestion = parser.getSuggestions();
      this.morphology = morphology;
      this.successOnly = successOnly;
      if (LOG.isDebugEnabled()) LOG.debug ("SuggestionFilter: creating class " + getClass().getName() + ".");
    }
    catch (SuggestionParser.SuggestionParserException e)
    {
      LOG.error ("SuggestionFilter: " + e.getMessage());
    }
  }


  protected void filter (String word)
  {
    set.clear();
    if (morphology.analyzeLowerCase (word, set)) {
      iterator = set.iterator();
    }
    else {
      iterator = suggest (word.toLowerCase());
    }
  }


  /** Try suggestions.
    */
  private Iterator<String> suggest (String term)
  {
    suggestionSet = getSuggestions (term);

    if (suggestionSet == null) { /* No suggestions found. */
      if (LOG.isDebugEnabled()) LOG.debug ("Suggest1 " + term);
      return (successOnly ? null : set.iterator());
    }
    else {
      if (LOG.isDebugEnabled()) LOG.debug ("Suggest2 " + term + " " + Arrays.toString (suggestionSet.toArray (new String[0])));
      return suggestionSet.iterator();
    }
  }


  private Set<String> getSuggestions (String word)
  {
    for (int i = 0; i < suggestion.size(); i++) {
      if (suggestion.get(i).suggest (word)) {
        return suggestion.get(i).getResult();
      }
    }
    return null;
  }


  private static final Logger LOG = LoggerFactory.getLogger (SuggestionFilter.class);

  private Set<String> set = new TreeSet<String>();
  private Set<String> suggestionSet;
  private Morphology morphology;
  private SuggestionParser parser;
  private Vector<Suggestion> suggestion;
  private boolean successOnly;
}

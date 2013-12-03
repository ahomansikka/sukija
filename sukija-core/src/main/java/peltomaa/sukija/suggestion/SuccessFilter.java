/*
Copyright (©) 2013 Hannu Väisänen

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
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import peltomaa.sukija.morphology.Morphology;


/**
 * Luokka, joka palauttaa vain ne sanat, joille on löytynyt perusmuoto.
 * Sanoista palautetaan vain perusmuodot.
 */
public class SuccessFilter extends TokenFilter {
  public SuccessFilter (TokenStream in, Morphology morphology, String suggestionFile)
  {
    super (in);
    try {
      parser = new SuggestionParser (morphology, new FileReader (suggestionFile));
      suggestion = parser.getSuggestions();

      this.morphology = morphology;

      if (LOG.isDebugEnabled()) LOG.debug ("SuccessFilter: creating class " + getClass().getName() + ".");
    }
    catch (FileNotFoundException e)
    {
      LOG.error ("SuccessFilter: " + e.getMessage());
    }
    catch (IOException e)
    {
      LOG.error ("SuccessFilter: " + e.getMessage());
    }
  }


  @Override
  public final boolean incrementToken() throws IOException
  {
     while ((iterator == null) || (!iterator.hasNext())) {
      if (!input.incrementToken()) {
         return false;
      }
      final String word = termAtt.toString();
      if (word == null || word.length() == 0) {
        return false;
      }
      set.clear();
      positionIncrement = 1;
      if (morphology.analyzeLowerCase (word, set)) {
        iterator = set.iterator();
      }
      else {
        suggest (word.toLowerCase());
      }
      savedState = captureState();
    }

    if (iterator.hasNext()) {
      restoreState (savedState);
      String baseWord = (String)iterator.next();
      termAtt.setEmpty();
      termAtt.append (baseWord);
      posIncrAtt.setPositionIncrement (positionIncrement);
      positionIncrement = 0;
      return true;
    }
    return false;
  }


  /* Try suggestions.
   */
  private void suggest (String term)
  {
    suggestionSet = getSuggestions (term);

    if (suggestionSet == null) { /* No suggestions found. */
      iterator = null;
      if (LOG.isDebugEnabled()) LOG.debug ("SuccessNotFound " + term);
    }
    else {
      iterator = suggestionSet.iterator();
      if (LOG.isDebugEnabled()) LOG.debug ("Success " + term + " " + Arrays.toString (suggestionSet.toArray (new String[0])));
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


  private static final Logger LOG = LoggerFactory.getLogger (SuccessFilter.class);

  private Iterator<String> iterator;
  private Set<String> set = new TreeSet<String>();
  private Set<String> suggestionSet;
  private int positionIncrement = 1;
  private State savedState;

  private final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  private final PositionIncrementAttribute posIncrAtt = addAttribute (PositionIncrementAttribute.class);

  private Morphology morphology;
  private SuggestionParser parser;
  private Vector<Suggestion> suggestion;
}

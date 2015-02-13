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
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;
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


  public SuggestionFilter (TokenStream in, Morphology morphology, Vector<Suggestion> suggestion, boolean stopOnSuccess)
  {
    super (in);
    this.morphology = morphology;
    this.suggestion = suggestion;
    this.successOnly = successOnly;
    if (LOG.isDebugEnabled()) LOG.debug ("SuggestionFilter: creating class " + getClass().getName() + ".");
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
  private Iterator<String> suggest (String word)
  {
    suggestionSet = getSuggestions (word);
    if (suggestionSet == null) { /* No suggestions found. */
      if (LOG.isDebugEnabled()) LOG.debug ("Suggest1 " + word);
      return (successOnly ? null : set.iterator());
    }
    else {
      if (LOG.isDebugEnabled()) LOG.debug ("Suggest2 " + word + " " + Arrays.toString (suggestionSet.toArray (new String[0])));
      return suggestionSet.iterator();
    }
  }


  private Set<String> getSuggestions (String word)
  {
    final Set<String> s = trySuggestions (word);
    if (s != null) {
      return s;
    }
    else {
      final String[] array = SPLIT.split (word);
      if (array.length > 1) {
        return trySuggestions (array);
      }
      return null;
    }
  }


  private Set<String> trySuggestions (String[] word)
  {
    Set<String> set = new HashSet<String>();
    Set<String> tmp = new HashSet<String>();

    for (int i = 0; i < word.length; i++) {
      tmp.clear();
      if (morphology.analyzeLowerCase (word[i], tmp)) {
        set.addAll (tmp);
      }
      else {
        Set<String> s = trySuggestions (word[i]);
        System.out.println ("Huuhaa: " + word.length + " " + join(word) + " " + word[i] + " " + ((s==null) ? null : s.toString()));
        if (s != null) set.addAll (s);
      }
    }

    if (set.size() > 0) {
      System.out.print ("SPLIT " + join (word) + ": ");
      for (String p : word) System.out.print (p + " ");
      System.out.println (" : " + set.toString());

      return set;
    }
    else {
      return null;
    }
  }


  private Set<String> trySuggestions (String word)
  {
    for (int i = 0; i < suggestion.size(); i++) {
      if (suggestion.get(i).suggest (word)) {
        return suggestion.get(i).getResult();
      }
    }
    return null;
  }


  private String join (String[] s)
  {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < s.length; i++) {
      sb.append (s[i]);
      if (i < s.length-1) sb.append ('-');
    }
    return sb.toString();
  }


  private static final Logger LOG = LoggerFactory.getLogger (SuggestionFilter.class);

  private Set<String> set = new TreeSet<String>();
  private Set<String> suggestionSet;
  private Morphology morphology;
  private SuggestionParser parser;
  private Vector<Suggestion> suggestion;
  private boolean successOnly;
  private static final Pattern SPLIT = Pattern.compile ("(''|[\"\\'.])?-+");
}

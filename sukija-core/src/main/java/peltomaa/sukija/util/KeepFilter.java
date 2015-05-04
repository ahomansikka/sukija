/*
Copyright (©) 2015 Hannu Väisänen

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


package peltomaa.sukija.util;


import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.WordlistLoader;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.suggestion.Suggestion;


public class KeepFilter extends SukijaFilter {
  public KeepFilter (TokenStream in, Morphology morphology, CharArraySet wordSet, Vector<Suggestion> suggestion)
  {
    super (in);
    this.morphology = morphology;
    this.wordSet = wordSet;
    this.suggestion = suggestion;
  }


  protected void filter (String word)
  {
    resultSet.clear();
    tmp.clear();

    if (morphology.analyzeLowerCase (word, tmp)) {
      for (String s : tmp) {
        if (wordSet.contains (s)) {
          resultSet.add (s);
        }
      }
      iterator = resultSet.iterator();
    }
    else {
      final Set<String> suggestionSet = Suggestion.trySuggestions(suggestion,word);
      iterator = (suggestionSet == null) ? null : suggestionSet.iterator();
    }
  }


  private Morphology morphology;
  private CharArraySet wordSet;
  private Vector<Suggestion> suggestion;
  private Set<String> tmp = new TreeSet<String>();
  private Set<String> resultSet = new TreeSet<String>();
}

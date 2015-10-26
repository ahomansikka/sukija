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


package peltomaa.sukija.suggestion;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import org.apache.lucene.analysis.util.CharArraySet;
import org.puimula.libvoikko.*;
import peltomaa.sukija.util.CharCombinator;
import peltomaa.sukija.voikko.VoikkoUtils;


public final class SuggestionUtils {
  private SuggestionUtils() {}


  public static final Collection<String> getSuggestions (Vector<Suggestion> suggestion, String word)
  {
    for (int i = 0; i < suggestion.size(); i++) {
      if (suggestion.get(i).suggest (word)) {
        return suggestion.get(i).getResult();
      }
    }
    return null;
  }


  public static final boolean analyze (String word, Collection<String> result,
                                       Vector<Suggestion> suggestion, String from, String to)
  {
    result.clear();

    Collection<String> suggestionResult = getSuggestions (suggestion, word);

    if (suggestionResult == null) {
      suggestionResult = getSuggestions (word, suggestion, from, to); 
    }

    if (suggestionResult != null) {
      result.addAll (suggestionResult);
      return true;
    }
    return false;
  }


  private static final Collection<String> getSuggestions
    (String word, Vector<Suggestion> suggestion, String from, String to)
  {
    CharCombinator charCombinator = new CharCombinator (word, from, to);
    Iterator<String> iterator = charCombinator.iterator();
//System.out.println ("SuggestionUtils " + charCombinator.size() + " " + word);

    while (iterator.hasNext()) {
      final String s = iterator.next();
//System.out.println ("SuggestionUtils " + s);
      Collection<String> suggestionResult = getSuggestions (suggestion, s);
      if (suggestionResult != null) {
//System.out.println ("SuggestionUtils Huu");
        return suggestionResult;
      }
    }
//System.out.println ("SuggestionUtils Haa");
    return null;
  }
}

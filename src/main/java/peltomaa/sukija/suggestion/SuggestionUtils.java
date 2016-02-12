/*
Copyright (©) 2015-2016 Hannu Väisänen

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


  public static final Collection<String> getSuggestions (Suggestion[] suggestion, String word)
  {
    for (int i = 0; i < suggestion.length; i++) {
      if (suggestion[i].suggest (word)) {
        return suggestion[i].getResult();
      }
    }
    return null;
  }


  public static final boolean analyze (String word, Collection<String> result,
                                       Suggestion[] suggestion, String from, String to)
  {
    result.clear();

    Collection<String> suggestionResult = getSuggestions (suggestion, word);

    if (suggestionResult == null) {
      suggestionResult = getSuggestions (suggestion, word, from, to); 
    }

    if (suggestionResult != null) {
      result.addAll (suggestionResult);
      return true;
    }
    else {
      result.add (word);
      return false;
    }
  }


  public static final Suggestion[] removeSuggestions (Suggestion[] suggestion, String className)
  {
    Vector<Suggestion> v = new Vector<Suggestion>();
    for (int i = 0; i < suggestion.length; i++) {
      if (suggestion[i].getClass().getName().indexOf(className) == -1) {
        v.add (suggestion[i]);
      }
    }
    return v.toArray (new Suggestion[0]);
  }


  private static final Collection<String> getSuggestions
    (Suggestion[] suggestion, String word, String from, String to)
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

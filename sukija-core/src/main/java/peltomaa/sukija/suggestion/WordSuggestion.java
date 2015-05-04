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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.WordlistLoader;
import peltomaa.sukija.morphology.Morphology;


/**
 * Hyväksytään vain ne sanat, jotka ovat muuttujassa 'words'.
 */
public class WordSuggestion extends Suggestion {
  public WordSuggestion (Morphology morphology, Collection<String> words)
  {
    super (morphology);
    this.words = new CharArraySet (words, false);
  }


  public WordSuggestion (Morphology morphology, String[] words)
  {
    this (morphology, Arrays.asList (words));
  }


  public WordSuggestion (Morphology morphology, String fileName) throws FileNotFoundException, IOException
  {
    super (morphology);
    words = WordlistLoader.getWordSet (new FileReader (fileName));
  }


  public boolean suggest (String word)
  {
    reset();
    sb.append (word);
    if (analyse()) {
System.out.println ("Ding Dong " + word);
      if (result.size() == 1 && words.contains(word)) return true;
    }
    return false;
  }


  private CharArraySet words;
}

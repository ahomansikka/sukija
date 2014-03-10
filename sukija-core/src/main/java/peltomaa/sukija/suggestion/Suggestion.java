/*
Copyright (©) 2009-2014 Hannu Väisänen

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

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.morphology.MorphologyException;

/**
Suggest a correct spelling for misspelled word.<p>

This is an abstract class and you should extend this class and provide
an implementation for function {@link #suggest(String)}.<p>

Function {@link #suggest(String)} should correct the spelling of
the word and then try to convert the corrected word to a base form
with function {@link #analyse()} or {@link #analyse(Set)}.<p>

If those functions return {@code true} {@link #suggest(String)}
should return {@code true}, otherwise it should return {@code false}.
*/
public abstract class Suggestion {
  public Suggestion (Morphology morphology)
  {
    this.morphology = morphology;
  }


  /**
   * A function that should return {@code true} if spelling correction is
   * succesful, otherwise it should return {@code false}.
   *
   * @param word  A word whose spelling is to be corrected.
   */
  public abstract boolean suggest (String word);


  /** An instance of Morphology.
   */
  protected Morphology morphology;


  /** Base form(s) of word whose spelling has been corrected.
   */
  protected Set<String> result = new TreeSet<String>();


  /** Character buffer for spelling corrections.
   */
  protected StringBuffer sb = new StringBuffer (200); // We allocate so much space that we
                                                      // should never need to reallocate.


  /** True if morphology recognises the word whose spelling has been corrected.
   */
  protected boolean found = false;


  /** If {@link #suggest(String)} returns {@code true}
   *  this function returns the base form(s) of the {@code word}
   *  {@link #suggest(String)} has corrected.
   */
  public Set<String> getResult() {return result;}


  /** If {@link #suggest(String)} returns {@code true}
   *  this function returns an iterator to the base form(s) of the {@code word}
   *  {@link #suggest(String)} has corrected. This function is equal
   *  to call {@code getResult().iterator()}.
   */
  public Iterator<String> iterator() {return result.iterator();}


  /** Set internal variables to initial values. You should call this
   *  function at the beginning of function {@link #suggest(String)}.
   */
  protected void reset()
  {
    sb.delete (0, sb.length());
    found = false;
  }


  /** Analyse a word after its spelling has been corrected.
   */
  protected boolean analyse()
  {
    String s = sb.toString();
    result.clear();

    try {
      if (morphology.analyzeLowerCase (s, result)) {
        return true;
      }
    }
    catch (MorphologyException e)
    {
      LOG.error (e.getMessage());
    }
    catch (NullPointerException e)
    {
      if (morphology == null)
        LOG.error ("morphology == null");
      throw e;
    }
    return false;
  }


  /** Analyse a set of words.
   *
   * @param words  Words to be analysed.
   */
  protected boolean analyse (Set<String> words)
  {
    try {
      result.clear();

      Iterator<String> i = words.iterator();
      Set<String> tmp = new TreeSet<String>();

      while (i.hasNext()) {
        if (morphology.analyzeLowerCase (i.next(), tmp)) {
          result.addAll (tmp);
        }
        tmp.clear();
      }
      return (result.size() > 0);
    }
    catch (MorphologyException e)
    {
      LOG.error (e.getMessage());
    }
    return false;
  }


  protected boolean analyse (String word, Set<String> result)
  {
    try {
      if (morphology.analyzeLowerCase (word, result)) {
        return true;
      }
    }
    catch (MorphologyException e)
    {
      LOG.error (e.getMessage());
    }
    return false;
  }


  protected static final Logger LOG = LoggerFactory.getLogger (Suggestion.class);
}

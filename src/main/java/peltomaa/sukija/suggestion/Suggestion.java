/*
Copyright (©) 2009-2015 Hannu Väisänen

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
import java.util.Set;
import java.util.TreeSet;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.puimula.libvoikko.Voikko;


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
  public Suggestion (Voikko voikko)
  {
    this.voikko = voikko;
  }


  /**
   * A function that should return {@code true} if spelling correction is
   * succesful, otherwise it should return {@code false}.
   *
   * @param word  A word whose spelling is to be corrected.
   */
  public abstract boolean suggest (String word);


  public Collection<String> getResult() {return result;}

  protected Voikko voikko;
  protected Set<String> result = new TreeSet<String>();
}

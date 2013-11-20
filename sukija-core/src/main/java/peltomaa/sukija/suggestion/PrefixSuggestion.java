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

import peltomaa.sukija.morphology.Morphology;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;


/**
 * Try to recognise a word without a prefix. If succesful, return
 * prefix + recognised word.
 *
 * For example, if prefix is "aasian" and word is "aasianleijonaa",
 * try to recognise "leijonaa". If succesful, return "aasianleijona".
 */
public class PrefixSuggestion extends Suggestion {
  public PrefixSuggestion (Morphology morphology, String prefix)
  {
    super (morphology);
    this.prefix = new Vector<String>();
    this.prefix.add (prefix);
  }


  public PrefixSuggestion (Morphology morphology, Vector<String> prefix)
  {
    super (morphology);
    this.prefix = prefix;
// for (String p : this.prefix) System.out.println ("Huu " + p + ".");
  }


  public boolean suggest (String word)
  {
    reset();

    for (String p : prefix) {
      if (word.startsWith (p)) {
        set.clear();
        if (analyse (word.substring (p.length()), set)) {
          result.clear();
          for (String s: set) {
            result.add (p + s);
          }
          return true;
        }
      }
    }
    return false;
  }

  private Vector<String> prefix;
  private Set<String> set = new TreeSet<String>();
}

/*
Copyright (©) 2009-2011, 2013-2016 Hannu Väisänen

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

import java.util.Iterator;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.util.CharCombinator;
import peltomaa.sukija.attributes.VoikkoAttribute;


/**
 * Replace combinations of characters.
 */
public class CharSuggestion extends Suggestion {
  public CharSuggestion (Voikko voikko, String from, String to)
  {
    super (voikko);
    if (from.length() != to.length()) {
      throw new IllegalArgumentException ("from.length() != to.length()");
    }
    this.from = from;
    this.to = to;
//System.out.println ("\nCharSuggestion " + this.from + " " + this.to);
  }


  @Override
  public boolean suggest (String word, VoikkoAttribute voikkoAtt)
  {
    charCombinator = new CharCombinator (word, from, to);
//System.out.println ("\nCharSuggestion " + word + " " + from + " " + to);

    Iterator<String> i = charCombinator.iterator();

    while (i.hasNext()) {
      final String s = i.next();
//System.out.println ("\nCharSuggestion " + word + " " + s);
      if (analyze (s, voikkoAtt)) {
        return true;  // Muutettu sana tunnistettiin.
      }
    }
    return false; // Muutettua sanaa ei tunnistetttu.
  }


  private String from;
  private String to;
  private CharCombinator charCombinator;
}

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

import java.util.HashSet;
import java.util.Set;
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.attributes.VoikkoAttribute;


public class ApostropheSuggestion extends Suggestion {
  /**
   * Muodostin
   *
   * @param voikko  Voikko.
   */
  public ApostropheSuggestion (Voikko voikko)
  {
    super (voikko);
    ch = '\'';
  }


  /**
   * Muodostin
   *
   * @param voikko  Voikko.
   * @param ch      Merkki, jota käytetään erottimena heittomerkin asemesta.
   */
  public ApostropheSuggestion (Voikko voikko, char ch)
  {
    super (voikko);
    this.ch = ch;
  }


  /**
  Delete apostrophe. If Voikko recognizes the word, return true,
  otherwise delete the end of the word starting from the apostrophe an
  set the start of the word as a base form of the word and return
  true.<p>

  If word has no apostrophe, return false.<p>

  For example, if word is {@code Bordeaux'iin} first try {@code
  Bordeauxiin} and if Voikko does not recognize that, set the base form
  to {@code Bordeaux} and return true.
  */
  @Override
  public boolean suggest (String word, VoikkoAttribute voikkoAtt)
  {
    final int n = word.indexOf (ch);
    if (n == -1) return false;

    final String START = word.substring(0,n);

    sb.delete (0, sb.length());
    sb.append (START).append (word.substring (n+1, word.length()));

    if (analyze (sb.toString(), voikkoAtt)) {
      return true;
    }
    else if (analyze (START, voikkoAtt)) {
      return true;
    }
    else {
      extraBaseForms.clear();
      extraBaseForms.add (START);
      return true;
    }
  }


  @Override
  public Set<String> getExtraBaseForms()
  {
    return extraBaseForms;
  }


  private final char ch;
  private StringBuilder sb = new StringBuilder (500);
  private HashSet<String> extraBaseForms = new HashSet<String>();
}

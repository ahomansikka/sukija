/*
Copyright (©) 2009-2011, 2013-2015 Hannu Väisänen

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

import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.voikko.VoikkoUtils;


public class ApostropheSuggestion extends Suggestion {
  /**
   * Constructor.
   *
   * @param voikko  An instance Of voikko.
   */
  public ApostropheSuggestion (Voikko voikko)
  {
    super (voikko);
    ch = '\'';
  }


  /**
   * Constructor.
   *
   * @param voikko  An instance Of voikko.
   * @param ch      Character to be used instead of apostrophe.
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
  public boolean suggest (String word)
  {
    final int n = word.indexOf (ch);
    if (n == -1) return false;

    result.clear();

    sb.delete (0, sb.length());
    sb.append (word.substring (0, n)).append (word.substring (n+1, word.length()));

    if (VoikkoUtils.analyze (voikko, word, result)) return true;

    result.clear();
    result.add (word.substring(0,n).toString().toLowerCase());
    return true;
  }


  private final char ch;
  private StringBuilder sb = new StringBuilder (500);
}

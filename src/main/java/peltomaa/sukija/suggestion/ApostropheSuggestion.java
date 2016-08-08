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
  Poistetaan heittomerkki. Jos Voikko tunnistaa sanan, palatetaan {@code true},
  muuten poistetaan sanan loppuosa, joka alkaa heittomerkistä. Jos Voikko
  tunnistaa sanan alkuosan, palatetaan {@code true}. Jos Voikko ei tunista
  sanan alkuosaa, asetetaan se sanan perusmuodoksi ja palautetaan {@true}.<p>

  Esimerkiksi jos sana on {@code centime'in}, yritetään tunnistaa
  ensin {@code centimein}. Jos Voikko ei tunnista sitä, yritetään
  tunnistaa {@centime}. Jos Voikko ei tunnista sitä, asetetaan se
  sanan perusmuodoksi ja palautetaan {@true}.<p>

  Huomaa, että sanan alkuosan perusmuoto voi olla erilainen kuin sanan
  alkuosa. Esimerkiksi merkkijonon {@code huomioksi'xxxx} alkuosa on
  {@code huomioksi}, jonka perusmuoto on {@code huomio.}<p>

  Jos sanassa ei ole heittomerkkiä, palautetaan {@false}.<p>
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

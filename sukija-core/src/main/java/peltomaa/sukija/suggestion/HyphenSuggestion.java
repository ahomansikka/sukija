/*
Copyright (©) 2014 Hannu Väisänen

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

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.TreeSet;
import peltomaa.sukija.morphology.Morphology;


/**
Poistetaan sanasta tavuviiva. Jos sana tunnistetaan, palautetaan
sana, muuten jaetaan sana tavuviivojen kohdalta ja yritetään tunnistaa
jokainen sanan osa erikseen.<p>

Esimerkiksi merkkijono "xyz-sanat" yritetään tunnistaa ensin
muodossa "xyzsanat", sitten yritetään tunnistaa "xyz" ja "sanat".
Jos addUnrecognizedWordPartsToResult on true eikä merkkijonoa "xyz" tunnisteta,
se lisätään tulokseen, muuten palautetaan vain "sana" merkkijonon "xyz-sanat"
perusmuotona.
*/
public class HyphenSuggestion extends Suggestion {
  /**
   * Muodostin.
   *
   * @param morphology Morfologialuokka
   * @param addUnrecognizedWordPartsToResult
   *        Lisätäänkö tunnistamattomat sanan osat tuloksiin?
   */
  public HyphenSuggestion (Morphology morphology, boolean addUnrecognizedWordPartsToResult)
  {
    super (morphology);
    this.addUnrecognizedWordPartsToResult = addUnrecognizedWordPartsToResult;
  }


  public boolean suggest (String word)
  {
    String[] s = PATTERN.split (word);
    if (s.length == 1) return false;
    reset();
    sb.append (join(s));
    if (analyse()) {
      return true;
    }
    else {
      return analyse (new TreeSet<String> (Arrays.asList(s)), addUnrecognizedWordPartsToResult);
    }
  }


  private String join (String[] s)
  {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < s.length; i++) {
      sb.append (s[i]);
    }
    return sb.toString();
  }


  private boolean addUnrecognizedWordPartsToResult;
  private static final Pattern PATTERN = Pattern.compile ("-+|[.]-|\"-+");
}

/*
Copyright (©) 2009-2016 Hannu Väisänen

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

import java.util.List;
import java.util.Set;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.voikko.VoikkoUtils;


/**
Suggest a correct spelling for misspelled word.<p>

This is an abstract class and you should extend this class and provide
an implementation for function {@link #suggest(String,VoikkoAttribute)}.<p>

Function {@link #suggest(String,VoikkoAttribute)} should correct the spelling of
the word and then try to convert the corrected word to a base form.<p>

If those functions return {@code true} {@link #suggest(String,VoikkoAttribute)}
should return {@code true}, otherwise it should return {@code false}.
*/
public abstract class Suggestion {
  protected Voikko voikko;


  public Suggestion (Voikko voikko)
  {
    this.voikko = voikko;
  }


  /**
   * Funktio joka palauttaa arvon {@code true}, jos Voikko tunnistaa
   * korjatun sanan, muuten palauttaa arvon {@code false}.
   *
   * @param word  Sana, jonka oikeinkirjoitus yritetään korjata.
   */
  public abstract boolean suggest (String word, VoikkoAttribute voikkoAtt);


  /**
   * Jos funktio {@code success} tekee perusmuotoja muuten kuin Voikon
   * kautta, tämä funktio palauttaa ne. Jos {@code success} ei tee
   * ylimääräisiä perusmuotoja, tämä funktio palauttaa arvon {@code null}.
   */
  public Set<String> getExtraBaseForms() {return null;}


  protected boolean analyze (String word, VoikkoAttribute voikkoAtt)
  {
//    List<Analysis> analysis = voikko.analyze (word);
    analysis = voikko.analyze (word);
    voikkoAtt.addAnalysis (analysis);
    return (analysis.size() > 0);
  }

  private List<Analysis> analysis;

  public List<Analysis> getAnalysis() {return analysis;}
}

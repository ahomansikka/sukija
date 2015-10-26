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

package peltomaa.sukija.baseform;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.puimula.libvoikko.*;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.util.SukijaFilter;
import peltomaa.sukija.voikko.VoikkoUtils;


/**
 * Erotetaan perusmuodot. Kaikki muut Voikon attribuutit häviävät.
 */
public final class BaseFormFilter extends SukijaFilter {
  public BaseFormFilter (TokenStream input, Voikko voikko, boolean successOnly)
  {
    super (input, voikko);
    this.successOnly = successOnly;
  }


  public BaseFormFilter (TokenStream input, Voikko voikko)
  {
    this (input, voikko, false);
  }


  @Override
  public Iterator<String> filter()
  {
    if (VoikkoUtils.analyze (voikko, word, baseForms)) {
      return baseForms.iterator();
    }
    else if (successOnly) {
      return null;
    }
    else {
      flagsAtt.setFlags (Constants.UNKNOWN);
      return baseForms.iterator();
    }
  }


  private int positionIncrement = 1;
  private Iterator<String> iterator;

  private Set<String> baseForms = new HashSet<String>();
  private final boolean successOnly;
}

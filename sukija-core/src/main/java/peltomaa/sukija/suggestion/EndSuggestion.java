/*
Copyright (©) 2009-2011 Hannu Väisänen

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

import java.util.Vector;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.util.Pair;


public class EndSuggestion extends Suggestion {
  public EndSuggestion (Morphology morphology, Vector<Pair<String,String>> data)
  {
    super (morphology);
    v = data;
  }


  public EndSuggestion (Morphology morphology, String[] array)
  {
    this (morphology, Pair.makePair (array));
  }


  public boolean suggest (CharSequence word)
  {
    final String s = word.toString();

    for (int i = 0; i < v.size(); i++) {
      if (s.endsWith (v.get(i).first)) {
        reset();
        sb.append(s).append(v.get(i).second);
        if (analyse()) return true;
      }
    }
    return false;
  }


  private Vector<Pair<String,String>> v;
}

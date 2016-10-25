/*
Copyright (©) 2016 Hannu Väisänen

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


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.puimula.libvoikko.*;
import peltomaa.sukija.attributes.VoikkoAttribute;


public class VoikkoAttributeSuggestion extends Suggestion {

  public VoikkoAttributeSuggestion (Voikko voikko,
                                    VoikkoAttributeParameter parameter)
  {
    super (voikko);
    this.parameter = parameter;
  }


  @Override
  public boolean suggest (String word, VoikkoAttribute voikkoAtt)
  {
    boolean success = false;
    final int N = parameter.item.length;
    for (int i = 0; i < parameter.item.length; i++) {
//System.out.println ("A " + word);
      if (suggest (i, word, voikkoAtt)) {
        if (parameter.tryAll) {
          success = true;
        }
        else {
          return true;
        }
      }
    }
    return success;
  }


  private boolean suggest (int i, String word, VoikkoAttribute voikkoAtt)
  {
    boolean success = false;
    Matcher m = parameter.item[i].pattern.matcher (word);

    if (m.find()) {
//System.out.println ("B "+ word + " " + m.pattern().toString());
      for (int j = 0; j < parameter.item[i].replacement.length; j++) {
        for (int k = 0; k < parameter.item[i].replacement[j].list.length; k++) {
//System.out.println ("C " + word + " " + m.pattern().toString() + " " + parameter.item[i].replacement[j].list[k]);
        final String u = m.replaceAll (parameter.item[i].replacement[j].list[k]);
//System.out.println ("D " + word + " " + m.pattern().toString() + " " + parameter.item[i].replacement[j].list[k] + " " + u);
          final List<Analysis> analysis = voikko.analyze (u);
          for (Analysis a: analysis) {
            if (ok (i, j, a)) {
              voikkoAtt.addAnalysis (a);
              success = true;
            }
          }
        }
      } 
    }
    return success;
  }


  private boolean ok (int i, int j, Analysis analysis)
  {
    if (parameter.item[i].replacement[j].attribute.length() == 0) {
      return true;
    }

    final String key = analysis.get (parameter.item[i].replacement[j].attribute);
    if (key != null) {
      return parameter.item[i].replacement[j].regex.matcher(key).find();
    }
    return false;
  }


  private final VoikkoAttributeParameter parameter;
}

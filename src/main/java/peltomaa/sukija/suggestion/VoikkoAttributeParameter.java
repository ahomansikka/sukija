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

import java.util.List;
import java.util.regex.Pattern;
import peltomaa.sukija.schema.ReplacementType;
import peltomaa.sukija.schema.VoikkoAttributeInput;
import peltomaa.sukija.util.RegexUtil;


class VoikkoAttributeParameter {
  final Item[] item;
  final boolean tryAll;


  VoikkoAttributeParameter (VoikkoAttributeInput input)
  {
    item = getItems (input);
    tryAll = input.isTryAll();
  }


  static class Item {
    final Pattern pattern;
    final Replacement[] replacement;

    Item (String pattern, Replacement[] replacement)
    {
      this.pattern = RegexUtil.makePattern  (pattern);
      this.replacement = replacement;
    }
  }


  private Item[] getItems (VoikkoAttributeInput input)
  {
    final List<VoikkoAttributeInput.Item> vitem = input.getItem();
    final int N = vitem.size();
    final Item[] item = new Item[N];

    for (int i = 0; i < N; i++) {
      item[i] = new Item (vitem.get(i).getPattern(),
                          getReplacements (vitem.get(i).getReplacement()));
    }
    return item;
  }


  static class Replacement {
    final String[] list;
    final String attribute;
    final Pattern regex;

    Replacement (String[] list, String attribute, String regex)
    {
      this.list = list;
      this.attribute = attribute;
      this.regex = RegexUtil.makePattern (regex);
    }
  }


  private Replacement[] getReplacements (List<ReplacementType> r)
  {
    final int N = r.size();
    final Replacement[] replacement = new Replacement[N];

    for (int i = 0; i < N; i++) {
      replacement[i] = new Replacement (r.get(i).getList().toArray(new String[0]),
                                        r.get(i).getAttribute(),
                                        r.get(i).getRegex());
    }
    return replacement;
  }
}

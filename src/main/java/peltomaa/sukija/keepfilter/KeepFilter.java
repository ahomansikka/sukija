/*
Copyright (©) 2015-2016, 2020-2022 Hannu Väisänen

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


package peltomaa.sukija.keepfilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.Vector;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.puimula.libvoikko.*;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionUtils;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.util.SukijaFilter;
import peltomaa.sukija.suggestion.ahocorasick.AhoCorasickCorrector;


public final class KeepFilter extends SukijaFilter {
  public KeepFilter (TokenStream in, Voikko voikko, CharArraySet wordSet, String from, String to, Suggestion[] suggestion)
  {
    super (in, voikko);
    this.wordSet = wordSet;
    this.from = from;
    this.to = to;
    this.suggestion = suggestion;
//System.out.println ("KeepFilter muodostin");
  }


  @Override
  protected Iterator<String> filter()
  {
    baseForms.clear();
    tmp.clear();

    List<Analysis> list = voikko.analyze (word);
    if (list.size() > 0) {
      if (copyIf (AhoCorasickCorrector.getCorrections (list), baseForms)) {
        return baseForms.iterator();
      }
    }

    return null;
  }


  private boolean copyIf (Set<String> from, Set<String> to)
  {
    to.clear();
    for (String s : from) {
      if (wordSet.contains (s)) {
        to.add (s);
      }
      else {
        final List<Analysis> list = new ArrayList<Analysis>();
        boolean p = SuggestionUtils.getSuggestions (suggestion, s,  list, baseFormAtt, voikkoAtt);
        if (p) {
          to.addAll (baseFormAtt.getBaseForms());
        }
      }
    }
    return (to.size() > 0);
  }


  private CharArraySet wordSet;
  private Suggestion[] suggestion;
  private Set<String> tmp = new HashSet<String>();
  private Set<String> baseForms = new HashSet<String>();
  private String from;
  private String to;
}

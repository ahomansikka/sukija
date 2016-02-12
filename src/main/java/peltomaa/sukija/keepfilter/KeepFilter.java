/*
Copyright (©) 2015-2016 Hannu Väisänen

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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.Vector;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.puimula.libvoikko.*;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionUtils;
import peltomaa.sukija.util.SukijaFilter;
import peltomaa.sukija.voikko.VoikkoUtils;


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

//System.out.println ("KeepFilter0 " + word);

    if (VoikkoUtils.analyze (voikko, word, tmp)) {
//System.out.println ("KeepFilter1 " + word);
      if (VoikkoUtils.copyIf (tmp, wordSet, baseForms, suggestion)) {
//System.out.println ("KeepFilter1 " + word);
        return baseForms.iterator();
      }
    }
    else {
//System.out.println ("KeepFilterA " + word);
      if (VoikkoUtils.analyze (voikko, word, tmp, from, to)) {
//System.out.println ("KeepFilter3 " + word + " " + tmp.toString());
        if (VoikkoUtils.copyIf (tmp, wordSet, baseForms, suggestion)) {
//System.out.println ("KeepFilter4 " + word + " " + baseForms.toString());
          return baseForms.iterator();
        }
//System.out.println ("KeepFilterB " + word);
      }
//System.out.println ("KeepFilter5 " + word);
      if (SuggestionUtils.analyze (word, tmp, suggestion, from, to)) {
//for (String s : tmp) System.out.println ("KeepFilter6 " + word + " " + s);
        baseForms.addAll (tmp);
        return baseForms.iterator();
      }
    }

//System.out.println ("KeepFilter7 " + word);
    return null;
  }


  private CharArraySet wordSet;
  private Suggestion[] suggestion;
  private Set<String> tmp = new HashSet<String>();
  private Set<String> baseForms = new HashSet<String>();
  private String from;
  private String to;
}

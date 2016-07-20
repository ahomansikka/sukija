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
import java.io.Reader;
import java.io.Writer;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.puimula.libvoikko.*;
import peltomaa.sukija.finnish.HVTokenizer;
import peltomaa.sukija.hyphen.HyphenFilter;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.util.SukijaFilter;
import peltomaa.sukija.attributes.BaseFormAttribute;
import peltomaa.sukija.attributes.OriginalWordAttribute;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionFilter;


public class KeepFilterTester {
  private KeepFilterTester() {}

  public static void test (Reader reader, Writer writer, Voikko voikko, CharArraySet wordSet,
                           String from, String to,
                           Suggestion[] suggestion, boolean stopOnSuccess) throws IOException
  {
    Set<String> set = new TreeSet<String>();
    TokenStream t = new HVTokenizer();
    ((Tokenizer)t).setReader (reader);

    t = new KeepFilter (t, voikko, wordSet, from, to, suggestion);

    CharTermAttribute termAtt = t.addAttribute (CharTermAttribute.class);
    BaseFormAttribute baseFormAtt = t.addAttribute (BaseFormAttribute.class);
    FlagsAttribute flagsAtt = t.addAttribute (FlagsAttribute.class);
    OriginalWordAttribute originalWordAtt = t.addAttribute (OriginalWordAttribute.class);

    try {
      t.reset();
      while (t.incrementToken()) {
        writer.write ("Sana: " + originalWordAtt.getOriginalWord()
                      + " " + termAtt.toString()
                      + " " + Constants.toString (flagsAtt)
                      + " " + baseFormAtt.getBaseForms().toString()
                      + "\n");
        writer.flush();
      }
      t.end();
    }
    finally {
      t.close();
    }
  }
}

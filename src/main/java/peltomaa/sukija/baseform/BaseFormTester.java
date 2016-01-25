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
import java.io.Reader;
import java.io.Writer;
import java.util.Vector;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.puimula.libvoikko.*;
import peltomaa.sukija.baseform.BaseFormFilter;
import peltomaa.sukija.finnish.HVTokenizer;
import peltomaa.sukija.hyphen.HyphenFilter;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.attributes.OriginalWordAttribute;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.suggestion.MultiSuggestionFilter;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionFilter;


/**
 * Test.
 */
public class BaseFormTester {
  private BaseFormTester() {}


  public static void test (Reader reader, Writer writer, Voikko voikko, Vector<Suggestion> suggestion,
                           boolean successOnly) throws IOException
  {
    test (reader, writer, voikko, null, null, suggestion, successOnly);
  }


  public static void test (Reader reader, Writer writer, Voikko voikko,
                           String from, String to, Vector<Suggestion> suggestion,
                           boolean successOnly) throws IOException
  {
    TokenStream t = new HVTokenizer();
    ((Tokenizer)t).setReader (reader);
    t = new HyphenFilter (t);
    if (suggestion == null) {
      t = new BaseFormFilter (t, voikko, successOnly);
    }
    else if (from != null) {
      t = new MultiSuggestionFilter (t, voikko, from, to, suggestion, successOnly);
    }
    else {
      t = new SuggestionFilter (t, voikko, suggestion, successOnly);
    }

    CharTermAttribute word = t.addAttribute (CharTermAttribute.class);
    FlagsAttribute flagsAtt = t.addAttribute (FlagsAttribute.class);
    OriginalWordAttribute originalWordAtt = t.addAttribute (OriginalWordAttribute.class);
    VoikkoAttribute voikkoAtt = t.addAttribute (VoikkoAttribute.class);

    try {
      t.reset();
      while (t.incrementToken()) {
        writer.write ("Sana: " + originalWordAtt.getOriginalWord()
                      + " " + word.toString()
                      + " " + Constants.toString (flagsAtt)
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

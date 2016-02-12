/*
Copyright (©) 2013-2014, 2016 Hannu Väisänen

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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenStream;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.finnish.HVTokenizer;
import peltomaa.sukija.voikko.*;
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;


/**
 * Testataan korjausehdotuksia.
 */
public class SuggestionTester {
  private SuggestionTester() {}

  public static void analyze (Reader reader, Writer writer, Voikko voikko, Suggestion[] suggestion, boolean stopOnSuccess) throws IOException
  {
    List<Analysis> analysis = null;
    Set<String> set = new TreeSet<String>();
    TokenStream t = new HVTokenizer();
    ((Tokenizer)t).setReader (reader);

    t = new VoikkoFilter (t, voikko);

    CharTermAttribute charTermAtt = t.addAttribute (CharTermAttribute.class);
//    VoikkoAttribute voikkoAtt = t.addAttribute (VoikkoAttribute.class);

//System.out.println ("VoikkoAttribute " + (voikkoAtt == null));
    try {
      t.reset();
      set.clear();
      while (t.incrementToken()) {
//    VoikkoUtils.printAnalysisResult (voikkoAtt, System.out);
        final String word = charTermAtt.toString();
        writer.write ("Sana: " + word);
        set.clear();
        if (VoikkoUtils.analyze (voikko, word, set)) {
          print (writer, "M", set);
        }
        else if (suggestion != null) {
          for (Suggestion s : suggestion) {
            if (s.suggest(word)) {
              print (writer, "S", s.getResult());
              if (stopOnSuccess) {
                break;
              }
            }
          }
        }
//        voikkoAtt.setAnalysis (analysis);
        writer.write ("\n");
        writer.flush();
//        VoikkoUtils.printAnalysisResult (voikkoAtt, System.out);
      }
      t.end();
    }
    finally {
      t.close();
    }
  }


  private static void print (Writer writer, String prefix, Collection<String> c) throws IOException
  {
    writer.write (" " + prefix);
    for (String s: c) {
      writer.write (" " + s);
    }
  }
}

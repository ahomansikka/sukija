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

package peltomaa.sukija.baseform;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttributeImpl;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.puimula.libvoikko.*;
import peltomaa.sukija.finnish.HVTokenizer;
import peltomaa.sukija.hyphen.HyphenFilter;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.attributes.BaseFormAttribute;
import peltomaa.sukija.attributes.OriginalWordAttribute;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionFilter;


/**
 * Test.
 */
public class BaseFormTester {
  private BaseFormTester() {}


  public static void test (Reader reader, Writer writer, Voikko voikko,
                           boolean successOnly) throws IOException
  {
    TokenStream t = new HVTokenizer();
    ((Tokenizer)t).setReader (reader);

    t = new BaseFormFilter (t, voikko, successOnly);

    CharTermAttribute termAtt = t.addAttribute (CharTermAttribute.class);
    BaseFormAttribute baseFormAtt = t.addAttribute (BaseFormAttribute.class);
    FlagsAttribute flagsAtt = t.addAttribute (FlagsAttribute.class);
    OriginalWordAttribute originalWordAtt = t.addAttribute (OriginalWordAttribute.class);

String orig = "";
TreeSet<String> tset = new TreeSet<String>();
FlagsAttribute flagsA = new FlagsAttributeImpl();

    try {
      t.reset();
      while (t.incrementToken()) {
        if (!orig.equals("") && !orig.equals(originalWordAtt.getOriginalWord())) {
          writer.write ("Sana: " + orig);
          if (Constants.hasFlag (flagsA, Constants.FOUND)) {
            writer.write (" M " + toString(tset));
          }
          writer.write ("\n");
          writer.flush();
          tset.clear();
        }
        orig = originalWordAtt.getOriginalWord();
        tset.addAll (baseFormAtt.getBaseForms());
        flagsA.setFlags (flagsAtt.getFlags());
      }
      
      writer.write ("Sana: " + orig);
      if (Constants.hasFlag (flagsA, Constants.FOUND)) {
        writer.write (" M " + toString(tset));
      }
      writer.write ("\n");
      writer.flush();
      t.end();
    }
    finally {
      t.close();
    }

/*
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
*/
  }


  private static final String toString (Set<String> set)
  {
    final String s = set.toString().replaceAll (",", "");
    return s.substring (1, s.length()-1);
  }
}

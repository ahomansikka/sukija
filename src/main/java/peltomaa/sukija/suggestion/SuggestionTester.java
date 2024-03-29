/*
Copyright (©) 2013-2014, 2016-2018, 2020-2022 Hannu Väisänen

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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.miscellaneous.RemoveDuplicatesTokenFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterIterator;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.TokenStream;
import peltomaa.sukija.attributes.BaseFormAttribute;
import peltomaa.sukija.attributes.OriginalWordAttribute;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.finnish.HVTokenizer;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.filters.*;
import peltomaa.sukija.filters.ahocorasick.*;
import peltomaa.sukija.finnish.FinnishFoldingLowerCaseFilter;
import peltomaa.sukija.voikko.*;
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;


/**
 * Testataan korjausehdotuksia.
 */
public class SuggestionTester {
  private SuggestionTester() {}

  public static void analyze (Reader reader, Writer writer, Voikko voikko,
                              String suggestionFile, boolean stopOnSuccess,
                              boolean printFlags, TokenStream t) throws IOException
  {
    List<Analysis> analysis = null;
    ((Tokenizer)t).setReader (reader);

    t = new VoikkoFilter (t, voikko);

//    t = new HVTokenFilter (t);

//    t = new FinnishFoldingLowerCaseFilter (t);
    t = new LaTeXFilter (t);

//    t = new HVCompoundWordFilter (t);
//    t = new FinnishCompoundWordTokenFilter (t);

/*
    if (useHyphenFilter) {
      t = new HyphenFilter (t);
    }

    int configurationFlags = WordDelimiterGraphFilter.CATENATE_ALL +
                             WordDelimiterGraphFilter.CATENATE_WORDS +
                             WordDelimiterGraphFilter.GENERATE_WORD_PARTS +
                             WordDelimiterGraphFilter.PRESERVE_ORIGINAL;
*/
//    t = new WordDelimiterFilter (t, configurationFlags, null);

//    t = new WordDelimiterGraphFilter (t, configurationFlags, null);
//    t = new FlattenGraphFilter (t);

//    Antavat saman tuloksen kuin edellinen
//    t = new WordDelimiterGraphFilter (t, true, WordDelimiterIterator.DEFAULT_WORD_DELIM_TABLE, configurationFlags, null);
//    t = new RemoveDuplicatesTokenFilter (t);

    t = new SuggestionFilter (t, voikko, suggestionFile, false);

    CharTermAttribute termAtt = t.addAttribute (CharTermAttribute.class);
    BaseFormAttribute baseFormAtt = t.addAttribute (BaseFormAttribute.class);
    FlagsAttribute flagsAtt = t.addAttribute (FlagsAttribute.class);
    OriginalWordAttribute originalWordAtt = t.addAttribute (OriginalWordAttribute.class);
    OffsetAttribute offsetAtt = t.addAttribute (OffsetAttribute.class);
    PositionIncrementAttribute positionIncrAtt = t.addAttribute (PositionIncrementAttribute.class);

    try {
      t.reset();
      while (t.incrementToken()) {
        writer.write ("Sana: " + originalWordAtt.getOriginalWord() + " | " + termAtt.toString());
        if (printFlags) {
          writer.write (" | " + Constants.toString(flagsAtt));
        }
        writer.write ("\n");
        writer.flush();
      }
      t.end();
    }
    finally {
      t.close();
    }
  }


  private static final String toString (Set<String> set)
  {
    final String s = set.toString().replaceAll (",", "");
    return s.substring (1, s.length()-1);
  }
}

/*
Copyright (©) 2020 Hannu Väisänen

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


package peltomaa.sukija;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.puimula.libvoikko.*;
import peltomaa.sukija.finnish.HVTokenizer;
import peltomaa.sukija.voikko.VoikkoFilter;
import peltomaa.sukija.voikko.VoikkoUtils;
import peltomaa.sukija.attributes.VoikkoAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


public class SukijaTest
{
  @BeforeEach
  public void setUp()
  {
// Tätä ei näköjään suoriteta ollenkaan??
    System.out.println ("setUp");
    voikko = VoikkoUtils.getVoikko ("fi", VOIKKO_PATH, LIBRARY_PATH, LIBVOIKKO);
    assertTrue (voikko != null);
  }


  @Test
  public void testSukija()
  {
    try {
      voikko = VoikkoUtils.getVoikko ("fi", VOIKKO_PATH, LIBRARY_PATH, LIBVOIKKO);
      assertTrue (voikko != null);
      assertTrue (test ("alusta", ""));
      assertTrue (test ("huuhaahoo", ""));
      assertTrue (test ("dioksidiin", ""));
      assertTrue (test ("honka-mänty-petäjä", ""));
      assertTrue (test ("menisimmekö", ""));
      assertTrue (test ("kauniisti", ""));
    }
    catch (Throwable t)
    {
      t.printStackTrace (System.out);
    }
  }


  private boolean test (String input, String expectedOutput) throws IOException
  {
    Reader r = new StringReader (input);
    TokenStream t = new HVTokenizer();
    ((Tokenizer)t).setReader (r);
    t = new VoikkoFilter (t, voikko);
    t.reset();
    VoikkoAttribute sukijaAtt = t.addAttribute (VoikkoAttribute.class);
    CharTermAttribute termAtt = t.addAttribute (CharTermAttribute.class);

    while (t.incrementToken()) {
      System.out.println ("sukijaTest " + termAtt.toString());
      for (int i = 0; i < sukijaAtt.getAnalysis().size(); i++) {
        System.out.println (sukijaAtt.getAnalysis(i).get("BASEFORM"));
//        VoikkoUtils.printAnalysisResult (sukijaAtt.getAnalysis(i), System.out);
      }
      System.out.println ("");
    }

    return true;
  }

  private Voikko voikko;
  private static final String VOIKKO_PATH = "/usr/local/lib/voikko";
  private static final String LIBRARY_PATH = "/usr/local/lib";
  private static final String LIBVOIKKO = "/usr/local/lib/libvoikko.so";
}

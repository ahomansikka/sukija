/*
Copyright (©) 2013 Hannu Väisänen

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


package peltomaa.sukija.voikko;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import peltomaa.sukija.finnish.HVTokenizer;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.suggestion.*;

/**
 * Unit test for spelling suggestions.
 */
public class SuggestionTest extends TestCase {
  /**
   * Create the test case.
   *
   * @param testName name of the test case
   */
  public SuggestionTest (String testName)
  {
    super (testName);
  }


  /**
   * @return The suite of tests being tested.
   */
  public static Test suite()
  {
    return new TestSuite (SuggestionTest.class);
  }


  public void testSuggestion()
  {
    try {
      morphology = VoikkoMorphology.getInstance ("fi");

      Suggestion prefixSuggestion1 = new PrefixSuggestion (morphology, "aamu");
      Suggestion prefixSuggestion2 = new PrefixSuggestion (morphology, "aasian");
      Suggestion prefixSuggestion3 = new PrefixSuggestion (morphology, "aito");
      Suggestion prefixSuggestion4 = new PrefixSuggestion (morphology, "amerikan");

      assertTrue (test (morphology, "aamuäreälle",         "aamuäreä",        prefixSuggestion1));
      assertTrue (test (morphology, "aasianleijonille",    "aasianleijona",   prefixSuggestion2));
      assertTrue (test (morphology, "aitomajavat",         "aitomajava",      prefixSuggestion3));
      assertTrue (test (morphology, "amerikanbiisoneille", "amerikanbiisoni", prefixSuggestion4));

      Suggestion compoundSuggestion = new CompoundWordRegexSuggestion (morphology, "l[aä]i(s[eit]|nen)");

      assertTrue (test (morphology, "aatelilaiset",     "aatelilainen",    compoundSuggestion));
      assertTrue (test (morphology, "vuonolaisien",     "vuonolainen",     compoundSuggestion));
      assertTrue (test (morphology, "faniklubilaisten", "faniklubilainen", compoundSuggestion));
      assertTrue (test (morphology, "vuonolainenkin",   "vuonolainen",     compoundSuggestion));
//      assertTrue (test (morphology, "ikäväläinen",      "ikäväläinen",     compoundSuggestion));
    }
    catch (IOException t)
    {
      if (t.getMessage() != null) System.out.println (t.getMessage());
//      t.printStackTrace (System.out);
    }
  }


  boolean test (Morphology morphology, String input, String expectedOutput, Suggestion suggestion) throws IOException
  {
    final String result = analyze (morphology, input, suggestion);
    System.out.println ("input " + input + " result " + result + " " + expectedOutput);
    return (result.equals (expectedOutput));
  }


  private String analyze (Morphology morphology, String input, Suggestion suggestion) throws IOException
  {
    Set<String> set = new TreeSet<String>();

    Reader r = new StringReader (input);
    Tokenizer t = new HVTokenizer (r);
    CharTermAttribute word = t.addAttribute (CharTermAttribute.class);

    try {
      t.reset();
      while (t.incrementToken()) {
        final String w = word.toString();
        System.out.println ("Sana: " + w);
        set.clear();
        if (morphology.analyzeLowerCase (w, set)) {
          print ("m", set);
        }
        else if (suggestion.suggest (w)) {
          Set<String> result = suggestion.getResult();
//          print ("s", result);
          if (result.size() == 1) {
            return result.iterator().next();
          }
        }
        else {
          return null;
        }
      }
      t.end();
    }
    finally {
      t.close();
    }

    return null;
  }


  private void print (String prefix, Collection<String> c)
  {
    Iterator i = c.iterator();
    while (i.hasNext()) {
      System.out.println ("   " + prefix + " " + i.next());
    }
  }


  private String getMalagaProjectFile()
  {
    return String.format ("%s/.sukija/suomi.pro", System.getProperty ("user.home"));
  }

  private Morphology morphology;
}

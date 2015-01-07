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

import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
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
      final String HOME = System.getProperty ("user.home");
      Morphology morphology = VoikkoMorphology.getInstance ("fi",
                                                            HOME + "/vvfst/voikkodict",
                                                            HOME + "/vvfst/lib/libvoikko.so",
                                                            HOME + "/vvfst/lib");
      assertTrue (morphology != null);

      SuggestionTester tester = new SuggestionTester (morphology);

      for (int i = 0; i < SuggestionTester.data.size(); i++) {
        assertTrue (tester.test (SuggestionTester.data.get(i)));
      }

      Suggestion compoundSuggestion = new CompoundWordRegexSuggestion (morphology, "l[aä]i(s[eit]|nen)");

      assertTrue (tester.test ("aatelilaiset",     "aatelilainen",    compoundSuggestion));
      assertTrue (tester.test ("vuonolaisien",     "vuonolainen",     compoundSuggestion));
      assertTrue (tester.test ("faniklubilaisten", "faniklubilainen", compoundSuggestion));
      assertTrue (tester.test ("vuononlainenkin",   "vuononlainen",     compoundSuggestion));
//      assertTrue (tester.test ("ikäväläinen",      "ikäväläinen",     compoundSuggestion));
    }
    catch (IOException t)
    {
      if (t.getMessage() != null) System.out.println (t.getMessage());
//      t.printStackTrace (System.out);
    }
  }
}

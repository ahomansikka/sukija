/*
Copyright (©) 2013-2014 Hannu Väisänen

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


package peltomaa.sukija.malaga;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Vector;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.suggestion.*;


public class SuggestionParserTest extends TestCase {
  /**
   * Create the test case.
   *
   * @param testName name of the test case
   */
  public SuggestionParserTest (String testName)
  {
    super (testName);
  }


  /**
   * @return The suite of tests being tested.
   */
  public static Test suite()
  {
    return new TestSuite (SuggestionParserTest.class);
  }


  public void testSuggestionParser()
  {
    try {
      Morphology morphology = MalagaMorphology.getInstance (getMalagaProjectFile());

      SuggestionParser sp = new SuggestionParser (morphology, System.getProperty("user.home") + "/.sukija/suggestion.txt");
      Vector<Suggestion> v = sp.getSuggestions();

      for (Suggestion s: v) {
        assertTrue (s.suggest ("qwertyuiop") == false);
      }
    }
    catch (SuggestionParser.SuggestionParserException t)
    {
      if (t.getMessage() != null) System.out.println (t.getMessage());
//      t.printStackTrace (System.out);
    }
  }

  private String getMalagaProjectFile()
  {
    return String.format ("%s/.sukija/suomi.pro", System.getProperty ("user.home"));
  }
}

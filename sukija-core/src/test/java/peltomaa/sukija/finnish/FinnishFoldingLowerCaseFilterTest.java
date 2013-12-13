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


package peltomaa.sukija.finnish;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


/**
 * Unit test for FinnishFoldingLowerCaseFilter
 */
public class FinnishFoldingLowerCaseFilterTest extends TestCase {
  /**
   * Create the test case.
   *
   * @param testName name of the test case
   */
  public FinnishFoldingLowerCaseFilterTest (String testName)
  {
    super (testName);
  }

  /**
   * @return The suite of tests being tested.
   */
  public static Test suite()
  {
    return new TestSuite (FinnishFoldingLowerCaseFilterTest.class);
  }

  public void testFinnishFoldingLowerCaseFilter()
  {
    try {
      assertTrue (test ("Alopæus", "alopaeus"));
      assertTrue (test ("Straße", "strasse"));
      assertTrue (test ("Waltari", "valtari"));
    }
    catch (IOException e)
    {
      System.out.println (e.getMessage());
    }
  }


  private boolean test (String input, String expectedOutput) throws IOException
  {
    Reader r = new StringReader (input);
    Tokenizer t = new HVTokenizer (r);
    FinnishFoldingLowerCaseFilter filter = new FinnishFoldingLowerCaseFilter (t);
    CharTermAttribute word = filter.addAttribute (CharTermAttribute.class);

    try {
      t.reset();
      while (filter.incrementToken()) {
        System.out.println (input + " " + word + " " + expectedOutput);
        return (word.toString().compareTo (expectedOutput) == 0);
      }
    }
    finally {
      t.close();
    }
    return false;
  }
}

/*
Copyright (©) 2017-2018 Hannu Väisänen

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

package peltomaa.sukija.suggestion.distance;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.TokenStream;

import org.puimula.libvoikko.*;
import peltomaa.sukija.voikko.VoikkoUtils;


public class MapMaker {
  public static final void main (String[] args)
  {
    try {
      MapMaker object = new MapMaker();
      object.read (args);
      object.write ("distance.txt.gz");
    }
    catch (Throwable t)
    {
      t.printStackTrace (System.out);
    }
  }


  private void read (String[] args) throws FileNotFoundException, IOException
  {
    for (String file : args) {
      read (new FileReader (file));
    }
    for (String word : set) {
//      System.out.println (word);
      List<Analysis> analysis = voikko.analyze (word);
      for (Analysis a : analysis) {
//        System.out.println (a.get("BASEFORM").toLowerCase() + " " + word);
        stringMap.putValue (a.get("BASEFORM").toLowerCase(), word);
      }
    }
  }


  private void read (Reader reader) throws IOException
  {
    TokenStream t = new StandardTokenizer();
    ((Tokenizer)t).setReader (reader);
    CharTermAttribute termAtt = t.addAttribute (CharTermAttribute.class);

    try {
      t.reset();
      while (t.incrementToken()) {
        final String word = termAtt.toString();
//        System.out.println (word);
        if (wordOK (word.toLowerCase())) {
          set.add (word);
        }
      }
    }
    catch (IllegalArgumentException e)
    {
      System.out.println (e.getMessage());
      System.out.println (termAtt.toString());
      System.err.println (e.getMessage());
      System.err.println (termAtt.toString());
    }
    finally {
      t.close();
    }
  }


  private void write (String file) throws FileNotFoundException, IOException
  {
    stringMap.writeGzipFile (file);
  }


  private boolean wordOK (String word)
  {
    if (word.length() < 7) return false;

    for (int i = 0; i < word.length(); i++) {
      if (CHARS.indexOf (word.charAt(i)) == -1) return false;
    }
    return true;
  }

  private final Set<String> set = new TreeSet<String>();
  private final StringMap stringMap = new StringMap();

  // Ääkköset, mutta ei å:ta.
  private static final String CHARS = "abcdefghijklmnopqrstuvwxyzäö";

  private static final String VOIKKO_PATH = "/usr/local/lib/voikko";
  private static final String LIBRARY_PATH = "/usr/local/lib";
  private static final String LIBVOIKKO = "/usr/local/lib/libvoikko.so";

//  private static final String LANGUAGE = "fi";
  private static final String LANGUAGE = "fi-x-sukija";

  private static final Voikko voikko = VoikkoUtils.getVoikko (LANGUAGE, VOIKKO_PATH, LIBRARY_PATH, LIBVOIKKO);
}

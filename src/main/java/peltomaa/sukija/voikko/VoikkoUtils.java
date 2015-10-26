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

package peltomaa.sukija.voikko;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.apache.lucene.analysis.util.CharArraySet;
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.util.CharCombinator;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionUtils;

/**
 * Staattisia metodeja, jotka palauttavat objektin tyyppiä Voikko
 * tai jotka korjaavat Libvoikon virheitä tai jotka muuten ovat
 * hyödyllisiä Sukijassa.
 */
public final class VoikkoUtils {
  private VoikkoUtils() {}


  /**
   * Palautetaan Voikko-objekti.<p>
   *
   * @param language    Kielikoodi. Tämän pitää olla "fi".
   * @param path        Polku, josta sanastoja etsitään.
   * @param libraryPath
   * @param libvoikkoPath Libvoikon kirjasto (esim. /usr/local/library/libvoikko.so).
   */
  public static final Voikko getVoikko (String language,
                                        String path,
                                        String libraryPath,
                                        String libvoikkoPath)
  {
    if (language == null) {
      new RuntimeException ("VoikkoUtils: language == null");
    }
    else if (path == null) {
      return new Voikko (language);
    }
    else if (libraryPath == null && libvoikkoPath == null) {
      return new Voikko (language, path);
    }
    else if (libraryPath != null && libvoikkoPath != null) {
      Voikko.addLibraryPath (libraryPath);
      System.load (libvoikkoPath);
      return new Voikko (language, path);
    }
    else {
      throw new RuntimeException ("VoikkoUtils: virheelliset parametrit");
    }
    return null;
  }


  /**
   * Palautetaan Voikko-objekti, jonka parametrit ovat<p>
   * Tämä on sama kuin kutsu
   * <pre>
     getVoikko ("fi", HOME + "/vvfst/voikkodict", HOME + "/vvfst/lib", HOME + "/vvfst/lib/libvoikko.so")
   * </pre>
   * missä
   * <pre>
     String HOME = System.getProperty ("user.home");
   * </pre>
   */
  public static final Voikko getVoikko()
  {
//    Voikko.addLibraryPath (LIBRARY_PATH);
    System.load (LIBVOIKKO);
    return new Voikko (LANGUAGE, PATH);
  }


  /** Libvoikossa osa sijamuodoista on väärin kirjoitettu.
   *  Tämä muuttaa ne oikeiksi. Esimerkiksi
   *  <pre>
      Analysis a = ...
      String s = VoikkoUtils.sijamuoto(a.get("SIJAMUOTO"));
      </pre>
   * Jos sijamuotoa ei ole, palauttaa arvon {@code null}.
   */
  public static String sijamuoto (String väärinKirjoitettuSijamuoto)
  {
    return ((väärinKirjoitettuSijamuoto == null)
            ? null
            : caseMap.get (väärinKirjoitettuSijamuoto));
  }


  /** Tulostaa formatoituna analyysin arvot virtaan out.<p>
   *  Tulostaa attribuutin "SIJAMUOTO" arvon oikein kirjoitettuna.
   */
  public static final void printAnalysisResult (Analysis analysis, PrintStream out)
  {
    Iterator<String> i = analysis.keySet().iterator();
    while (i.hasNext()) {
      final String key = i.next();
      String value = analysis.get (key);
      if (value != null && key.equals("SIJAMUOTO")) value = sijamuoto (value);
      out.println (key + " " + value);
    }
  }


  public static final void printAnalysisResult (VoikkoAttribute voikkoAtt, PrintStream out)
  {
    List<Analysis> list = voikkoAtt.getAnalysis();
//System.out.println ("VoikkoUtils " + (list == null));
    if (list == null) return;

    for (int i = 0; i < list.size(); i++) {
      printAnalysisResult (list.get(i), out);
      out.println ("");
    }
  }


  /**
   * Etsitään sanan {@code word} perusmuodot (niitä voi olla useampi kuin yksi).<p>
   *
   * Jos {@code voikko} löytää sanan perusmuodot, palautetaan arvo {@code true}, muuten
   * palautetaan arvo {@code false}.
   * Sanan persumuodot palautetaan oliossa {@code result}. Jos perusmuoto(j)a ei löydy,
   * olioon {@code result} palautetaan {@code word}. Olion {@code result} arvot muutetaan
   * pieniksi kirjaimiksi.
   */
  public static final boolean analyze (Voikko voikko, String word, Collection<String> result)
  {
    result.clear();
    List<Analysis> analysis = voikko.analyze (word);
    if (analysis.size() == 0) {
      result.add (word.toLowerCase());
      return false;
    }
    else {
      for (Analysis a : analysis) {
        result.add (a.get("BASEFORM").toLowerCase());
      }
      return true;
    }
  }


  public static final boolean analyze (Voikko voikko, String word, Collection<String> result,
                                       String from, String to)
  {
    CharCombinator charCombinator = new CharCombinator (word, from, to);

//System.out.println ("VoikkoUtils " + word + " " + charCombinator.size());
    Iterator<String> iterator = charCombinator.iterator();
    while (iterator.hasNext()) {
      final String s = iterator.next();
      if (analyze (voikko, s, result)) {
        return true;
      }
    }
    return false;
  }


  /**
   * Kopioi oliosta 'from' olioon 'result' sanat, jotka
   * ovat oliossa 'wordSet'.
   */
  public static final boolean copyIf (Collection<String> from, CharArraySet wordSet, Collection<String> result)
  {
    result.clear();
    for (String s : from) {
      if (wordSet.contains (s)) {
        result.add (s);
      }
    }
    return (result.size() > 0);
  }


  public static final boolean copyIf (Collection<String> from, CharArraySet wordSet, Collection<String> result,
                                      Vector<Suggestion> suggestion)
  {
    result.clear();
    for (String s : from) {
      if (wordSet.contains (s)) {
        result.add (s);
      }
      else {
        Collection<String> p = SuggestionUtils.getSuggestions (suggestion, s);
        if (p != null) {
          result.addAll (p);
//System.out.println ("VoikkoUtils " + s + " " + p.toString());
        }
      }
    }
    return (result.size() > 0);
  }


  private static final String HOME = System.getProperty ("user.home");
  private static final String PATH = HOME + "/vvfst/voikkodict";
  private static final String LIBVOIKKO = HOME + "/vvfst/lib/libvoikko.so";
  private static final String LANGUAGE = "fi";
  private static final String LIBRARY_PATH = HOME + "/vvfst/lib";
  private static final Map<String,String> caseMap = new HashMap<String,String>();

  static {
    caseMap.put ("nimento", "nimentö");
    caseMap.put ("omanto", "omanto");
    caseMap.put ("osanto", "osanto");
    caseMap.put ("olento", "olento");
    caseMap.put ("tulento", "tulento");
    caseMap.put ("kohdanto", "kohdanto");
    caseMap.put ("sisaolento", "sisäolento");
    caseMap.put ("sisaeronto", "sisäeronto");
    caseMap.put ("sisatulento", "sisätulento");
    caseMap.put ("ulkoolento", "ulko_olento");
    caseMap.put ("ulkoeronto", "ulkoeronto");
    caseMap.put ("ulkotulento", "ulkotulento");
    caseMap.put ("vajanto", "vajanto");
    caseMap.put ("seuranto", "seuranto");
    caseMap.put ("keinonto", "keinonto");
    caseMap.put ("kerrontosti", "kerronto_sti");
  }
}

/*
Copyright (©) 2015-2016, 2021 Hannu Väisänen

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
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.CharArraySet;
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peltomaa.sukija.attributes.BaseFormAttribute;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.util.CharCombinator;
import peltomaa.sukija.util.Constants;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionUtils;

/**
 * Staattisia metodeja, jotka palauttavat objektin tyyppiä Voikko
 * tai jotka korjaavat Libvoikon virheitä tai jotka muuten ovat
 * hyödyllisiä Sukijassa.
 */
public final class VoikkoUtils {
  private VoikkoUtils() {}


  private static Voikko voikko = null;
  private static final Logger LOG = LoggerFactory.getLogger (VoikkoUtils.class);


  /**
   * Palautetaan Voikko-objekti.<p>
   *
   * @param language    Kielikoodi.
   * @param path        Polku, josta sanastoja etsitään.
   * @param libraryPath Polku, josta löytyy tiedosto libvoikko.so.
   * @param libvoikkoPath Libvoikon kirjasto (esim. /usr/local/library/libvoikko.so).
   */
  public static final Voikko getVoikko (String language,
                                        String path,
                                        String libraryPath,
                                        String libvoikkoPath)
  {
    if (voikko == null) {
      LOG.info ("language " + language);
      LOG.info ("path " + path);
      LOG.info ("libraryPath " + libraryPath);
      LOG.info ("libvoikkoPath " + libvoikkoPath);

      if (language == null) {
        new RuntimeException ("VoikkoUtils: language == null");
      }
      else if (path == null) {
        voikko = new Voikko (language);
      }
      else if (libraryPath == null && libvoikkoPath == null) {
        voikko = new Voikko (language, path);
      }
      else if (libraryPath != null && libvoikkoPath != null) {
        Voikko.addLibraryPath (libraryPath);
        try {
          System.load (libvoikkoPath);
        }
        catch (UnsatisfiedLinkError e) {
          if (!e.getMessage().matches (".*Native Library.*libvoikko.*already loaded in another classloader")) {
            throw e;
          }
        }
        voikko = new Voikko (language, path);
      }
      else {
        throw new RuntimeException ("VoikkoUtils: virheelliset parametrit");
      }
    }
    return voikko;
  }


  public static Voikko getVoikko (String language)
  {
    return getVoikko (language, null, null, null);
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


  public static final void printAnalysisResult (Analysis analysis, String[] what, PrintStream out)
  {
    if (what == null) {
      printAnalysisResult (analysis, out);
    }
    else {
      for (String key: what) {
        String value = analysis.get (key);
        if (value != null && key.equals("SIJAMUOTO")) value = sijamuoto (value);
        out.print (value + " ");
      }
    }
  }


  public static final void printAnalysisResult (VoikkoAttribute voikkoAtt, String[] what, PrintStream out)
  {
    List<Analysis> list = voikkoAtt.getAnalysis();
//System.out.println ("VoikkoUtils " + (list == null));
    if (list == null) return;

    for (int i = 0; i < list.size(); i++) {
      printAnalysisResult (list.get(i), what, out);
      out.println ("");
    }
  }


  public static final boolean panalyze (Voikko voikko, String word, Set<String> result,
                                        String from, String to)
  {
    CharCombinator charCombinator = new CharCombinator (word, from, to);

    Iterator<String> iterator = charCombinator.iterator();
    while (iterator.hasNext()) {
      final String s = iterator.next();
      List<Analysis> list = voikko.analyze (s);
      if (list.size() > 0) {
        result.clear();
        result.addAll (getBaseForms (list));
        return true;
      }
    }
    return false;
  }


  public static final Set<String> getBaseForms (List<Analysis> analysis)
  {
    if (analysis == null) {
      throw new RuntimeException ("analysis == null.");
    }

/*
for (Analysis a : analysis) {
  System.out.println ("Voikko " + a.get("BASEFORM").toLowerCase());
}
*/

    Set<String> set = new HashSet<String>();
    for (Analysis a : analysis) {
      set.add (a.get("BASEFORM").toLowerCase());
    }
    return set;
  }


  public static final Analysis newBaseForm (String baseForm)
  {
    Analysis analysis = new Analysis();
    analysis.put ("BASEFORM", baseForm);
    return analysis;
  }

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

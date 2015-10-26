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

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.FilteringTokenFilter;
import org.puimula.libvoikko.*;
import peltomaa.sukija.attributes.VoikkoAttribute;


/** Suodatin, joka valitsee sanoja Voikon ominaisuuksien mukaan. Tämän täytyy olla
 *  suodatinketjussa suodattimen {@link VoikkoFilter} jälkeen, koska luokka käyttää
 *  sen tuottamia attribuutteja {@link peltomaa.sukija.attributes.VoikkoAttribute}.
 *  Esimerkiksi
    <pre>
      TokenStream t = ...
      t = new VoikkoFilter (t, voikko);
      t = VoikkoAttributeFilter (t, "CLASS", "paikannimi", "\\p{Lu}.*");
    </pre>
 *  erottaa tiedostosta paikannimet
 */
public final class VoikkoAttributeFilter extends FilteringTokenFilter {
  /**
   * @param input  Virta, josta symbolit luetaan.
   * @param key    Voikon attribuutti (esim. "CLASS").
   * @param value  Arvo, joka attribuutilla pitää olla, jotta sana hyväksytään (esim. "paikannimi").
   * @param regex  Hyväksytään vain sellaiset sanat, jotka täsmäävät säännöllisen lausekkeen 'regex' kanssa.
   *               Jos tämä on {@code null}, hyväksytään kaikki sanat, joilla on oikea attribuutin {@code} arvo.
   */
  public VoikkoAttributeFilter (TokenStream input, String key, String value, String regex)
  {
    super (input);
    map.put (key, new HashSet<String> (Arrays.asList (value)));
    if (regex != null) pattern = Pattern.compile (regex);
  }


  /**
   * Sama kuin {@code VoikkoAttributeFilter (input, key, value, null)}
   */
  public VoikkoAttributeFilter (TokenStream input, String key, String value)
  {
    this (input, key, value, null);
  }


  /**
   * @param input  Virta, josta symbolit luetaan.
   * @param key    Voikon attribuutti (esim "CLASS").
   * @param value  Sana hyväksytään, jos attribuutilla on mikä tahansa arvo tästä kokoelmasta.
   * @param regex  Hyväksytään vain sellaiset sanat, jotka täsmäävät säännöllisen lausekkeen 'regex' kanssa.
   *               Jos tämä on {@code null}, hyväksytään kaikki sanat, joilla on oikea attribuutin {@code} arvo.
   */
  public VoikkoAttributeFilter (TokenStream input, String key, Collection<String> value, String regex)
  {
    super (input);
    map.put (key,  new HashSet<String> (value));
    if (regex != null) pattern = Pattern.compile (regex);
  }


  /**
   * Sama kuin {@code VoikkoAttributeFilter (input, key, value, null)}
   */
  public VoikkoAttributeFilter (TokenStream input, String key, Collection<String> value)
  {
    this (input, key, value, null);
  }


  /**
   * @param input  Virta, josta symbolit luetaan.
   * @param map    Kokoelma voikon attribuuttien ja niiden arvoja. Esim. {@code map.value("CLASS")} sisältää
   *               arvot, jotka attribuutilla "CLASS" pitää olla, jotta se hyväksytään.
   * @param regex  Hyväksytään vain sellaiset sanat, jotka täsmäävät säännöllisen lausekkeen 'regex' kanssa.
   *               Jos tämä on {@code null}, hyväksytään kaikki sanat, joilla on oikea attribuutin {@code} arvo.
   */
  public VoikkoAttributeFilter (TokenStream input, Map<String,Set<String>> map, String regex)
  {
    super (input);
    this.map = map;
    if (regex != null) pattern = Pattern.compile (regex);
  }


  /**
   * Sama kuin {@code VoikkoAttributeFilter (input, key, map, null)}
   */
  public VoikkoAttributeFilter (TokenStream input, Map<String,Set<String>> map)
  {
    this (input, map, null);
  }


  @Override
  protected boolean accept() throws IOException
  {
    List<Analysis> analysis = voikkoAtt.getAnalysis();
    List<Analysis> result = new ArrayList<Analysis>();

    Iterator<String> i = map.keySet().iterator();
    while (i.hasNext()) {
      final String key = i.next();
      if (OK (key, map.get(key), analysis, result)) {
        voikkoAtt.setAnalysis (result);
        return true;
      }
    }
    return false;
  }


  private boolean OK (String key, Set<String> value, List<Analysis> analysis, List<Analysis> result)
  {
    for (Analysis a: analysis) {
      final String s = a.get(key);
      if (s == null) return false;
      if (value.contains(s) && (pattern == null || pattern.matcher(termAtt.toString()).matches())) {
        result.add (a);
        return true;
      }
    }
    return false;
  }


  private final FlagsAttribute flagsAtt = addAttribute (FlagsAttribute.class);
  private final CharTermAttribute termAtt = addAttribute (CharTermAttribute.class);
  private final PositionIncrementAttribute posIncrAtt = addAttribute (PositionIncrementAttribute.class);
  private final VoikkoAttribute voikkoAtt = addAttribute  (VoikkoAttribute.class);
  private Pattern pattern;
  private Map<String,Set<String>> map = new HashMap<String,Set<String>>();
}

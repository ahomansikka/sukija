/*
Copyright (©) 2009-2012 Hannu Väisänen

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

import java.util.Vector;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.util.RegexUtil;
import peltomaa.sukija.suggestion.*;
import peltomaa.sukija.util.StringUtil;


public class FinnishSuggestion {
  private FinnishSuggestion() {}


  private static final void initialize (Morphology morphology)
  {
    v.add (new CharSuggestion (morphology, "w", "v"));         // Wanha => vanha.
    v.add (new EndSuggestion (morphology, end));
    v.add (new CharCombinationSuggestion (morphology, "bdgkptw", "ptkgbdv"));
    v.add (new Length3Suggestion (morphology));
    v.add (new RegexSuggestion (morphology, "^([0-9]+)", ""));
    v.add (new RegexSuggestion (morphology, "(\"-|''-|--)", "-"));
    v.add (new ApostropheSuggestion (morphology));
    v.add (new EraseSuggestion (morphology, '[', ']'));
    v.add (new RegexCombinationSuggestion (morphology, rx_hd));
    v.add (new RegexCombinationSuggestion (morphology, rx_de));
    v.add (new RegexCombinationSuggestion (morphology, rx_grad));
    v.add (new RegexCombinationSuggestion (morphology, rx_array1));
    v.add (new RegexCombinationSuggestion (morphology, rx_array2));
    v.add (new RegexCombinationSuggestion (morphology, rx_ija));
    v.add (new RegexCombinationSuggestion (morphology, rx_inen));
    v.add (new RegexCombinationSuggestion (morphology, rx_nen));
    v.add (new RegexSuggestion (morphology, "C(eesti)$", "esti"));
    v.add (new RegexSuggestion (morphology, "^(parai)",  "parhai"));
    v.add (new SplitSuggestion (morphology, "(\"-|''-|--|-)"));
    v.add (new CompoundWordSuggestion (morphology, 4));
  }


  public static final Vector<Suggestion> getSuggestions (Morphology morphology)
  {
    if (!initialized) {
      initialize (morphology);
      initialized = true;
    }
    return v;
  }


  private static boolean initialized = false;
  private static Vector<Suggestion> v = new Vector<Suggestion>();


  private static final String[] rx_hd = {
    "V(h)V",        "hd",   // Puhistus => puhdistus.
  };

  private static final String[] rx_de = {
    "V(h)V",        "hd",   // Puhistus => puhdistus.
    "VV(v)V",       "d",    // Pöyvän => pöydän, uuven => uuden.
  };

  private static final String[] rx_grad = {
    "r(t)elle",        "r",
    "h(t)elle",        "d",
    "V(t)elle",        "d",
    "(n)pi(?:A|in)?$", "m",  // Pienenpi, pienenpiä, pienenpiin.
  };

  private static final String[] end = {
    "ki", "n",  // Puuki => puukin. Ei ole sijapääte.
    "ks", "i",  // Puuks(i); myös yks(i), kaks(i).
    "ll", "a",  // Puull(a).
    "ll", "e",  // Puull(e)
    "ll", "ä",  // Pääll(ä).
    "ss", "a",  // Puuss(a).
    "ss", "ä",  // Pääss(ä).
    "st", "a",  // Puust(a).
    "st", "ä",  // Pääst(ä).
    "tt", "a",  // Puutt(a).
    "tt", "ä",  // Päätt(ä).
  };

  private static final String[] rx_array1 = {
    "(illi)(?:nen|s)", "i",    // Traagillinen => traaginen.
    "C[ae](hi)C",      "i",    // Aina(h)inen, ete(h)inen.
    "(?:uu|yy)(e)",    "de",   // Uuen => uuden.
    "ai(j)[eou]",      "",     // Ai(j)emmin, ai(j)omme, kai(j)utin.
  };

  private static final String[] rx_array2 = {
    "V(mm)Ai[ns]",     "m",    // Alammainen => alamainen.
    "V(m)Ai[ns]",      "mm",   // Lähimäinen => lähimmäinen.
  };


  // Vartia => vartija.
  //
  private static final String[] rx_ija = {
    "..C(ia)(?=ni|si|nsa|mme|tte|n$|a|ta|na|ks[ie]|ssa|sta|h?an|lla|lta|lle|tta|t$|in)", "ija",
    "..C(ioi)(?=den|tten|na|ks[ie]|ssa|sta|hin|lla|lta|lle|tta|ine)",                    "ijoi",
    "..C(iä)(?=ni|si|nsä|mme|tte|n$|ä|tä|nä|ks[ie]|ssä|stä|h?än|llä|ltä|lle|ttä|t$|in)", "ijä",
    "..C(iöi)(?=den|tten|nä|ks[ie]|ssä|stä|hin|llä|ltä|lle|ttä|ine)",                    "ijöi",
  };

  private static final String[] rx_inen = {
    "..C[aouyäö](i)(?=nen|se|st[aeä]|sn[aä]|si)", "",
  };

  private static final String[] rx_nen = {
    "..C[aouyäö](n)(?=en)",            "in",
    "..C[aouyäö](s)(?=e|t[aeä]|nA|i)", "is",
  };


  private static final String[] all =
    StringUtil.join (rx_hd, rx_de, rx_grad, rx_array1, rx_array2, rx_ija, rx_inen, rx_nen);


  private static final void addSuggestions (Morphology morphology, Vector<Suggestion> v, String[] array)
  {
    for (int i = 0; i < array.length; i += 2) {
//System.out.println (array[i] + " " + array[i+1]);
      v.add (new RegexSuggestion (morphology, array[i], array[i+1]));
    }
  }
}

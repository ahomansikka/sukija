/*
Copyright (©) 2013-2017, 2022 Hannu Väisänen

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

package peltomaa.sukija.suggestion;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;
import peltomaa.sukija.util.StringUtil;
import peltomaa.sukija.voikko.VoikkoUtils;


/**
 * Try to recognise a word without a prefix. If succesful, return
 * prefix + recognised word.
 *
 * For example, if prefix is "aasian" and word is "aasianleijonaa",
 * try to recognise "leijonaa". If succesful, return "aasianleijona".
 */
public class PrefixSuggestion extends Suggestion {
  public PrefixSuggestion (Voikko voikko, String prefix, boolean savePrefix, boolean saveWord)
  {
    super (voikko);
    prefixSet = new HashSet<String>();
    prefixSet.add (prefix);
    minLength = prefix.length();
    maxLength = minLength;
    this.savePrefix = savePrefix;
    this.saveWord = saveWord;
  }


  public PrefixSuggestion (Voikko voikko, String prefix)
  {
    this (voikko, prefix, true, true);
  }


  public PrefixSuggestion (Voikko voikko, List<String> prefix, boolean savePrefix, boolean saveWord)
  {
    super (voikko);
    prefixSet = new HashSet<String> (prefix);
    minLength = 1000000;
    maxLength = 0;
    for (String s: prefixSet) {
      if (minLength > s.length()) minLength = s.length();
      if (maxLength < s.length()) maxLength = s.length();
    }
    this.savePrefix = savePrefix;
    this.saveWord = saveWord;
  }


  public PrefixSuggestion (Voikko voikko, List<String> prefix)
  {
    this (voikko, prefix, true, true);
  }


  @Override
  public boolean suggest (String word)
  {
    clearAnalysis();
    analysisList.clear();

    // Käydään läpi kaikki etuliitteet pisimmästä alkaen ja
    // lopetetaan, kun löytyy eka tunnistettu sana.
    //
    for (int i = Math.min (maxLength, word.length()-1); i >= minLength; i--) {
      final String p = word.substring (0, i);    // Sanan alku.
      if (prefixSet.contains (p)) {              // Onko etuliite?
        List<Analysis> list = voikko.analyze (StringUtil.trimNotLetterOrDigit (word.substring(i)));
        if (list.size() > 0) {
          //
          // Etuliitteetön sana tunnistettiin.
          //
          if (saveWord) {
            analysisList.addAll (list);
          }
          if (savePrefix) {
            prefixList = voikko.analyze (p);
            if (prefixList.size() > 0) {
              analysisList.addAll (prefixList);
//System.out.println ("prefixList " + prefixList);
            }
            else {
//System.out.println ("q " + p);
              analysisList.add (VoikkoUtils.newBaseForm (p));
            }
          }

          for (Analysis a: list) {
//System.out.println ("p " + p + " " +  a.get("BASEFORM"));
            analysisList.add (VoikkoUtils.newBaseForm (StringUtil.joinIfVowel (p, "-", a.get("BASEFORM"))));
            if (prefixList.size() > 0) {
              for (Analysis q: prefixList) {
                analysisList.add (VoikkoUtils.newBaseForm (StringUtil.joinIfVowel (q.get("BASEFORM"), "-", a.get("BASEFORM"))));
              }
            }
          }
//System.out.println ("analysisList " + analysisList);
          addToAnalysis (analysisList);
          return true;
        }
      }
    }

    return false;
  }

  private Set<String> prefixSet;  // Etuliitteet.
  private int minLength;  // Lyhimmän etuliitteen pituus.
  private int maxLength;  // Pisimmän etuliitteen pituus.
  private boolean savePrefix;
  private boolean saveWord;
  private List<Analysis> analysisList = new ArrayList<Analysis>();
  private List<Analysis> prefixList;
}

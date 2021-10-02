/*
Copyright (©) 2021 Hannu Väisänen

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

package peltomaa.sukija.filters;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.compound.CompoundWordTokenFilterBase;
import org.apache.lucene.analysis.TokenStream;
import org.puimula.libvoikko.*;
import peltomaa.sukija.attributes.OriginalWordAttribute;
import peltomaa.sukija.attributes.VoikkoAttribute;
import peltomaa.sukija.util.Constants;


// public class FinnishCompoundWordTokenFilter extends CompoundWordTokenFilterBase {

public class FinnishCompoundWordTokenFilter extends HVTokenFilterBase {
 /**
   * The default for minimal word length that gets decomposed.
   */
  public static final int DEFAULT_MIN_WORD_SIZE = 3;

  /**
   * The default for minimal length of subwords that get propagated to the output of this filter.
   */
  public static final int DEFAULT_MIN_SUBWORD_SIZE = 1;

  /**
   * The default for maximal length of subwords that get propagated to the output of this filter.
   */
  public static final int DEFAULT_MAX_SUBWORD_SIZE = Integer.MAX_VALUE;


  public FinnishCompoundWordTokenFilter (TokenStream input)
  {
    super (input);
  }


  public FinnishCompoundWordTokenFilter (TokenStream input,
                                         int minWordSize,
                                         int minSubwordSize,
                                         int maxSubwordSize,
                                         boolean onlyLongestMatch)
  {
    super (input); //, CharArraySet.EMPTY_SET, minWordSize, minSubwordSize, maxSubwordSize, onlyLongestMatch);
    this.minWordSize = minWordSize;
  }

/*

  public FinnishCompoundWordTokenFilter (TokenStream input)
  {
    this (input, DEFAULT_MIN_WORD_SIZE, DEFAULT_MIN_SUBWORD_SIZE, DEFAULT_MAX_SUBWORD_SIZE, false);
  }
*/



  @Override
  protected void decompose()
  {
    if (termAtt.length() >= this.minWordSize) {
      allTokens.clear();
      voikkoAtt = input.getAttribute (VoikkoAttribute.class);
      originalWordAtt = input.getAttribute (OriginalWordAttribute.class);
      List<Analysis> analysis = voikkoAtt.getAnalysis();

      for (Analysis a : analysis) {
//        System.out.println (a.get("BASEFORM"));
        String structure = a.get ("STRUCTURE");
        if (structure.indexOf('=',1) > 0) {
          makeTokens (structure);
        }
      }
      tokens.addAll (allTokens);
    }
  }


  private void makeTokens (String structure)
  {
    int n = 0;
    int len = 0;
    int start = 0;
    for (int i = 1; i < structure.length(); i++) { // Ohitetaan eka yhdysmerkki.
      if (structure.charAt(i) == '=') {
//        System.out.println ("Term1 " + termAtt.toString() + " " + start + " " + len + " " + (start+len)
//                            + " " + Constants.toString(flagsAtt)
//                            + " " + termAtt.toString().substring(start)
//                            + " " + originalWordAtt.getOriginalWord());
        allTokens.add (new NewToken (termAtt.toString(), Constants.WORD));
//      allTokens.add (new NewToken (termAtt.toString().substring(start,start+len), Constants.WORD));
        start = n;
        len = 0;
      }
      else if (structure.charAt(i) == '-') {
        n++; // Ei kopioida viivaa.
      }
      else {
        len++;
        n++;
      }
    }
//    System.out.println ("Term " + termAtt.subSequence (start, start+len) + "\n");
    // Lisätään yhdyssanan viimeinen osa.
    allTokens.add (new NewToken (termAtt.toString(), Constants.WORD));
  }


  private static class TokenComparator implements java.util.Comparator<NewToken> {
    public int compare (NewToken o1, NewToken o2)
    {
      // Muuttujat startOffset ja endOffset ovat yhdyssanan eivätkä sen osien indeksejä
      // eli ne ovat samat o1:ssä ja o2:ssa eli niitä ei tarvitse testata.
      //
      return o1.text.compareTo (o2.text);
    }
  }

  private int minWordSize = DEFAULT_MIN_WORD_SIZE;

  private Set<NewToken> allTokens = new TreeSet<NewToken> (new TokenComparator());
  private OriginalWordAttribute originalWordAtt;
  private VoikkoAttribute voikkoAtt;
}

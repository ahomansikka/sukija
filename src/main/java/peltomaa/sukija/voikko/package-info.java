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

/** Liittymä Voikon ja Solr:n välillä.

Esimerkki:

<pre>
    Reader r = new FileReader ("tiedosto.txt");
    TokenStream t = new HVTokenizer();  // Tai FinnishTokenizer().
    ((Tokenizer)t).setReader (r);
    t = new VoikkoFilter (t, voikko);
    t.reset();
    CharTermAttribute termAtt = t.addAttribute (CharTermAttribute.class);
    VoikkoAttribute voikkoAtt = t.addAttribute (VoikkoAttribute.class);
    OriginalWordAttribute originalWordAtt = t.addAttribute (OriginalWordAttribute.class);

    while (t.incrementToken()) {
      // Tulosta sana ja sen perusmuoto.
      System.out.println (originalWordAtt.toString() + " " + termAtt().toString();

      // Tulosta sanasta kaikki tiedot, jotka Voikko tuottaa.
      VoikkoUtils.printAnalysisResult (voikkoAtt.getAnalysis(), System.out);
    }
</pre>
*/

package peltomaa.sukija.voikko;

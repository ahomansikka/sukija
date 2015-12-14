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


package peltomaa.sukija.voikko.tokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.puimula.libvoikko.Voikko;


/** Analyzer that uses VoikkoTokenizer.
 */
public final class VoikkoAnalyzer extends Analyzer {
  public VoikkoAnalyzer (Voikko voikko, boolean ignoreND)
  {
    this.voikko = voikko;
    this.ignoreND = ignoreND;
  }


  protected TokenStreamComponents createComponents (String fieldName)
  {
    Tokenizer source = new VoikkoTokenizer (voikko, ignoreND);
    return new TokenStreamComponents (source);
  }


  private Voikko voikko;
  private boolean ignoreND;
}

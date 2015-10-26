/*
Copyright (©) 2014 Hannu Väisänen

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

package peltomaa.sukija.morphology;


import java.util.Set;
import java.util.TreeSet;
import org.apache.lucene.analysis.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peltomaa.sukija.util.SukijaFilter;


public class MorphologyFilter extends SukijaFilter {
  public MorphologyFilter (TokenStream in, Morphology morphology)
  {
    super (in);
    this.morphology = morphology;
  }


  protected void filter (String word)
  {
    set.clear();
    morphology.analyzeLowerCase (word, set);
    iterator = set.iterator();
  }


  private static final Logger LOG = LoggerFactory.getLogger (MorphologyFilter.class);
  private Morphology morphology;
  private Set<String> set = new TreeSet<String>();
}

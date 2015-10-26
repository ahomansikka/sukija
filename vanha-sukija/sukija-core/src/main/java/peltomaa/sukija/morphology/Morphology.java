/*
Copyright (©) 2009-2011 Hannu Väisänen

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

import java.util.Collection;

public interface Morphology {
  /**
   * Analyze a word and return the result(s) in a collection.<p>
   *
   * If function does not find an analysis for the {@code word} the {@code word}
   * is returned as the only element of the collection.
   * So the collection is never empty.
   *
   * @param word  Word to be analyzed This must not be null.
   * @param c     A collection. This must not be null.
   *
   * @return      true if function finds an analysis for the {@code word}, otherwise false.
   *
   * @throws      MorphologyException on error.
   */
  boolean analyze (String word, Collection<String> c) throws MorphologyException;


  /**
   * Analyze a word and return the result(s) in a collection converted to lower case.<p>
   *
   * If function does not find an analysis for the {@code word} the {@code word}
   * is returned as the only element of the collection.
   * So the collection is never empty.
   *
   * @param word  Word to be analyzed This must not be null.
   * @param c     A collection. This must not be null.
   *
   * @return      true if function finds an analysis for the {@code word}, otherwise false.
   *
   * @throws      MorphologyException on error.
   */
  boolean analyzeLowerCase (String word, Collection<String> c) throws MorphologyException;
}

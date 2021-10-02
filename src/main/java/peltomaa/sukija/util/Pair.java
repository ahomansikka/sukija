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

package peltomaa.sukija.util;

import java.util.Vector;

/**
 * A heterogeneous pair.
 */
public class Pair<T, U> {
  /** First object of the pair. */
  public T first;

  /** Second object of the pair. */
  public U second;


  /** Constructor. */
  public Pair (T first, U second)
  {
    this.first = first;
    this.second = second;
  }

/*
  public Pair()
  {
    this.first = null;
    this.second = null;
  }
*/
  /**
   * Convert an array of objects to a vector of pairs.<p>
   *
   * First pair is (array[0], array[1]),
   * second pair is (array[2], array[3]), etc.<p>
   *
   * Array must have an even number of items.
   */
  public static final <T> Vector<Pair<T,T>> makePair (T[] array)
  {
    Vector<Pair<T,T>> v = new Vector<Pair<T,T>>();

    for (int i = 0; i < array.length; i += 2) {
      v.add (new Pair<T,T> (array[i], array[i+1]));
    }
    return v;
  }
}

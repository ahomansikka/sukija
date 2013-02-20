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

/** Create combinations.<p>
 *
 * Combination code is modified from GSL (Gnu Scientfic Library)
 * function gsl_combination, Copyright (C) 2001 Szymon Jaroszewicz.
 */
public class Combination {
  int n;
  int k;
  int []data;

  /**
   * Construct a combination.
   *
   * @param _n  The range of the combination (_n > 0).
   * @param _k  The number of elements in the combination (_k <= _n).
   */
  public Combination (int _n, int _k)
  {
    n = _n;
    k = _k;

    if (n < 0) throw new RuntimeException ("n < 0");
    if (k > n) throw new RuntimeException ("k > n");

    if (k > 0) {
      data = new int[k];
    }

    for (int i = 0; i < k; i++) {
      data[i] = i;
    }
  }


  /** Create next combination (in the standard lexicographical
   *  ordering). Return false if there is no next combination.
   */
  public boolean next()
  {
    if (k == 0) {
      return false;
    }

    int i = k - 1;

    while (i > 0 && data[i] == n - k + i) {
      i--;
    }

    if (i == 0 && data[i] == n - k) {
      return false;
    }

    data[i]++;
    for ( ; i < k - 1; i++) {
      data[i + 1] = data[i] + 1;
    }

    return true;
  }


  /** Return true if combination is valid; otherwise throw RuntimeException.
   */
  public boolean valid()
  {
    if (k > n) {
      throw new RuntimeException ("Combination has k greater than n.");
    }

    for (int i = 0; i < k; i++) {
      final int ci = data[i];

      if (ci >= n) {
        throw new RuntimeException ("Combination index outside range.");
      }

      for (int j = 0; j < i; j++) {
        if (data[j] == ci) {
          throw new RuntimeException ("Duplicate combination index.");
        }
        if (data[j] > ci) {
          throw new RuntimeException ("Combination indices not in increasing order.");
        }
      }
    }
    return true;
  }


  /** Write current combination to stream.
   */
  public void println (java.io.PrintStream s)
  {
    if (k == 0) return;

    for (int i = 0; i < k; i++) {
      s.print (data[i] + " ");
    }
    s.println ("");
  }


  /** Return the range (n) of the combination.
   */
  public int getN() {return n;}


  /**
   * Return the number of elements (k) in the combination.
   */
  public int getK() {return k;}


  /** Return the value of i'th element of the combination.
   */
  public int get (int i)
  {
//    if (i >= k) throw new RuntimeException ("i >= k");

    return data[i];
  }

/*
  public static void main (String[] args)
  {
    final int n = Integer.parseInt (args[0]);

    for (int k = 0; k <= n; k++) {
      Combination c = new Combination (n, k);
      do {
        c.println (System.out);
      } while (c.next());
    }
  }
*/
}

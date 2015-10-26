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


public class MorphologyException extends RuntimeException {
  /**
   * Construct an exception with message and cause.
   *
   * @param message The error message.
   * @param cause   The cause; can be null.
   */
  public MorphologyException (String message, Throwable cause)
  {
    super (message, cause);
  }

  /**
   * Construct an exception with message.
   *
   * @param message The error message.
   */
  public MorphologyException (String message)
  {
    super (message);
  }
}

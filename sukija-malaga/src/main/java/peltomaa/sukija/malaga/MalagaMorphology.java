/*
Copyright (©) 2009-2013 Hannu Väisänen

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

package peltomaa.sukija.malaga;

import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.morphology.MorphologyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;


public final class MalagaMorphology implements Morphology {
  /**
   * Construct MalagaMorphology class.
   *
   * @param projectFile  Malaga project file.
   */
  private MalagaMorphology (String projectFile)
  {
    try {
      init_libmalaga (projectFile);
    }
    catch (Throwable e)
    {
      System.out.println ("MalagaMorphology: " + e.getMessage());
      e.printStackTrace (System.out);
      System.exit (1);
    }
  }


  /**
   * Get an instance of this class.
   *
   * @throws MorphologyException if initialization fails
   */
  public static synchronized MalagaMorphology getInstance (String projectFile) throws MorphologyException
  {
    if (initialized) {
      return malaga;
    }

    try {
      if (r == null) throw new RuntimeException ("r == null.");

      malagaProjectFile = projectFile;

      if (malagaProjectFile == null) {
        // "1" is a dummy argument. Java can not compile next line without it.
        LOG.error (MessageFormat.format (r.getString("project-file-not-found"), 1));
      }
      else if (!(new java.io.File(malagaProjectFile)).exists()) {
        LOG.error (MessageFormat.format (r.getString ("no-file"), malagaProjectFile));
      }
      else {
        LOG.info (MessageFormat.format (r.getString ("init-malaga"), malagaProjectFile));

        malaga = new MalagaMorphology (malagaProjectFile);

        initialized = !malagaErrorHasOccured();

        return malaga;
      }
    }
    catch (ExceptionInInitializerError e)
    {
      LOG.error (MessageFormat.format (r.getString ("can-not-init"),  e.getClass().getName()));
      Throwable cause = e.getCause();

      if (cause != null) {
        LOG.error ("Cause = " + cause.getClass().getName());
        final String name = cause.getClass().getName();
        if (name.compareTo ("java.util.MissingResourceException") == 0) {
          MissingResourceException m = (MissingResourceException)cause;
          LOG.error ("Class = '" + m.getClassName() + "', key = '" + m.getKey() + "'");
        }
      }
    }
    catch (Throwable t)
    {
      initialized = false;
      final String message = t.getMessage();

      if (message == null) {
        final String m = MessageFormat.format (r.getString ("can-not-init"), t.getClass().getName());
        LOG.error (m);
        throw new MorphologyException (m);
      }
      else {
        final String m = MessageFormat.format (r.getString ("can-not-init"), message);
        LOG.error (m);
        throw new MorphologyException (m);
      }
    }
    initialized = false;
    return null;
  }


  /** Malaga grammatical analysis type. */
  private static final int MORPHOLOGY = 0;


  /** Malaga grammatical analysis type. */
  private static final int SYNTAX = 1;

  /** Has malaga been initialized? */
  private static boolean initialized = false;

  private static String malagaProjectFile;

  /** The name of the resource bundle file. */
  private static final String BUNDLE_NAME = "peltomaa.sukija.malaga.MalagaMorphology";

  private static ResourceBundle r =
                 PropertyResourceBundle.getBundle (BUNDLE_NAME, Locale.getDefault());

  private static final Logger LOG = LoggerFactory.getLogger (MalagaMorphology.class);

  private static MalagaMorphology malaga;


  protected void finalize() throws Throwable
  {
    terminate_libmalaga();
    super.finalize();
  }


  /* Malaga functions. */

  private static native void analyse_item (String item, int grammar);
  private static native Pointer first_analysis_result();
  private static native Pointer next_analysis_result();
  private static native int length_of_value (Pointer value);
  private static native String get_info();
  private static native Pointer value_to_readable (Pointer value, boolean full_value, int indent);
//  private static native String value_to_string (Pointer value);
  private static native void init_libmalaga (String projectFile);
  private static native void terminate_libmalaga();

  private static native void free (Pointer ptr); // C: void free(void *ptr);


  private static Pointer malaga_error;
  private static String lastMalagaErrorMessage;


  /** If Malaga error has occured, copy the error message pointed to by malaga_error
   *  to lastMalagaErrorMessage and return true, otherwise return false.
   */
  private static boolean malagaErrorHasOccured()
  {
    if (malaga_error == Pointer.NULL) return false;

    Pointer p = malaga_error.getPointer(0);
    if (p == Pointer.NULL) return false;

    lastMalagaErrorMessage = p.getString(0);

    if ((lastMalagaErrorMessage == null) || (lastMalagaErrorMessage.length() == 0)) {
      return false;
    }

    LOG.error (MessageFormat.format (r.getString ("malaga-error"), lastMalagaErrorMessage));
    return true;
  }


  /** Convert Malaga value to Java string.
   */
  private static String getString (Pointer value)
  {
//    return value_to_string (value);  // This does not work!

    if (value == Pointer.NULL) return null;

    Pointer p = value_to_readable (value, false, 0);
    if (p == Pointer.NULL) return null;

    String s = p.getString (0);

    free (p);  // Pointer must be freed.

    if (s == null) return null;

    s = s.substring (1, s.length()-1);  // Delete '"' at the beginning and end of string.
//    s = s.toLowerCase();                // Lowercase s, otherwise Nutch search does not work.
    if (LOG.isDebugEnabled()) LOG.debug ("getString [" + s + "]");
    return s;
  }


  public synchronized boolean analyze (String word, Collection<String> c) throws MorphologyException
  {
    if (!initialized) {
      throw new MorphologyException (MessageFormat.format (r.getString ("not-init"), "MalagaMorphology.analyze"));
    }

    if (LOG.isDebugEnabled()) LOG.debug ("analyze [" + word + "]");
//System.out.println ("analyze [" + word + "]");

    analyse_item (word, MORPHOLOGY);

    if (malagaErrorHasOccured()) {
      throw new MorphologyException (lastMalagaErrorMessage);
    }

    Pointer p = first_analysis_result();
    String result = getString (p);

    if (result == null) {
      c.add (word);
      return false;
    }
    else {
      c.add (result);
      while ((p = next_analysis_result()) != null) {
        result = getString (p);
        c.add (result);
      }
      return true;
    }
  }


  public synchronized boolean analyzeLowerCase (String word, Collection<String> c) throws MorphologyException
  {
    if (!initialized) {
      throw new MorphologyException (MessageFormat.format (r.getString ("not-init"), "MalagaMorphology.analyzeLowerCase"));
    }

    if (LOG.isDebugEnabled()) LOG.debug ("analyzeLowerCase [" + word + "]");
//System.out.println ("analyzeLowerCase [" + word + "]");

    analyse_item (word, MORPHOLOGY);

    if (malagaErrorHasOccured()) {
      throw new MorphologyException (lastMalagaErrorMessage);
    }

    Pointer p = first_analysis_result();
    String result = getString (p);

    if (result == null) {
      c.add (word.toLowerCase());
      return false;
    }
    else {
      c.add (result.toLowerCase());
      while ((p = next_analysis_result()) != null) {
        result = getString (p);
        c.add (result.toLowerCase());
      }
      return true;
    }
  }


  static {
    try {
      System.setProperty ("jna.encoding", "UTF8");
      Native.register ("malaga");
      malaga_error = NativeLibrary.getInstance("malaga").getGlobalVariableAddress ("malaga_error");
      LOG.info ("MalagaMorphology");
    }
    catch (Throwable e)
    {
      System.out.println ("MalagaMorphology: " + e.getMessage());
      e.printStackTrace (System.out);

      Throwable t = e.getCause();
      if (t != null) System.out.println (t.getMessage());
      System.exit (1);
    }
  }
}

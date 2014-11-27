/*
Copyright (©) 2012-2014 Hannu Väisänen

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

package peltomaa.sukija.voikko;

import org.puimula.libvoikko.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.morphology.MorphologyException;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class VoikkoMorphology implements Morphology {
  private VoikkoMorphology (String dictionary)
  {
    voikko = new Voikko (dictionary);
  }


  private VoikkoMorphology (String dictionary, String path)
  {
    voikko = new Voikko (dictionary, path);
  }


  /**
   * Get an instance of this class.
   *
   * @param dictionary Dictionary to use.
   *
   * @throws MorphologyException if initialization fails
   */
  public static synchronized VoikkoMorphology getInstance (String dictionary) throws MorphologyException
  {
    if (voikkoMorphology == null) {
      voikkoMorphology = new VoikkoMorphology (dictionary);
      LOG.info (MessageFormat.format (r.getString ("init-voikko-morphology"), null));
    }
    return voikkoMorphology;
  }


  /**
   * Get an instance of this class.
   *
   * @param dictionary Dictionary to use.
   *
   * @throws MorphologyException if initialization fails
   */
  public static synchronized VoikkoMorphology getInstance (String dictionary, String path) throws MorphologyException
  {
    if (voikkoMorphology == null) {
      voikkoMorphology = new VoikkoMorphology (dictionary, path);
      LOG.info (MessageFormat.format (r.getString ("init-voikko-morphology"), null));
    }
    return voikkoMorphology;
  }


  /**
   * Get an instance of this class.
   *
   * @param dictionary    Dictionary to use.
   * @param path          Path to dictionary files.
   * @param libvoikkoPath Path to libvoikko.so.
   * @param libraryPath   Path to libvoikko library
   *
   * @throws MorphologyException if initialization fails
   */
  public static synchronized VoikkoMorphology getInstance
    (String dictionary,
     String path,
     String libvoikkoPath,
     String libraryPath) throws MorphologyException
  {
    if (voikkoMorphology == null) {
      System.load (libvoikkoPath);
      Voikko.addLibraryPath (libraryPath);
      voikkoMorphology = new VoikkoMorphology (dictionary, path);
      LOG.info (MessageFormat.format (r.getString ("init-voikko-morphology"), null));
      LOG.info ("Voikko vfst morphology.");
    }
    return voikkoMorphology;
  }



  public synchronized boolean analyze (String word, Collection<String> c) throws MorphologyException
  {
    List<Analysis> list = voikko.analyze (word);
/*
    if (LOG.isDebugEnabled()) {
      LOG.debug ("analyze [" + word + "]");
      for (Analysis a: list) LOG.debug ("  " + a.get("BASEFORM"));
    }
*/
    if (list.size() == 0) {
      c.add (word);
      return false;
    }
    else {
      for (Analysis a: list) {
        c.add (a.get("BASEFORM"));
      }
      return true;
    }
  }


  public synchronized boolean analyzeLowerCase (String word, Collection<String> c) throws MorphologyException
  {
    List<Analysis> list = voikko.analyze (word);

    if (LOG.isDebugEnabled()) {
      LOG.debug ("analyzeLowerCase [" + word + "]");
      for (Analysis a: list) LOG.debug ("  " + a.get("BASEFORM").toLowerCase());
    }

    if (list.size() == 0) {
      c.add (word.toLowerCase());
      return false;
    }
    else {
      for (Analysis a: list) {
        final String s = a.get("BASEFORM");
        if (s == null) {
          if (LOG.isDebugEnabled()) {
            LOG.debug ("analyzeLowerCase [" + word + "]: no base form.");
          }
          c.add (word.toLowerCase());
        }
        else {
          c.add (s.toLowerCase());
        }
      }
      return true;
    }
  }


  private static Voikko voikko = null;
  private static VoikkoMorphology voikkoMorphology = null;

  private static final Logger LOG = LoggerFactory.getLogger (VoikkoMorphology.class);
  private static final String BUNDLE_NAME = "peltomaa.sukija.voikko.VoikkoMorphology";
  private static ResourceBundle r =
                 PropertyResourceBundle.getBundle (BUNDLE_NAME, Locale.getDefault());
}

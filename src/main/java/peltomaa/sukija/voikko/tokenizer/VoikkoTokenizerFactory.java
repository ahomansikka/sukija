/*
Copyright (©) 2015-2016, 2021-2022 Hannu Väisänen

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

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.lucene.util.ResourceLoader;
import org.apache.lucene.util.ResourceLoaderAware;
import org.apache.lucene.analysis.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
import org.apache.solr.common.util.PropertiesUtil;
import org.puimula.libvoikko.*;
import peltomaa.sukija.voikko.VoikkoUtils;


public class VoikkoTokenizerFactory extends TokenizerFactory implements ResourceLoaderAware {

  /** Create new VoikkoTokenizerFactory.
   */
  public VoikkoTokenizerFactory (Map<String,String> args)
  {
    super (args);

    language      = get (args, "language", "fi");
    path          = getValue (args, "path");
    libvoikkoPath = getValue (args, "libvoikkoPath");
    libraryPath   = getValue (args, "libraryPath");
    ignoreNL      = getBoolean (args, "ignoreNL", false);

    ignore_dot = get (args, "ignore_dot");
    ignore_numbers = get (args, "ignore_numbers");
    ignore_uppercase = get (args, "ignore_uppercase");
    ignore_nonwords = get (args, "ignore_nonwords");
    accept_first_uppercase = get (args, "accept_first_uppercase");
    accept_all_uppercase = get (args, "accept_all_uppercase");
    accept_extra_hyphens = get (args, "accept_extra_hyphens");
    accept_missing_hyphens = get (args, "accept_missing_hyphens");
  }


  public VoikkoTokenizer create (AttributeFactory factory)
  {
    return new VoikkoTokenizer (factory, voikko, ignoreNL);
  }


  @Override
  public void inform (ResourceLoader loader) throws IOException
  {
    LOG.info ("VoikkoTokenizerFactory " + loader.getClass().getName());
    voikko = VoikkoUtils.getVoikko (language, path, libraryPath, libvoikkoPath);

    if (ignore_dot != null) voikko.setIgnoreDot (Boolean.parseBoolean (ignore_dot));
    if (ignore_numbers != null) voikko.setIgnoreNumbers (Boolean.parseBoolean (ignore_numbers));
    if (ignore_uppercase != null) voikko.setIgnoreUppercase (Boolean.parseBoolean (ignore_uppercase));
    if (ignore_nonwords != null) voikko.setIgnoreNonwords (Boolean.parseBoolean (ignore_nonwords));
    if (accept_first_uppercase != null) voikko.setAcceptFirstUppercase (Boolean.parseBoolean (accept_first_uppercase));
    if (accept_all_uppercase != null) voikko.setAcceptAllUppercase (Boolean.parseBoolean (accept_all_uppercase));
    if (accept_extra_hyphens != null) voikko.setAcceptExtraHyphens (Boolean.parseBoolean (accept_extra_hyphens));
    if (accept_missing_hyphens != null) voikko.setAcceptMissingHyphens (Boolean.parseBoolean (accept_missing_hyphens));
  }


  private String getValue (Map<String,String> args, String name)
  {
    return PropertiesUtil.substituteProperty (get(args,name), null);
  }

  private Voikko voikko;
  private String language;
  private String path;
  private String libvoikkoPath;
  private String libraryPath;
  private boolean ignoreNL; // Hylkää sanat, joissa ei ole yhtään kirjainta (esim. "1234" tai "1234-").
  private static final Logger LOG = LoggerFactory.getLogger (VoikkoTokenizerFactory.class);

  private String ignore_dot;
  private String ignore_numbers;
  private String ignore_uppercase;
  private String ignore_nonwords;
  private String accept_first_uppercase;
  private String accept_all_uppercase;
  private String accept_extra_hyphens;
  private String accept_missing_hyphens;
}

/*
Copyright (©) 2016, 2018, 2021 Hannu Väisänen

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


import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.solr.common.util.PropertiesUtil;
import org.puimula.libvoikko.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peltomaa.sukija.voikko.VoikkoUtils;


public abstract class SukijaFilterFactory extends TokenFilterFactory {
  public SukijaFilterFactory (Map<String,String> args)
  {
    super (args);
    language      = get (args, "language", "fi");
    path          = getValue (args, "path");
    libvoikkoPath = getValue (args, "libvoikkoPath");
    libraryPath   = getValue (args, "libraryPath");
    successOnly   = getBoolean (args, "successOnly", false);

    voikko = VoikkoUtils.getVoikko (language, path, libraryPath, libvoikkoPath);

    LOG.info ("language " + language);
    LOG.info ("path " + path);
    LOG.info ("libvoikkoPath " + libvoikkoPath);
    LOG.info ("libraryPath " + libraryPath);
    LOG.info ("successOnly " + successOnly);
  }


  protected String getValue (Map<String,String> args, String name)
  {
    return PropertiesUtil.substituteProperty (get(args,name), null);
  }

  protected Voikko getVoikko() {return voikko;}


  private String language;
  private String path;
  private String libvoikkoPath;
  private String libraryPath;
  protected static final Logger LOG = LoggerFactory.getLogger (SukijaFilterFactory.class);
  protected Voikko voikko;
  protected boolean successOnly;
}

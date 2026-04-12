/*
Copyright (©) 2026 Hannu Väisänen

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

package peltomaa.sukija.indexer;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import peltomaa.sukija.schema.IndexerConfigurationType;
import peltomaa.sukija.schema.UnionType;


class SukijaFilenameFilter implements FilenameFilter {
    public SukijaFilenameFilter (IndexerConfigurationType configuration)
    {
        PROP = System.getProperty ("user.home");

        includesPattern  = compilePattern (configuration.getIncludes());
        excludesPattern  = compilePattern (configuration.getExcludes());

        includesExtension = compileExtension (configuration.getIncludes());
        excludesExtension = compileExtension (configuration.getExcludes());

if (includesPattern   != null) System.out.println ("includesPattern "   + includesPattern.pattern());
if (excludesPattern   != null) System.out.println ("excludesPattern "   + excludesPattern.pattern());
if (includesExtension != null) System.out.println ("includesExtension " + includesExtension.pattern());
if (excludesExtension != null) System.out.println ("excludesExtension " + excludesExtension.pattern());
     }


    public boolean accept (File dir, String name)
    {
        if (!acceptIncludes(name)) return false;

        if ((excludesPattern != null) && excludesPattern.matcher(name).matches()) return false;
        if ((excludesExtension != null) && excludesExtension.matcher(name).matches()) return false;


        try {  // Tyhjä tiedosto kaataa indeksointiohjelman.
            if (Files.size (Path.of(name)) == 0) return false;
        }
        catch (IOException e)
        {
//             e.printStackTrace (System.out);
             return false;
        }
        return true;
    }


    private boolean acceptIncludes (String name)
    {
        if ((includesPattern == null) && (includesExtension == null)) return true;
        if ((includesPattern != null) && includesPattern.matcher(name).matches()) return true;
        if ((includesExtension != null) && includesExtension.matcher(name).matches()) return true;
        return false;
    }


    private Pattern compilePattern (UnionType u)
    {
        if (u == null) {
            return null;
        }
        else if (u.getPattern() != null) {
            return Pattern.compile (parseString(u.getPattern()));
        }
        else {
            return null;
        }
    }


    private Pattern compileExtension (UnionType u)
    {
        if (u == null) {
            return null;
        }
        else if (u.getExtension().size() > 0) {
            return Pattern.compile (".*[.](" + String.join ("|", u.getExtension()) + ")$");
//          return Pattern.compile ("(?u)(?i).*[.](" + String.join ("|", u.getExtension()) + ")$");
        }
        else {
            return null;
        }
    }


    private String parseString (String s)
    {
        return s.replace ("${user.home}", PROP);
    }


    private final String PROP;
    private final Pattern includesPattern;
    private final Pattern excludesPattern;
    private final Pattern includesExtension;
    private final Pattern excludesExtension;
}

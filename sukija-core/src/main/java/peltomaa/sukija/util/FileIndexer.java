/*
Copyright (©) 2012 Hannu Väisänen

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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;

public abstract class FileIndexer {
  public FileIndexer (String pattern)
  {
    ignoreFilePattern = Pattern.compile (pattern);
  }


  public abstract void indexFile (File file) throws IOException;


  public void indexFiles (String[] fileName) throws IOException
  {
    for (String s: fileName) indexFiles (s);
  }


  public void indexFiles (Collection<String> fileName) throws IOException
  {
    for (String s: fileName) indexFiles (s);
  }


  private void indexFiles (String fileName) throws IOException
  {
    final File file = new File (fileName);
    if (!file.canRead()) return;

    if (file.isDirectory()) {
      for (File f: file.listFiles()) indexFiles (f.getCanonicalPath());
    }
    else if (!ignoreFilePattern.matcher(file.getCanonicalPath()).matches()) {
      indexFile (file);
    }
  }

  private Pattern ignoreFilePattern;
}

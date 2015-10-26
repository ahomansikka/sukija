/*
Copyright (©) 2014 Hannu Väisänen

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

package peltomaa.sukija;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;
import javax.activation.MimetypesFileTypeMap;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class FileDisplayer {
  public FileDisplayer()
  {
    try {
      map = new MimetypesFileTypeMap ("/etc/mime.types");
    }
    catch (Exception e)
    {
      System.out.println (e.getMessage());
      e.printStackTrace (System.out);
    }
  }


  public void showFile (String fileName) throws Exception
  {
    JFrame frame = new JFrame (fileName);
    JTextArea textArea = new JTextArea();
//System.out.println (textArea.getFont().toString());
    Font font = new Font (Font.MONOSPACED, Font.PLAIN, 20);
    textArea.setFont (font);
//System.out.println (textArea.getFont().toString());
    JScrollPane scrollPane = new JScrollPane (textArea);

    BufferedReader reader = new BufferedReader (new FileReader (fileName));
    String line;
    int maxWidth = 0;
    while ((line = reader.readLine()) != null) {
      maxWidth = Math.max (maxWidth, textArea.getFontMetrics(font).stringWidth(line));
      textArea.append (line + System.lineSeparator());
    }
    reader.close();

    frame.getContentPane().add (scrollPane);
    frame.pack();
    frame.setSize ((int)(1.1*maxWidth), 500);
    frame.setVisible (true);
    textArea.setEditable (false);
  }


  public void showFile (URL url) throws Exception
  {
    showFile (url.getFile());
  }

  private MimetypesFileTypeMap map;
}

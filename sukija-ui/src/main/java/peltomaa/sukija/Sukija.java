/*
Copyright (©) 2014-2015 Hannu Väisänen

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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import javax.swing.BoxLayout;
import javax.swing.AbstractAction;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.Font;
import com.l2fprod.common.swing.JFontChooser;


// Katso http://wiki.apache.org/solr/Solrj

public class Sukija extends JFrame {
  public static void main (String[] args)
  {
    try {
      BasicConfigurator.configure();

//      System.out.println (System.getProperty ("java.class.path") + "\n===\n");

      String url = "http://localhost:8983/solr/";

      Sukija sukija = new Sukija (url);
    }
    catch (Throwable t)
    {
      System.out.println (t.getMessage());
      t.printStackTrace (System.out);
    }
  }


  public Sukija (String url) throws SolrServerException
  {
    super ("Sukija");

    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();

    JPanel panel = new JPanel (layout);
    set (panel, layout, c, 0, 0, 0, makeForm());

    messageField = new JLabel ("Kirjoita etsittävä sana.");
    set (panel, layout, c, 0, 1, 0, messageField);

    editorPane = makeEditorPane();
    set (panel, layout, c, 0, 2, 1, new JScrollPane (editorPane));

    getContentPane().add (panel);
    setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
    setSize (900, 500);
    setVisible (true);

    client = new HttpSolrClient (url);

    query = new Query (client, messageField, editorPane);

//    JFontChooser chooser = new JFontChooser();
//    Font selectedFont = JFontChooser.showDialog (null, "Choose Font", null);
  }


  public class QueryX extends AbstractAction {
    public void actionPerformed (ActionEvent e)
    {
      query.query (queryField.getText());
    }
  }


  private JPanel makeForm()
  {
    JPanel panel = new JPanel();
    panel.setLayout (new BoxLayout (panel, BoxLayout.X_AXIS));
    panel.add (new JLabel ("Sana: "));
    queryField = new JTextField (20);
    panel.add (queryField);

    queryField.addActionListener (new QueryX());
    return panel;
  }


  private class LinkListener implements HyperlinkListener {
    public void hyperlinkUpdate (HyperlinkEvent e)
    {
      if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        try {
          fileDisplayer.showFile (e.getURL());
        }  
        catch (Exception ex)
        {
          messageField.setText (ex.getMessage());
        }
      }
    }
  }


  private void set (JPanel panel,
                    GridBagLayout gridbag,
                    GridBagConstraints c,
                    int x,
                    int y,
                    double weighty,
                    JComponent component)
  {
    c.ipadx = 10;
    c.fill = GridBagConstraints.BOTH;
    c.gridx = x;
    c.gridy = y;
    c.weightx = 1.0;
    c.weighty = weighty;
    gridbag.setConstraints (component, c);
    panel.add (component);
  }


  private JEditorPane makeEditorPane()
  {
    JEditorPane p = new JEditorPane ("text/html; charset=\"utf-8\"", "");
    p.setEditable (false);
    p.setEnabled (true);
    p.addHyperlinkListener (new LinkListener());
    return p;
  }


  private SolrClient client;
  private JTextField queryField;
  private JLabel messageField;
  private FileDisplayer fileDisplayer = new FileDisplayer();
  private JEditorPane editorPane;
  private Query query;
}

/*
String firstHighlight = response.getHighlighting().get(id).get("text").get(0);
firstHighlight = firstHighlightreplaceAll ("[\n\r]", "");
System.out.println ("Highlight: " + firstHighlight;

jar cfm sukija.jar MANIFEST.MF *.class
java -jar sukija.jar
jar cfe sukija.jar Sukija Sukija.class

*/

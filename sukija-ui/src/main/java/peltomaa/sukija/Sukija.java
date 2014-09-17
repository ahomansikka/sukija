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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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


// Katso http://wiki.apache.org/solr/Solrj

public class Sukija extends JFrame {
  public static void main (String[] args)
  {
    try {
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

    server = new HttpSolrServer (url);
  }


  public class Query extends AbstractAction {
    public void actionPerformed (ActionEvent e)
    {
      try {
        SolrQuery query = new SolrQuery();
        query.setQuery (queryField.getText());
        query.setParam ("rows", "1000");
        query.addTermsField ("text");
        query.setTerms (true);
        query.setParam ("hl.useFastVectorHighlighter", "true");
        query.setParam ("hl.mergeContiguous", "true");
//        query.setParam ("hl.fragsize", "0");
        query.setParam ("hl.maxAnalyzedChars", "-1");

        query.set ("qt", "/sukija"); // To choose a different request handler, for example, just set the qt parameter.
        QueryResponse response = server.query (query);

//        for (String s : query.getTermsFields()) System.out.println ("Field " + s);

//        QueryResponsePrinter.print (System.out, response);

        SolrDocumentList documents = response.getResults();

        if (documents == null) {
          messageField.setText ("Sukija.java: documents == null: ohjelmassa on virhe.");
          System.exit (1);
        }

        setMessage (documents.size());

        if (documents.size() == 0) {
          editorPane.setText ("");
        }
        else {
          editorPane.setText (getText (response));
          editorPane.setCaretPosition (0);
        }
      }
      catch (SolrServerException ex)
      {
        messageField.setText (ex.getMessage());
      }
      catch (Exception ex)
      {
        messageField.setText (ex.getMessage());
      }
    }
  }


  private void setMessage (int n)
  {
    if (n == 0) {
      messageField.setText ("Ei löytynyt.");
    }
    else if (n == 1) {
      messageField.setText (String.format ("Löytyi %d tiedosto.", n));
    }
    else {
      messageField.setText (String.format ("Löytyi %d tiedostoa.", n));
    }
  }


  private JPanel makeForm()
  {
    JPanel panel = new JPanel();
    panel.setLayout (new BoxLayout (panel, BoxLayout.X_AXIS));
    panel.add (new JLabel ("Sana: "));
    queryField = new JTextField (20);
    panel.add (queryField);

    queryField.addActionListener (new Query());
    return panel;
  }


  private void writeEntry (StringBuffer s, Map.Entry<String,Map<String,List<String>>> entry)
  {
    final String fileName = entry.getKey();
    final Map<String,List<String>> map = entry.getValue();

    s.append ("<a href=\"file://" + fileName + "\">" + fileName + "</a><p>\n");

    Iterator<Map.Entry<String,List<String>>> i = map.entrySet().iterator();

    while (i.hasNext()) {
      Map.Entry<String,List<String>> u = i.next();
      final List<String> list = (List<String>)u.getValue();
      for (String p : list) {
        s.append (p + "<p>");
      }
    }
    s.append ("<p>\n");
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


  private String getText (QueryResponse response)
  {
    Map<String,Map<String,List<String>>> hlMap = response.getHighlighting();
    Iterator<Map.Entry<String,Map<String,List<String>>>> i = hlMap.entrySet().iterator();

    StringBuffer s = new StringBuffer();
    s.append ("<html><style> body {font-size: 18px;} </style><body>\n");
    while (i.hasNext()) {
      writeEntry (s, i.next());
    }
    s.append ("</body></html>\n");
    return s.toString();
  }


  private SolrServer server;
  private JTextField queryField;
  private JLabel messageField;
  private FileDisplayer fileDisplayer = new FileDisplayer();
  private JEditorPane editorPane;
}

/*
String firstHighlight = response.getHighlighting().get(id).get("text").get(0);
firstHighlight = firstHighlightreplaceAll ("[\n\r]", "");
System.out.println ("Highlight: " + firstHighlight;

jar cfm sukija.jar MANIFEST.MF *.class
java -jar sukija.jar
jar cfe sukija.jar Sukija Sukija.class

*/

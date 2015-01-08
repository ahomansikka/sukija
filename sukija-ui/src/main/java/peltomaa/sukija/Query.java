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

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JLabel;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;


public class Query {
  public Query (SolrServer server, JLabel messageField, JEditorPane editorPane)
  {
    this.server = server;
    this.messageField = messageField;
    this.editorPane = editorPane;
  }


  public void query (String text)
  {
    try {
      SolrQuery query = new SolrQuery();
      query.setQuery (text);
      query.setParam ("rows", "1000");
      query.addTermsField ("text");
      query.setTerms (true);
      query.setParam ("hl.useFastVectorHighlighter", "true");
      query.setParam ("hl.mergeContiguous", "true");
//    query.setParam ("hl.fragsize", "0");
      query.setParam ("hl.maxAnalyzedChars", "-1");

      // To choose a different request handler, for example, just set the qt parameter.
      query.set ("qt", "/sukija");
      QueryResponse response = server.query (query);

//    for (String s : query.getTermsFields()) System.out.println ("Field " + s);
//    QueryResponsePrinter.print (System.out, response);

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


  private SolrServer server;
  private JLabel messageField;
  private JEditorPane editorPane;
}

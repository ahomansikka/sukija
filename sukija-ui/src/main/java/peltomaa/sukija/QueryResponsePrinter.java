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

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.common.util.NamedList;


/**
Testaustulostuksia.
*/
public class QueryResponsePrinter {
  private QueryResponsePrinter() {}

  public static final void print (PrintStream out, QueryResponse response)
  {
    NamedList<Object> nl = response.getHeader();
    out.println (nl.toString());

    TermsResponse tr = response.getTermsResponse();
    Map<String,List<TermsResponse.Term>> tmap = tr.getTermMap();
    Iterator<Map.Entry<String,List<TermsResponse.Term>>> i = tmap.entrySet().iterator();
    while (i.hasNext()) {
      Map.Entry<String,List<TermsResponse.Term>> e = i.next();
      out.println (e.getKey());
      for (TermsResponse.Term m : e.getValue()) {
        out.println (m.getTerm() + " " + m.getFrequency());
      }
    }
out.println ("=========================");
    List<TermsResponse.Term> tterms = tr.getTerms ("text");

for (TermsResponse.Term t : tr.getTerms ("text")) {
//  out.println (t.getTerm());
}


  }
}

/*
Iterator<Entry<String, Object>> termVectors =  ((NamedList) solrResponse.get("termVectors")).iterator();
    while(termVectors.hasNext()){
        Entry<String, Object> docTermVector = termVectors.next();
        for(Iterator<Entry<String, Object>> fi = ((NamedList)docTermVector.getValue()).iterator(); fi.hasNext(); ){
            Entry<String, Object> fieldEntry = fi.next();
            if(fieldEntry.getKey().equals("contents")){
                for(Iterator<Entry<String, Object>> tvInfoIt = ((NamedList)fieldEntry.getValue()).iterator(); tvInfoIt.hasNext(); ){
                    Entry<String, Object> tvInfo = tvInfoIt.next();
                    NamedList tv = (NamedList) tvInfo.getValue();
                    System.out.println("Vector Info: " + tvInfo.getKey() + " tf: " + tv.get("tf") + " df: " + tv.get("df") + " tf-idf: " + tv.get("tf-idf"));
                }
            }       
        }
    }
*/

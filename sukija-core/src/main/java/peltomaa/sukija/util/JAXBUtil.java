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

package peltomaa.sukija.util;


import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import peltomaa.sukija.schema.*;


/** Luetaan ja kirjoitetaan XML-dataa.
 */
public class JAXBUtil {
  private JAXBUtil() {}


  public static final <T> T unmarshal (String xmlFile, String xsdFile, String contextPath, ClassLoader classLoader)
    throws JAXBException, SAXException
  {
//    LOG.debug ("JAXBUtil.unmarshal (1).");
/*
    SchemaFactory sf = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = sf.newSchema (JAXBUtil.class.getResource (xsdFile));
    JAXBContext jc = JAXBContext.newInstance (contextPath, classLoader);
    Unmarshaller u = jc.createUnmarshaller();
    u.setSchema (schema);
*/
    Unmarshaller u = getUnmarshaller (xsdFile, contextPath, classLoader);

    @SuppressWarnings("unchecked")
    JAXBElement<T> v = (JAXBElement<T>)u.unmarshal (new File (xmlFile));
//    LOG.debug ("JAXBUtil.unmarshal (2).");
    return v.getValue();
  }


  public static final <T> T unmarshal (InputStream is, String xsdFile, String contextPath, ClassLoader classLoader)
    throws JAXBException, SAXException
  {
    Unmarshaller u = getUnmarshaller (xsdFile, contextPath, classLoader);

    @SuppressWarnings("unchecked")
    JAXBElement<T> v = (JAXBElement<T>)u.unmarshal (is);
//    LOG.debug ("JAXBUtil.unmarshal (2).");
    return v.getValue();
  }


  /** Kirjoitetaan XML-dataa.
   *
   * @param value  Data, joka kirjoitetaan.
   * @param contextPath
   * @param out    Paikka, johon kirjoitetaan.
   */
  public static final <T> void marshal (JAXBElement<T> value, String contextPath, OutputStream out, ClassLoader classLoader)
    throws JAXBException
  {
    JAXBContext jc = JAXBContext.newInstance (contextPath, classLoader);
    Marshaller m = jc.createMarshaller();
    m.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.marshal (value, out);
  }


  /** Kirjoitetaan XML-dataa.
   *
   * @param value  Data, joka kirjoitetaan.
   * @param contextPath
   * @param out    Paikka, johon kirjoitetaan.
   */

  public static final <T> void marshal (JAXBElement<T> value, String contextPath, Writer out, ClassLoader classLoader)
    throws JAXBException
  {
    JAXBContext jc = JAXBContext.newInstance (contextPath, classLoader);
    Marshaller m = jc.createMarshaller();
    m.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.marshal (value, out);
  }

//  private static final Logger LOG = LoggerFactory.getLogger (JAXBUtil.class);


  private static final Unmarshaller getUnmarshaller (String xsdFile, String contextPath, ClassLoader classLoader)
    throws JAXBException, SAXException
  {
    SchemaFactory sf = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = sf.newSchema (JAXBUtil.class.getResource (xsdFile));
    JAXBContext jc = JAXBContext.newInstance (contextPath, classLoader);
    Unmarshaller u = jc.createUnmarshaller();
    u.setSchema (schema);
    return u;
  }
}

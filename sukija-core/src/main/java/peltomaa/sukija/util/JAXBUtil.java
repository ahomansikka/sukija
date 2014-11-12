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
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;
import org.xml.sax.SAXException;


/** Luetaan ja kirjoitetaan XML-dataa.
 */
public class JAXBUtil {
  private JAXBUtil() {}


  /** Luetaan XML-dataa tiedostosta.
   *
   * @param xmlFile     Tiedosto, josta XML-data luetaan.
   * @param xsdFile     Luettavan datan schema-tiedosto.
   * @param contextPath
   */
  public static final <T> T unmarshal (String xmlFile, String xsdFile, String contextPath)
    throws JAXBException, SAXException
  {
    SchemaFactory sf = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = sf.newSchema (JAXBUtil.class.getResource (xsdFile));
    JAXBContext jc = JAXBContext.newInstance (contextPath);
    Unmarshaller u = jc.createUnmarshaller();
    u.setSchema (schema);

    @SuppressWarnings("unchecked")
    JAXBElement<T> v = (JAXBElement<T>)u.unmarshal (new File (xmlFile));
    return v.getValue();
  }


  /** Kirjoitetaan XML-dataa.
   *
   * @param value  Data, joka kirjoitetaan.
   * @param contextPath
   * @param out    Paikka, johon kirjoitetaan.
   */
  public static final <T> void marshal (JAXBElement<T> value, String contextPath, OutputStream out) throws JAXBException
  {
    JAXBContext jc = JAXBContext.newInstance (contextPath);
    Marshaller m = jc.createMarshaller();
    m.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.marshal (value, out);
  }
}

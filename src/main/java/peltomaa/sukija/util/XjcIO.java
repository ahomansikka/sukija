/*
Copyright (©) 2022 Hannu Väisänen

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;
import org.xml.sax.SAXException;


/**
Luokka, joka helpottaa XML-tiedostojen lukemista (unmarshal) ja
kirjoittamista (marshal), kun XML-tiedostojen määrittelyt (XSD-tiedostot)
on käännetty Java-luokiksi komennolla Xjc.<p>

Esimerkki:

<pre>
XjcIO&lt;Luokka&gt; io = new XjcIO&lt;Luokka&gt; (XSD_FILE, Luokka.class, ObjectFactory.class);
Luokka o = io.read ("/tiedosto.xml");
io.write (o, System.out);
</pre>

Luokka on Xjc:n tekemän luokan nimi.
*/
public final class XjcIO<T> {
  //
  // JAXBContext on säieturvallinen, mutta Marshaller ja Unmarshaller
  // eivät ole eli ne on parasta tehdä jokaisessa metodissa erikseen.
  // Schema voidaan jakaa eri säikeitten välillä.
  //
  private final Class<T> klass;
  private final QName qname;
  private final JAXBContext jaxbContext;
  private final Schema schema;


  /** Muodostin.

    @param xsdFile            Tiedosto, jossa XML-schema on. Sen pitää olla samassa jar-paketissa kuin luokka T.
    @param klass              Geneerisen parametrin T luokka, koska ei voi kirjoittaa T.class.
    @param objectFactoryClass Sen ObjectFactory'n luokka, jossa on metodi
                              {@code JAXBElement<T> create...(T value)}.
    @throws JAXBException     Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
    @throws SAXException      Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
  */
  public XjcIO (String xsdFile, Class<T> klass, Class<?> objectFactoryClass) throws JAXBException, SAXException
  {
    this.klass = klass;
    this.qname = makeQName (objectFactoryClass);
    this.jaxbContext = JAXBContext.newInstance (objectFactoryClass.getPackageName(), objectFactoryClass.getClassLoader());

    final URL url = objectFactoryClass.getResource (xsdFile);

    this.schema = makeSchema (url);
/*
System.out.println ("packageName " + objectFactoryClass.getPackageName());
System.out.println ("objectFactoryClass " + objectFactoryClass.getName());
System.out.println ("xsdFile " + xsdFile);
System.out.println ("context " + this.jaxbContext.toString());
System.out.println ("klass " + klass.getName());
System.out.println ("qname " + qname.toString());
System.out.println ("URL " + url.toString());
//System.exit (1);
*/
  }


  /** Muodostin.

    @param xsdURL             URL, josta XML-schema luetaan.
    @param klass              Geneerisen parametrin T luokka, koska ei voi kirjoittaa T.class.
    @param objectFactoryClass Sen ObjectFactory'n luokka, jossa on metodi
                              {@code JAXBElement<T> create...(T value)}.
    @throws JAXBException     Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
    @throws SAXException      Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
  */
  public XjcIO (URL xsdURL, Class<T> klass, Class<?> objectFactoryClass) throws JAXBException, SAXException
  {
    this.klass = klass;
    this.qname = makeQName (objectFactoryClass);
    this.jaxbContext = JAXBContext.newInstance (objectFactoryClass.getPackageName(), objectFactoryClass.getClassLoader());
    this.schema = makeSchema (xsdURL);

/*
System.out.println ("packageName " + objectFactoryClass.getPackageName());
System.out.println ("objectFactoryClass " + objectFactoryClass.getName());
System.out.println ("xsdFile " + xsdFile);
System.out.println ("context " + this.jaxbContext.toString());
System.out.println ("klass " + klass.getName());
System.out.println ("qname " + qname.toString());
System.exit (1);
*/
  }


  /** Muodostin.

    @param xsdFile            Tiedosto, jossa XML-schema on. Käytä tätä muodostinta,
                              jos tiedosto ei ole samassa jar-paketissa kuin luokka T.
    @param klass              Geneerisen parametrin T luokka, koska ei voi kirjoittaa T.class.
    @param objectFactoryClass Sen ObjectFactory'n luokka, jossa on metodi
                              {@code JAXBElement<T> create...(T value)}.
    @throws JAXBException     Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
    @throws SAXException      Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
  */
  public XjcIO (File xsdFile, Class<T> klass, Class<?> objectFactoryClass) throws JAXBException, SAXException
  {
    this.klass = klass;
    this.qname = makeQName (objectFactoryClass);
    this.jaxbContext = JAXBContext.newInstance (objectFactoryClass.getPackageName(), objectFactoryClass.getClassLoader());
    this.schema = makeSchema (xsdFile);
/*
System.out.println ("packageName " + objectFactoryClass.getPackageName());
System.out.println ("objectFactoryClass " + objectFactoryClass.getName());
System.out.println ("xsdFile " + xsdFile);
System.out.println ("context " + this.jaxbContext.toString());
System.out.println ("klass " + klass.getName());
System.out.println ("qname " + qname.toString());
System.exit (1);
*/
  }


  /** Luetaan XML-tiedosto.
   *
   * @param xmlFile XML-tiedoston nimi.
   * @return Luettu XML-data.
   * @throws JAXBException     Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   */
  public final T read (String xmlFile) throws JAXBException
  {
    Unmarshaller u = jaxbContext.createUnmarshaller();
    u.setSchema (schema);

    @SuppressWarnings("unchecked")
    JAXBElement<T> v = (JAXBElement<T>)u.unmarshal (new File (xmlFile));
    return v.getValue();
  }


  /** Luetaan XML-data merkkijonosta.
   *
   * @param xmlData XML-data, joka luetaan.
   * @return Luettu XML-data.
   * @throws JAXBException     Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   */
  public final T readFromString (String xmlData) throws JAXBException
  {
    Unmarshaller u = jaxbContext.createUnmarshaller();
    u.setSchema (schema);

    @SuppressWarnings("unchecked")
    JAXBElement<T> v = (JAXBElement<T>)u.unmarshal (new StreamSource (new StringReader (xmlData)));
    return v.getValue();
  }


  /** Luetaan XML-data.
   *
   * @param file XML-tiedosto, josta luetaan.
   * @return Luettu XML-data.
   * @throws JAXBException     Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   * @throws SAXException      Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   *
   */
  public final T read (File file) throws JAXBException, SAXException
  {
    Unmarshaller u = jaxbContext.createUnmarshaller();
    u.setSchema (schema);

    @SuppressWarnings("unchecked")
    JAXBElement<T> v = (JAXBElement<T>)u.unmarshal (file);
    return v.getValue();
  }


  /** Luetaan XML-data.
   *
   * @param is Objekti, josta luetaan.
   * @return Luettu XML-data.
   * @throws JAXBException     Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   * @throws SAXException      Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   *
   */
  public final T read (InputStream is) throws JAXBException, SAXException
  {
    Unmarshaller u = jaxbContext.createUnmarshaller();
    u.setSchema (schema);

    @SuppressWarnings("unchecked")
    JAXBElement<T> v = (JAXBElement<T>)u.unmarshal (is);
    return v.getValue();
  }


  /** Luetaan XML-data.
   *
   * @param reader Objekti, josta luetaan.
   * @return Luettu XML-data.
   * @throws JAXBException     Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   * @throws SAXException      Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   *
   */
  public final T read (Reader reader) throws JAXBException, SAXException
  {
    Unmarshaller u = jaxbContext.createUnmarshaller();
    u.setSchema (schema);

    @SuppressWarnings("unchecked")
    JAXBElement<T> v = (JAXBElement<T>)u.unmarshal (reader);
    return v.getValue();
  }


  /** Kirjoitetaan XML-data tiedostoon eli muutetaan Java-objekti XML:ksi.
   *
   * @param value XML-data, joka kirjoitetaan.
   * @param fileName Sen tiedoston nimi, johon kirjoitetaan.
   * @throws JAXBException     Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   * @throws SAXException      Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   * @throws FileNotFoundException Jos tiedostoa ei löydy.
   */
  public final void write (T value, String fileName) throws JAXBException, SAXException, FileNotFoundException
  {
    JAXBElement<T> element = new JAXBElement<T> (qname, klass, value);
    makeMarshaller().marshal (element, new FileOutputStream (fileName));
  }


  /** Kirjoitetaan XML-data tiedostoon eli muutetaan Java-objekti XML:ksi.
   *
   * @param value XML-data, joka kirjoitetaan.
   * @param file Tiedosto, johon kirjoitetaan.
   * @throws JAXBException     Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   * @throws SAXException      Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   * @throws FileNotFoundException Jos tiedostoa ei löydy.
   */
  public final void write (T value, File file) throws JAXBException, SAXException, FileNotFoundException
  {
    JAXBElement<T> element = new JAXBElement<T> (qname, klass, value);
    makeMarshaller().marshal (element, file);
  }


  /** Kirjoitetaan XML-data eli muutetaan Java-objekti XML:ksi.
   *
   * @param value XML-data, joka kirjoitetaan.
   * @param os Objekti, johon kirjoitetaan.
   * @throws JAXBException     Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   * @throws SAXException      Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   */
  public final void write (T value, OutputStream os) throws JAXBException, SAXException
  {
    JAXBElement<T> element = new JAXBElement<T> (qname, klass, value);
    makeMarshaller().marshal (element, os);
  }


  /** Kirjoitetaan XML-data eli muutetaan Java-objekti XML:ksi.
   *
   * @param value XML-data, joka kirjoitetaan.
   * @param writer Objekti, johon kirjoitetaan.
   * @throws JAXBException     Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   * @throws SAXException      Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   */
  public final void write (T value, Writer writer) throws JAXBException, SAXException
  {
    JAXBElement<T> element = new JAXBElement<T> (qname, klass, value);
    makeMarshaller().marshal (element, writer);
  }


  /** Muutetaan Java-objekti 'value' XML-merkkijonoksi.
   *
   * @param value Java-objekti joka muutetaan.
   * @return XML-data muutettuna merkkijonoksi.
   * @throws JAXBException     Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   * @throws SAXException      Jos jokin menee pieleen (katso JAXB:n dokumenttia :-).
   */
  public final String toXML (T value) throws JAXBException, SAXException
  {
    final StringWriter stringWriter = new StringWriter();
    write (value, stringWriter);
    return stringWriter.toString();    
  }


  private final QName makeQName (Class<?> objectFactoryClass)
  {
    final Method createMethod = getCreateMethod (objectFactoryClass);
    final Annotation[] annotation = createMethod.getDeclaredAnnotations();
    if (annotation.length != 1) throw new RuntimeException ("annotation.length != 1");
    final XmlElementDecl xed = (XmlElementDecl)annotation[0];
    return new QName (xed.namespace(), xed.name());
  }


  private final Method getCreateMethod (Class<?> objectFactoryClass)
  {
    final Method[] list = objectFactoryClass.getDeclaredMethods();
    for (Method m : list) {
      final Class<?> p[] = m.getParameterTypes();
      for (int i = 0; i < p.length; i++) {
        if (p[i].toString().startsWith ("class ") &&
            (klass.getName().compareTo (p[i].toString().substring(6)) == 0)) {
          return m;
        }
      }
    }
    throw new RuntimeException ("Luokassa " + objectFactoryClass.getName() + " ei ole metodia, " +
                                "jonka parametri on tyyppiä " + klass.getName() + ".");
  }



  private final synchronized Marshaller makeMarshaller() throws JAXBException, SAXException
  {
    Marshaller m = jaxbContext.createMarshaller();
    m.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.setSchema (schema);
    return m;
  }


  private final synchronized Schema makeSchema (URL xsdURL) throws SAXException
  {
    SchemaFactory sf = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
    return sf.newSchema (xsdURL);
  }


  private final synchronized Schema makeSchema (File xsdFile) throws SAXException
  {
    SchemaFactory sf = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
    return sf.newSchema (xsdFile);
  }


  private static final long serialVersionUID = 1L;
}

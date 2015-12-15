/*
Copyright (©) 2015 Hannu Väisänen

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


/* Tämä tiedosto voidaan kääntää komennolla
   javac SukijaAsennus.java
*/

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import static java.nio.file.StandardCopyOption.*;


public class SukijaAsennus {

  public static void main (String[] args)
  {
    try {
      SukijaAsennus a = new SukijaAsennus (args);
    }
    catch (Throwable t)
    {
      System.out.println (t.getMessage());
      t.printStackTrace (System.out);
    }
  }


  private SukijaAsennus (String[] args) throws Throwable
  {
     final String propertiesFile = (args.length > 0) ? args[0] : SUKIJA_PROPERTIES;

     Properties p = new Properties();
     p.load (new FileReader (propertiesFile));

     final String SUKIJA = getProperty (p, "sukija.sukija");
     makeDirectory (SUKIJA + "/conf");
     makeDirectory (SUKIJA + "/data");
     makeDirectory (SUKIJA + "/lib");

//     Set<String> set = p.stringPropertyNames();
//     for (String s : set) System.out.println (s + " " + p.getProperty(s) + " " + getProperty(p,s));
//     System.exit (1);
//     p.list (System.out); System.exit (1);
//     printDataConfigFile (p, new OutputStreamWriter (System.out));
//     printSchemaFile (p, new OutputStreamWriter (System.out));

     printDataConfigFile (p, new FileWriter ("conf/data-config.xml"));
     printSchemaFile     (p, new FileWriter ("conf/schema.xml"));

     copyFile ("conf2/sukija-context.xml",
               getProperty (p, "sukija.jetty") + "/sukija-context.xml");
     copyDirectory (new File ("conf"),
                    new File (SUKIJA + "/conf"));
  }


  private void copyFile (String from, String to) throws IOException
  {
//System.out.println ("COPY2 " + from + " " + to);
    Files.copy (Paths.get(from), Paths.get(to), REPLACE_EXISTING);
  }


  private void copyDirectory (File from, File to) throws IOException
  {
    if (from.isDirectory()) {
//System.out.println ("COPY1 " + from.toString() + " " + to.toString());
      if (!to.exists()) {
        to.mkdir();
      }
      String[] files = from.list();
      for (String f : files) {
        copyDirectory (new File (from, f), new File (to, f));
      }
    }
    else {
      copyFile (from.getPath(), to.getPath());
    }
  }


  private boolean makeDirectory (String file)
  {
    File f = new File (file);
    return f.mkdirs();
  }


  private void printDataConfigFile (Properties p, Writer out) throws IOException
  {
    final String[] BASE_DIR = getProperty(p,"sukija.baseDir","${user.home}/Asiakirjat").split(PATH_SEPARATOR);
//for (String q : BASE_DIR) System.out.println(q);System.exit(1);

    out.write ("<dataConfig>\n" +
               "   <dataSource name=\"bin\" type=\"BinFileDataSource\"/>\n" +
               "   <document>\n");

    for (int i = 0; i < BASE_DIR.length; i++) {
      if (!directoryOK (BASE_DIR[i])) {
        throw new RuntimeException (BASE_DIR[i] + " ei ole olemassa tai se ei ole hakemisto.");
      }
      out.write (String.format (dataConfigEntity,
                                i,
                                BASE_DIR[i],
                                getProperty (p, "sukija.fileName",  ".*"),
                                getProperty (p, "sukija.excludes",  "(?u)(?i).*[.]jpg$"),
                                getProperty (p, "sukija.recursive", "true"),
                                i));
    }
    out.write ("  </document>\n" +
               "</dataConfig>\n");

    out.flush();
  }


  private void printSchemaFile (Properties p, Writer out) throws IOException
  {
    final String TOKENIZER = getProperty (p, "sukija.Tokenizer", "peltomaa.sukija.finnish.FinnishTokenizerFactory");
    printTokenizer (p, TOKENIZER, out);

    final String HYPHEN_FILTER = getProperty (p, "sukija.HyphenFilter");
    if (HYPHEN_FILTER != null) {
      out.write (String.format ("        <filter class=\"%s\"/>\n", HYPHEN_FILTER));
    }

    out.write (getBaseFormFilter (p));
    out.write (FINNISH_FOLDING_LOWER_CASE_FILTER);
    if (TOKENIZER.indexOf ("VoikkoTokenizerFactory") >= 0) {
     out.write (VOIKKO_TOKENIZER_TRIM_FILTER);
    }

    final String SYNONYM_FILTER = getSynonymFilter (p);
    if (SYNONYM_FILTER != null) {
      out.write (SYNONYM_FILTER);
    }
    out.write (schemaFileEnd);
    out.flush();
  }


  private void printTokenizer (Properties p, String tokenizer, Writer out) throws IOException
  {
    StringBuilder sb = new StringBuilder();

    sb.append (String.format (schemaFileStart, tokenizer));
    if (tokenizer.indexOf ("VoikkoTokenizerFactory") >= 0) {
      appendVoikkoProperties (sb, p);
      append (sb, p, "ignoreNL", "sukija.voikko.tokenizer.ignoreNL", "true");
    }
    out.write (sb.toString());
    out.flush();
  }


  private void appendVoikkoProperties (StringBuilder sb, Properties p)
  {
    append (sb, p, "language", "sukija.voikko.language", "fi");
    append_if (sb, p, "path",           "sukija.voikko.path");
    append_if (sb, p, "libvoikkoPath",  "sukija.voikko.libvoikkoPath");
    append_if (sb, p, "libraryPath",    "sukija.voikko.libraryPath");
  }


  private String getBaseFormFilter (Properties p)
  {
    final String FACTORY = "peltomaa.sukija.baseform.BaseFormFilterFactory";

    StringBuilder sb = new StringBuilder();

    sb.append (String.format ("        <filter class=\"%s\"", FACTORY));
    appendVoikkoProperties (sb, p);
    sb.append ("/>\n");
    return sb.toString();
  }


  private String getSynonymFilter (Properties p)
  {
    final String synonymFile = getProperty (p, "sukija.synonyms");
    if (synonymFile == null) return null;

    StringBuilder sb = new StringBuilder ("        <filter class=\"solr.SynonymFilterFactory\"");
    append (sb, "synonyms", synonymFile);
    append (sb, p, "ignoreCase", "sukija.ignoreCase", "true");
    append (sb, p, "expand", "sukija.expand", "false");
    sb.append ("/>\n");
    return sb.toString();
  }


  private void append_if (StringBuilder sb, Properties p, String argument, String key)
  {
    final String s = getProperty (p, key);
    if (s != null) {
      append (sb, argument, s);
    }
  }


  private void append (StringBuilder sb, Properties p, String argument, String key, String def)
  {
    append (sb, argument, getProperty (p, key, def));
  }


  private void append (StringBuilder sb, String argument, String value)
  {
    sb.append (String.format (" %s = \"%s\"", argument, value));
  }



  private String findProperty (Properties p, String s)
  {
    String u = p.getProperty (s);
    if (u == null) {
      return systemProperties.getProperty (s);
    }
    else {
      return u;
    }
  }



  // Korvataan ympäristömuuttujat.
  //
  private String replace (Properties p, String s)
  {
    Matcher m = PATTERN.matcher (s);
    if (m.find()) {
      String u = findProperty (p, m.group(1));
      if (u == null) {
        throw new RuntimeException ("Ominaisuutta " + m.group(1) + " ei ole määritelty.");
      }
      return replace (p, s.replace(m.group(),u));
    }
    else {
      return s;
    }
  }


  private String getProperty (Properties p, String key)
  {
    final String s = p.getProperty (key);
    return (s == null) ? null : replace (p, s);
  }


  private String getProperty (Properties p, String key, String def)
  {
    final String s = p.getProperty (key);
    return (s == null) ? replace (p, def) : replace (p, s);
  }


  private boolean directoryOK (String fileName)
  {
    final File file = new File (fileName);
    return (file.exists() && file.isDirectory());
  }


  private static final String PATH_SEPARATOR = System.getProperty ("path.separator");
  private static final String SUKIJA_PROPERTIES = "sukija.properties";

  // Jos merkkijono on "${user.home}/.sukija", tämä tunnistaa osan "${user.home}"
  // ja ulommaiset sulut (group(1)) osan "user.home".
  private static final Pattern PATTERN = Pattern.compile ("[$][{]((\\p{L}|[-.0-9])+)[}]");

  private static final Properties systemProperties = System.getProperties();
  private static final FileSystem fileSystem = FileSystems.getDefault();

  private static final String dataConfigEntity =
    "    <entity name = \"f%d\"\n" +
    "            dataSource = \"null\"\n" +
    "            rootEntity = \"false\"\n" +
    "            processor = \"solr.handler.dataimport.FileListEntityProcessor\"\n" +
    "            baseDir = \"%s\"\n" +
    "            fileName = \"%s\"\n" +
    "            excludes = \"%s\"\n" +
    "            recursive = \"%s\">\n" +
    "      <field column = \"fileAbsolutePath\" meta = \"true\" name = \"id\"/>\n" +
    "      <entity name = \"tika\"\n" +
    "              dataSource = \"bin\"\n" +
    "              processor = \"solr.handler.dataimport.TikaEntityProcessor\"\n" +
    "              url = \"${f%d.fileAbsolutePath}\"\n" +
    "              format = \"text\"\n" +
    "              onError = \"skip\">\n" +
    "      </entity>\n" +
    "    </entity>\n";

//    "              processor = \"peltomaa.sukija.util.TikaEntityProcessor\"\n" +

  private static final String schemaFileStart =
    "<schema name=\"sukija\" version=\"1.0\">\n" +
    "  <fields>\n" +
    "    <field name=\"_version_\" type=\"long\" indexed=\"true\" stored=\"true\" multiValued=\"false\"/>\n" +
    "    <field name=\"id\" type=\"string\" indexed=\"true\" stored=\"true\" required=\"true\" multiValued=\"false\"/>\n" +
    "    <field name=\"text\" type=\"text\" indexed=\"true\" stored=\"true\" termOffsets=\"true\" termPositions=\"true\" termVectors=\"true\" multiValued=\"true\"/>\n" +
    "  </fields>\n" +
    "  <defaultSearchField>text</defaultSearchField>\n" +
    "  <uniqueKey>id</uniqueKey>\n" +
    "  <types>\n" +
    "    <fieldType name=\"long\" class=\"solr.TrieLongField\" precisionStep=\"0\" positionIncrementGap=\"0\"/>\n" +
    "    <fieldType name=\"string\" class=\"solr.StrField\" sortMissingLast=\"true\"/>\n" +
    "    <fieldType name=\"text\" class=\"solr.TextField\" positionIncrementGap=\"1\">\n" +
    "      <analyzer type=\"index\">\n" +
    "        <tokenizer class=\"%s\"/>\n";

  private static final String FINNISH_FOLDING_LOWER_CASE_FILTER =
    "        <!-- Tämä luokka tietää, että å, ä ja ö eivät ole aksentillisia merkkejä. -->\n" +
    "        <filter class=\"peltomaa.sukija.finnish.FinnishFoldingLowerCaseFilterFactory\"/>\n";

  private static final String VOIKKO_TOKENIZER_TRIM_FILTER =
    "        <filter class=\"peltomaa.sukija.voikko.tokenizer.VoikkoTokenizerTrimFilterFactory\"/>\n";

  private static final String schemaFileEnd =
    "      </analyzer>\n" +
    "      <analyzer type=\"query\">\n" +
    "        <tokenizer class=\"solr.WhitespaceTokenizerFactory\"/>\n" +
    "        <filter class=\"peltomaa.sukija.finnish.FinnishFoldingLowerCaseFilterFactory\"/>\n" +
    "      </analyzer>\n" +
    "    </fieldType>\n" +
    "  </types>\n" +
    "  <solrQueryParser defaultOperator=\"AND\"/>\n" +
    "</schema>\n";
}

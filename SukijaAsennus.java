/*
Copyright (©) 2015-2018, 2020-2021 Hannu Väisänen

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
import java.io.Writer;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
      if (t.getCause() != null) {
        System.out.println ("Cause" + t.getCause().getMessage());
      }
    }
  }


  private SukijaAsennus (String[] args) throws Throwable
  {
    final String propertiesFile = (args.length > 0) ? args[0] : SUKIJA_PROPERTIES;

    Properties p = new Properties();
    p.load (new FileReader (propertiesFile));
    printDataConfigFile (p, new FileWriter ("conf/data-config.xml"));
    printSchemaFile     (p, new FileWriter ("conf/schema.xml"));
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
//      throw new RuntimeException (BASE_DIR[i] + " ei ole olemassa tai se ei ole hakemisto.");
        System.out.println (BASE_DIR[i] + " ei ole olemassa tai se ei ole hakemisto.");
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
    printTokenizer (p, out);

    if (getProperty (p, "sukija.suggestionFile") == null) {
      printBaseFormFilter (p, out);
    }
    else {
      printSuggestionFilter (p, out);
    }
    out.write (FINNISH_FOLDING_LOWER_CASE_FILTER);
//    if (TOKENIZER.indexOf ("VoikkoTokenizerFactory") >= 0) {
//      out.write (VOIKKO_TOKENIZER_TRIM_FILTER);
//    }

    final String WHEN = getSynonymTime (p);

    if ((WHEN != null) && (WHEN.compareTo ("indexing") == 0)) {
      final String FILTER = getSynonymFilter (p, true);
      if (FILTER != null) {
        out.write (FILTER);
      }
    }
    printSchemaFileEnd (WHEN, p, out);
    out.flush();
  }


  private void printTokenizer (Properties p, Writer out) throws IOException
  {
    final String TOKENIZER = getProperty (p, "sukija.Tokenizer", "peltomaa.sukija.finnish.FinnishTokenizerFactory");

    StringBuilder sb = new StringBuilder();

    sb.append (String.format (schemaFileStart, TOKENIZER));
    if (TOKENIZER.indexOf ("VoikkoTokenizerFactory") >= 0) {
      appendVoikkoProperties (sb, p);
      append (sb, p, "ignoreNL", "sukija.voikko.tokenizer.ignoreNL", "true");
    }
    out.write (sb.toString());
    if (TOKENIZER.compareTo ("peltomaa.sukija.finnish.HVTokenizerFactory") == 0) {
      out.write ("        <filter class=\"peltomaa.sukija.filters.LaTeXFilterFactory\"/>\n");
    }
    out.flush();
  }


  private void appendVoikkoProperties (StringBuilder sb, Properties p)
  {
    append (sb, p, "language", "sukija.voikko.language", "fi");
    append_if (sb, p, "path",           "sukija.voikko.path");
    append_if (sb, p, "libvoikkoPath",  "sukija.voikko.libvoikkoPath");
    append_if (sb, p, "libraryPath",    "sukija.voikko.libraryPath");

    for (String option : voikkoOption) {
      final String s = getProperty (p, option);
      if (s != null) {
        if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false")) {
          append (sb, option.substring(21), s);
        }
        else {
          throw new RuntimeException ("Väärä parametrin " + option + " arvo " + s + " (pitää olla true tai false).");
        }
      }
    }
  }


  private static final String[] voikkoOption = {
    "sukija.voikko.option.ignore_dot",
    "sukija.voikko.option.ignore_numbers",
    "sukija.voikko.option.ignore_uppercase",
    "sukija.voikko.option.ignore_nonwords",
    "sukija.voikko.option.accept_first_uppercase",
    "sukija.voikko.option.accept_all_uppercase",
    "sukija.voikko.option.accept_extra_hyphens",
    "sukija.voikko.option.accept_missing_hyphens"
  };


  private void printBaseFormFilter (Properties p, Writer out) throws IOException
  {
    final String FACTORY = "peltomaa.sukija.baseform.BaseFormFilterFactory";

    StringBuilder sb = new StringBuilder();

    sb.append (String.format ("        <filter class=\"%s\"", FACTORY));
    append_if (sb, p, "successOnly", "sukija.successOnly");
    appendVoikkoProperties (sb, p);
    sb.append ("/>\n");
    out.write (sb.toString());
    out.flush();
  }


  private void printSuggestionFilter (Properties p, Writer out) throws IOException
  {
    final String FACTORY = "peltomaa.sukija.suggestion.SuggestionFilterFactory";

    StringBuilder sb = new StringBuilder();

    sb.append (String.format ("        <filter class=\"%s\"", FACTORY));
    append_if (sb, p, "suggestionFile", "sukija.suggestionFile");
    append_if (sb, p, "successOnly",    "sukija.successOnly");
    appendVoikkoProperties (sb, p);
    sb.append ("/>\n");
    out.write (sb.toString());
    out.flush();
  }


  private void printSchemaFileEnd (String when, Properties p, Writer out) throws IOException
  {
    out.write (schemaFileEnd1);

    if ((when != null) && (when.compareTo ("query") == 0)) {
      final String FILTER = getSynonymFilter (p, false);
      if (FILTER != null) {
        out.write (FILTER);
      }
    }
    out.write (schemaFileEnd2);
  }


  private String getSynonymTime (Properties p)
  {
    final String when = getProperty (p, "sukija.synonyms.when");
    if (when != null) {
      if ((when.compareTo ("indexing") != 0) && (when.compareTo ("query") != 0)) {
        throw new RuntimeException ("sukija.synonyms.when pitää olla joko indexing tai query (on " + when + ").");
      }
    }
    return when;
  }


  private String getSynonymFilter (Properties p, boolean analysisTime)
  {
    final String synonymFile = getProperty (p, "sukija.synonyms.file");
    if (synonymFile == null) return null;


    StringBuilder sb = new StringBuilder ("        <filter class=\"solr.SynonymGraphFilterFactory\"");
    append (sb, "synonyms", synonymFile);
    append (sb, p, "ignoreCase", "sukija.synonyms.ignoreCase", "true");
    append (sb, p, "expand", "sukija.synonyms.expand", "false");
    sb.append ("/>\n");
    if (analysisTime) {
      sb.append ("        <filter class=\"solr.FlattenGraphFilterFactory\"/>\n");
    }
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
//    if (s == null) {
//      throw new RuntimeException ("Ominaisuutta " + key + " ei ole.");
//    }
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

  // Jos merkkijono on "${user.home}/jotain/muuta", koko lauseke tunnistaa
  // osan "${user.home}" ja ulommaiset sulut (group(1)) osan "user.home".
  private static final Pattern PATTERN = Pattern.compile ("[$][{]((\\p{L}|[-.0-9])+)[}]");

  private static final Properties systemProperties = System.getProperties();

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
    "              tikaConfig = \"tika-config.xml\"\n" +
    "              url = \"${f%d.fileAbsolutePath}\"\n" +
    "              format = \"text\"\n" +
    "              onError = \"abort\">\n" +
//    "              <field column = \"Content-Type\" name = \"contentType\" meta = \"true\"/>\n" +
    "      </entity>\n" +
    "    </entity>\n";


  private static final String schemaFileStart =
    "<schema name=\"sukija\" version=\"1.0\">\n" +
    "  <fields>\n" +
    "    <field name=\"_version_\" type=\"long\" indexed=\"true\" stored=\"true\" multiValued=\"false\"/>\n" +
    "    <field name=\"id\" type=\"string\" indexed=\"true\" stored=\"true\" required=\"true\" multiValued=\"false\"/>\n" +
    "    <field name=\"text\" type=\"text\" indexed=\"true\" stored=\"true\" termOffsets=\"true\" termPositions=\"true\" termVectors=\"true\" multiValued=\"true\"/>\n" +
//    "    <field name=\"contentType\" type=\"text\" indexed=\"true\" stored=\"true\"/>\n" +
    "  </fields>\n" +
    "  <uniqueKey>id</uniqueKey>\n" +
    "  <types>\n" +
    "    <fieldType name=\"long\" class=\"solr.TrieLongField\" precisionStep=\"0\" positionIncrementGap=\"0\"/>\n" +
    "    <fieldType name=\"string\" class=\"solr.StrField\" sortMissingLast=\"true\"/>\n" +
    "    <fieldType name=\"text\" class=\"solr.TextField\" positionIncrementGap=\"1\">\n" +
    "      <analyzer type=\"index\">\n" +
    "        <tokenizer class=\"%s\"/>\n";


  private static final String LATEX_FILTER =
    "        <filter class=\"peltomaa.sukija.filters.LaTeXFilterFactory\"/>\n";


  private static final String FINNISH_FOLDING_LOWER_CASE_FILTER =
    "        <!-- Tämä luokka tietää, että å, ä ja ö eivät ole aksentillisia merkkejä. -->\n" +
    "        <filter class=\"peltomaa.sukija.finnish.FinnishFoldingLowerCaseFilterFactory\"/>\n";

  private static final String VOIKKO_TOKENIZER_TRIM_FILTER =
    "        <filter class=\"peltomaa.sukija.voikko.tokenizer.VoikkoTokenizerTrimFilterFactory\"/>\n";

  private static final String schemaFileEnd1 =
    "      </analyzer>\n" +
    "      <analyzer type=\"query\">\n" +
    "        <tokenizer class=\"solr.WhitespaceTokenizerFactory\"/>\n" +
    "        <filter class=\"peltomaa.sukija.finnish.FinnishFoldingLowerCaseFilterFactory\"/>\n";

  private static final String schemaFileEnd2 =
    "      </analyzer>\n" +
    "    </fieldType>\n" +
    "  </types>\n" +
    "</schema>\n";
}

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


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;


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

//     p.list (System.out);
//     printDataConfigFile (p, new OutputStreamWriter (System.out));
//     printSchemaFile (p, new OutputStreamWriter (System.out));

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
    out.write (String.format (schemaFileStart, getProperty (p, "sukija.Tokenizer", "peltomaa.sukija.finnish.FinnishTokenizerFactory")));

    final String HYPHEN_FILTER = getProperty (p, "sukija.HyphenFilter");
    if (HYPHEN_FILTER != null) {
      out.write (String.format ("        <filter class=\"%s\"/>\n", HYPHEN_FILTER));
    }

    out.write (getMorphologyFilter (p));

    final String SYNONYM_FILTER = getSynonymFilter (p);
    if (SYNONYM_FILTER != null) {
      out.write (SYNONYM_FILTER);
    }
    out.write (schemaFileEnd);
    out.flush();
  }


  private String getMorphologyFilter (Properties p)
  {
    final String MORPHOLOGY = getProperty (p, "sukija.Morphology", "peltomaa.sukija.voikko.VoikkoMorphologySuggestionFilterFactory");

    StringBuilder sb = new StringBuilder();

    sb.append (String.format ("        <filter class=\"%s\"", MORPHOLOGY));

    if (MORPHOLOGY.indexOf ("Malaga") >= 0) {
      append (sb, p, "malagaProjectFile", "sukija.malagaProjectFile", "${user.home}/.sukija/suomi.pro");
    }
    else if (MORPHOLOGY.indexOf ("Voikko") >= 0) {
      append (sb, p, "dictionary", "sukija.voikko.dictionary", "fi");
      append_if (sb, p, "path",          "sukija.voikko.path");
      append_if (sb, p, "libvoikkoPath", "sukija.voikko.libvoikkoPath");
      append_if (sb, p, "libraryPath",   "sukija.voikko.libraryPath");
    }

    if (MORPHOLOGY.indexOf ("Suggestion") >= 0) {
      append (sb, p, "suggestionFile", "sukija.suggestionFile", "suggestions.xml");
      append (sb, p, "success", "sukija.success", "false");
    }
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


  private String getProperty (Properties p, String key)
  {
    final String s = p.getProperty (key);
    return (s == null) ? null : s.replace ("${user.home}", HOME);
  }


  private String getProperty (Properties p, String key, String def)
  {
    final String s = p.getProperty (key);
    return (s == null) ? def.replace ("${user.home}", HOME) : s.replace ("${user.home}", HOME);
  }


  private static final String HOME = System.getProperty ("user.home");
  private static final String PATH_SEPARATOR = System.getProperty ("path.separator");
  private static final String SUKIJA_PROPERTIES = "sukija.properties";


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
    "              processor = \"peltomaa.sukija.util.TikaEntityProcessor\"\n" +
    "              url = \"${f%d.fileAbsolutePath}\"\n" +
    "              format = \"text\"\n" +
    "              onError = \"skip\">\n" +
    "      </entity>\n" +
    "    </entity>\n";


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

  private static final String schemaFileEnd =
    "        <!-- Tämä luokka tietää, että å, ä ja ö eivät ole aksentillisia merkkejä. -->\n" +
    "        <filter class=\"peltomaa.sukija.finnish.FinnishFoldingLowerCaseFilterFactory\"/>\n" +
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

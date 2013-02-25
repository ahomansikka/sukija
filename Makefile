CONFIG_DIR=conf
SCHEMA_XML=$(CONFIG_DIR)/schema.xml
SCHEMA_XML_IN=$(CONFIG_DIR)/schema.xml.in

FINNISH_TOKENIZER_FACTORY=peltomaa.sukija.finnish.FinnishTokenizerFactory
HV_TOKENIZER_FACTORY=peltomaa.sukija.finnish.HVTokenizerFactory

MALAGA_MORPHOLOGY_FILTER_FACTORY=filter class="peltomaa.sukija.malaga.MalagaMorphologyFilterFactory"\
                                 malagaProjectFile="$${user.home}/.sukija/suomi.pro"

MALAGA_MORPHOLOGY_SUGGESTION_FILTER_FACTORY=filter class="peltomaa.sukija.malaga.MalagaMorphologySuggestionFilterFactory"\
                                 malagaProjectFile="$${user.home}/.sukija/suomi.pro"\
                                 suggestionFile="$${user.home}/.sukija/suggestion.txt"

VOIKKO_MORPHOLOGY_FILTER_FACTORY=filter class="peltomaa.sukija.voikko.VoikkoMorphologyFilterFactory\
                                 dictionary="fi"

VOIKKO_MORPHOLOGY_SUGGESTION_FILTER_FACTORY=filter class="peltomaa.sukija.voikko.VoikkoMorphologyFilterFactory\
                                            dictionary="fi"\
                                            suggestionFile="$${user.home}/.sukija/suggestion.txt"


TOKENIZER_FACTORY=$(FINNISH_TOKENIZER_FACTORY)


malaga-schema:
	sed -e 's%TOKENIZER_FACTORY%$(TOKENIZER_FACTORY)%g' \
	    -e 's%MORPHOLOGY_FILTER_FACTORY%$(MALAGA_MORPHOLOGY_FILTER_FACTORY)%g' $(SCHEMA_XML_IN) >$(SCHEMA_XML)

malaga-suggestion-schema:
	sed -e 's%TOKENIZER_FACTORY%$(TOKENIZER_FACTORY)%g' \
	    -e 's%MORPHOLOGY_FILTER_FACTORY%$(MALAGA_MORPHOLOGY_SUGGESTION_FILTER_FACTORY)%g' $(SCHEMA_XML_IN) >$(SCHEMA_XML)

voikko-schema:
	sed -e 's%TOKENIZER_FACTORY%$(TOKENIZER_FACTORY)%g' \
	    -e 's%MORPHOLOGY_FILTER_FACTORY%$(VOIKKO_MORPHOLOGY_SUGGESTION_FILTER_FACTORY)%g' $(SCHEMA_XML_IN) >$(SCHEMA_XML)

voikko-suggestion-schema:
	sed -e 's%TOKENIZER_FACTORY%$(TOKENIZER_FACTORY)%g' \
	    -e 's%MORPHOLOGY_FILTER_FACTORY%$(VOIKKO_MORPHOLOGY_FILTER_FACTORY)%g' $(SCHEMA_XML_IN) >$(SCHEMA_XML)

debug-schema:
	sed -e 's%TOKENIZER_FACTORY%$(HV_TOKENIZER_FACTORY)%g' \
	    -e 's%MORPHOLOGY_FILTER_FACTORY%$(MALAGA_MORPHOLOGY_SUGGESTION_FILTER_FACTORY)%g' $(SCHEMA_XML_IN) >$(SCHEMA_XML)


JNA=${HOME}/.m2/repository/net/java/dev/jna/jna/3.4.0/jna-3.4.0.jar
LIBVOIKKO=${HOME}/.m2/repository/org/puimula/libvoikko/3.2/libvoikko-3.2.jar
SOLR_HOME=${HOME}/Lataukset/apache-solr-3.6.1/example/solr
JETTY_CONTEXTS_DIR=${HOME}/Lataukset/apache-solr-3.6.1/example/contexts
SUKIJA_HOME=${HOME}/.sukija
CORE_JAR=sukija-core/target/*jar
MALAGA_JAR=sukija-malaga/target/*jar
VOIKKO_JAR=sukija-voikko/target/*jar


BASE_DIR=$(HOME)/Asiakirjat
FILE_NAME=.*
EXCLUDES=(?u)(?i).*[.](au|bmp|bz2|class|gif|gpg|gz|jar|jpg|jpeg|m|o|png|tif|tiff|wav|zip)$$

install:
	if [ ! -e ${SOLR_HOME}/conf/schema.xml.orig ]; then \
	  cp ${SOLR_HOME}/conf/schema.xml ${SOLR_HOME}/conf/schema.xml.orig; \
	fi
	if [ ! -e ${SOLR_HOME}/conf/solrconfig.xml.orig ]; then \
	  cp ${SOLR_HOME}/conf/solrconfig.xml ${SOLR_HOME}/conf/solrconfig.xml.orig; \
	fi
	if [ ! -e ${JETTY_CONTEXTS_DIR} ]; then \
	  mkdir ${JETTY_CONTEXTS_DIR}; \
	fi
	cp ${CONFIG_DIR}/sukija-context.xml ${JETTY_CONTEXTS_DIR}
	cp ${CORE_JAR} ${MALAGA_JAR} ${VOIKKO_JAR} ${SOLR_HOME}/lib
	cp ${JNA} ${LIBVOIKKO} ${SOLR_HOME}/lib
	cp ${CONFIG_DIR}/solrconfig.xml ${SCHEMA_XML} ${SOLR_HOME}/conf
	cp ${CONFIG_DIR}/sukija.xsl ${SOLR_HOME}/conf/xslt
	if [ ! -e ${SUKIJA_HOME} ]; then \
	  mkdir ${SUKIJA_HOME}; \
	fi
	cp ${CONFIG_DIR}/suggestion.txt ${CONFIG_DIR}/synonyms.txt ${CONFIG_DIR}/logging.properties ${SUKIJA_HOME}
	sed -e 's,BASE_DIR,$(BASE_DIR),' \
	    -e 's,FILE_NAME,$(FILE_NAME),' \
	    -e 's,EXCLUDES,$(EXCLUDES),' < $(CONFIG_DIR)/data-config.xml.in > $(CONFIG_DIR)/data-config.xml
	cp ${CONFIG_DIR}/data-config.xml ${SUKIJA_HOME}


clean:
	mvn clean
	rm -f ${CONFIG_DIR}/data-config.xml $(SCHEMA_XML)


SUKIJA=sukija
tar:
	if [ -e $(SUKIJA) ]; then \
	  rm -f $(SUKIJA).tar.bz2; \
	  tar cf $(SUKIJA).tar $(SUKIJA); \
	  bzip2 $(SUKIJA).tar; \
	fi

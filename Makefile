# Muuta nämä kaksi muuttujaa, jos Solr on jossain muualla.
SOLR_HOME=${HOME}/Lataukset/solr/solr-4.10.2/example/solr/collection1
JETTY_CONTEXTS_DIR=${HOME}/Lataukset/solr/solr-4.10.2/example/contexts


CONFIG_DIR=conf
SCHEMA_XML=$(CONFIG_DIR)/schema.xml
SCHEMA_XML_IN=$(CONFIG_DIR)/schema.xml.in

FINNISH_TOKENIZER_FACTORY=peltomaa.sukija.finnish.FinnishTokenizerFactory
HV_TOKENIZER_FACTORY=peltomaa.sukija.finnish.HVTokenizerFactory

MALAGA_MORPHOLOGY_FILTER_FACTORY=filter class="peltomaa.sukija.malaga.MalagaMorphologyFilterFactory"\
                                 malagaProjectFile="$${user.home}/.sukija/suomi.pro"

MALAGA_MORPHOLOGY_SUGGESTION_FILTER_FACTORY=filter class="peltomaa.sukija.malaga.MalagaMorphologySuggestionFilterFactory"\
                                 malagaProjectFile="$${user.home}/.sukija/suomi.pro"\
                                 suggestionFile="solr/collection1/conf/finnish-suggestion.xml"\
                                 success="false"

VOIKKO_MORPHOLOGY_FILTER_FACTORY=filter class="peltomaa.sukija.voikko.VoikkoMorphologyFilterFactory"\
                                 dictionary="fi"

VOIKKO_MORPHOLOGY_SUGGESTION_FILTER_FACTORY=filter class="peltomaa.sukija.voikko.VoikkoMorphologySuggestionFilterFactory"\
                                            dictionary="fi"\
                                            suggestionFile="finnish-suggestion.xml"\
                                            success="false"

VFST_MORPHOLOGY_FILTER_FACTORY=filter class="peltomaa.sukija.voikko.VoikkoMorphologyFilterFactory"\
                               dictionary="fi"\
                               path="$${user.home}/vvfst/voikkodict"\
                               libvoikkoPath="$${user.home}/vvfst/lib/libvoikko.so"\
                               libraryPath="$${user.home}/vvfst/lib"

VFST_MORPHOLOGY_SUGGESTION_FILTER_FACTORY=\
    filter class="peltomaa.sukija.voikko.VoikkoMorphologySuggestionFilterFactory"\
    dictionary="fi"\
    path="$${user.home}/vvfst/voikkodict"\
    libvoikkoPath="$${user.home}/vvfst/lib/libvoikko.so"\
    libraryPath="$${user.home}/vvfst/lib"\
    suggestionFile="finnish-suggestion.xml"\
    success="false"

#    suggestionFile="$${solr.solr.home}/collection1/conf/finnish-suggestion.xml"\
#
#    suggestionFile="solr/collection1/conf/finnish-suggestion.xml"\
#

TOKENIZER_FACTORY=$(FINNISH_TOKENIZER_FACTORY)


malaga-schema:
	sed -e 's%TOKENIZER_FACTORY%$(TOKENIZER_FACTORY)%g' \
	    -e 's%MORPHOLOGY_FILTER_FACTORY%$(MALAGA_MORPHOLOGY_FILTER_FACTORY)%g' $(SCHEMA_XML_IN) >$(SCHEMA_XML)

malaga-suggestion-schema:
	sed -e 's%TOKENIZER_FACTORY%$(TOKENIZER_FACTORY)%g' \
	    -e 's%MORPHOLOGY_FILTER_FACTORY%$(MALAGA_MORPHOLOGY_SUGGESTION_FILTER_FACTORY)%g' $(SCHEMA_XML_IN) >$(SCHEMA_XML)

voikko-schema:
	sed -e 's%TOKENIZER_FACTORY%$(TOKENIZER_FACTORY)%g' \
	    -e 's%MORPHOLOGY_FILTER_FACTORY%$(VOIKKO_MORPHOLOGY_FILTER_FACTORY)%g' $(SCHEMA_XML_IN) >$(SCHEMA_XML)

voikko-suggestion-schema:
	sed -e 's%TOKENIZER_FACTORY%$(TOKENIZER_FACTORY)%g' \
	    -e 's%MORPHOLOGY_FILTER_FACTORY%$(VOIKKO_MORPHOLOGY_SUGGESTION_FILTER_FACTORY)%g' $(SCHEMA_XML_IN) >$(SCHEMA_XML)

vfst-schema:
	sed -e 's%TOKENIZER_FACTORY%$(TOKENIZER_FACTORY)%g' \
	    -e 's%MORPHOLOGY_FILTER_FACTORY%$(VFST_MORPHOLOGY_FILTER_FACTORY)%g' $(SCHEMA_XML_IN) >$(SCHEMA_XML)

vfst-suggestion-schema:
	sed -e 's%TOKENIZER_FACTORY%$(TOKENIZER_FACTORY)%g' \
	    -e 's%MORPHOLOGY_FILTER_FACTORY%$(VFST_MORPHOLOGY_SUGGESTION_FILTER_FACTORY)%g' $(SCHEMA_XML_IN) >$(SCHEMA_XML)

debug-schema:
	sed -e 's%TOKENIZER_FACTORY%$(HV_TOKENIZER_FACTORY)%g' \
	    -e 's%MORPHOLOGY_FILTER_FACTORY%$(MALAGA_MORPHOLOGY_SUGGESTION_FILTER_FACTORY)%g' $(SCHEMA_XML_IN) >$(SCHEMA_XML)


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
	if [ ! -e ${SOLR_HOME}/conf/velocity.orig ]; then \
	  cp -r ${SOLR_HOME}/conf/velocity ${SOLR_HOME}/conf/velocity.orig; \
        fi
	if [ ! -e ${JETTY_CONTEXTS_DIR} ]; then \
	  mkdir ${JETTY_CONTEXTS_DIR}; \
	fi
	cp ${CONFIG_DIR}/sukija-context.xml ${JETTY_CONTEXTS_DIR}
	cp ${CONFIG_DIR}/solrconfig.xml ${SOLR_HOME}/conf
	cp ${CONFIG_DIR}/schema.xml ${SOLR_HOME}/conf
	cp ${CONFIG_DIR}/data-config.xml ${SOLR_HOME}/conf
	cp ${CONFIG_DIR}/finnish-synonyms.txt ${SOLR_HOME}/conf
	cp ${CONFIG_DIR}/finnish-suggestion.xml ${SOLR_HOME}/conf
	cp ${CONFIG_DIR}/sukija.xsl ${SOLR_HOME}/conf/xslt
	rm ${SOLR_HOME}/conf/velocity/*
	cp ${CONFIG_DIR}/velocity/* ${SOLR_HOME}/conf/velocity
	sed -e 's,BASE_DIR,$(BASE_DIR),' \
	    -e 's,FILE_NAME,$(FILE_NAME),' \
	    -e 's,EXCLUDES,$(EXCLUDES),' \
            < $(CONFIG_DIR)/data-config.xml.in > $(CONFIG_DIR)/data-config.xml


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

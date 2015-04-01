# Muuta tämä, jos Solr on jossain muualla.
SOLR=${HOME}/Lataukset/solr/solr-5.0.0


SOLR_BIN=${SOLR}/bin
SOLR_HOME=${SOLR}/server/solr/sukija
JETTY_CONTEXTS_DIR=${SOLR}/server/contexts
CONF=conf


asenna:
	javac SukijaAsennus.java
	java SukijaAsennus
	${SOLR_BIN}/solr start
	${SOLR_BIN}/solr create -c sukija -d ${CONF}
	cp ${CONF}/sukija-context.xml ${JETTY_CONTEXTS_DIR}


päivitä-asennus:
	java SukijaAsennus
	cp -r ${CONF}/* ${SOLR_HOME}/conf
	cp ${CONF}/sukija-context.xml ${JETTY_CONTEXTS_DIR}


clean:
	mvn clean


SUKIJA=sukija
tar:
	if [ -e $(SUKIJA) ]; then \
	  rm -f $(SUKIJA).tar.bz2; \
	  tar cf $(SUKIJA).tar $(SUKIJA); \
	  bzip2 $(SUKIJA).tar; \
	fi

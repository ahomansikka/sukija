# Muuta tämä, jos Solr on jossain muualla. Muuta myös tiedoston
# sukija.properties alussa oleva muuttuja sukija.solr.
#
SOLR=${HOME}/Lataukset/solr/solr-6.3.0


SOLR_BIN=${SOLR}/bin
SOLR_HOME=${SOLR}/server/solr/sukija
CONF=conf


asenna: SukijaAsennus.class
	   java SukijaAsennus

päivitä: asenna
	cp -r ${CONF}/* ${SOLR_HOME}/conf

solr-start:
	${SOLR_BIN}/solr start

solr-create:
	${SOLR_BIN}/solr create -c sukija -d conf

solr-stop:
	${SOLR_BIN}/solr stop

solr-restart:
	${SOLR_BIN}/solr restart


poista:
	rm -rf ${SOLR_HOME}


service-update: asenna
	cp -r ${CONF}/* /var/solr/data/sukija/conf


SukijaAsennus.class: SukijaAsennus.java
	javac SukijaAsennus.java


clean:
	mvn clean
	rm -f SukijaAsennus.class conf/data-config.xml conf/schema.xml


SUKIJA=sukija-2.1.3
tar:
	if [ -e $(SUKIJA) ]; then \
	  rm -f $(SUKIJA).tar.bz2; \
	  tar cf $(SUKIJA).tar $(SUKIJA); \
	  bzip2 $(SUKIJA).tar; \
	fi

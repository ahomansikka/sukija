# Muuta tämä, jos Solr on jossain muualla.
SOLR=${HOME}/Lataukset/solr/solr-5.0.0


SOLR_BIN=${SOLR}/bin
SOLR_HOME=${SOLR}/server/solr/sukija
CONF=conf


asenna: SukijaAsennus.class
	java SukijaAsennus
	./asenna.sh


päivitä: SukijaAsennus.class
	java SukijaAsennus
	cp -r ${CONF}/* ${SOLR_HOME}/conf


service: SukijaAsennus.class
	java SukijaAsennus
	./asenna.sh -s


service-update: SukijaAsennus.class
	java SukijaAsennus
	cp -r ${CONF}/* /var/solr/data/sukija/conf


SukijaAsennus.class: SukijaAsennus.java
	javac SukijaAsennus.java


clean:
	mvn clean
	rm SukijaAsennus.class


SUKIJA=sukija
tar:
	if [ -e $(SUKIJA) ]; then \
	  rm -f $(SUKIJA).tar.bz2; \
	  tar cf $(SUKIJA).tar $(SUKIJA); \
	  bzip2 $(SUKIJA).tar; \
	fi

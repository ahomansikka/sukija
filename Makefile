tiedostot: SukijaAsennus.class
	   java SukijaAsennus


SukijaAsennus.class: SukijaAsennus.java
	javac SukijaAsennus.java


clean:
	mvn clean
	rm -f SukijaAsennus.class conf/data-config.xml conf/schema.xml


SUKIJA=sukija-2.2.6
tar:
	if [ -e $(SUKIJA) ]; then \
	  rm -f $(SUKIJA).tar.bz2; \
	  tar cf $(SUKIJA).tar $(SUKIJA); \
	  bzip2 $(SUKIJA).tar; \
	fi

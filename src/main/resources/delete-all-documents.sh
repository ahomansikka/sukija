#!/bin/bash

# Sukijan URL
S=http://localhost:8983/solr/sukija

# Debug.
#D=-Dorg.apache.tika.service.error.warn=true


SOLR=/opt/solr

# On parasta käyttää samoja jar-paketteja kuin Solr.
CP=.
CP=${CP}:${SOLR}/modules/extraction/lib/*
CP=${CP}:${SOLR}/server/lib/ext/*
CP=${CP}:${SOLR}/server/solr-webapp/webapp/WEB-INF/lib/*

# Kun indeksoidaan, käytetään tätä.
CP=${CP}:/var/solr/data/sukija/lib/*

# Testataan.
#CP=${CP}:${HOME}/.m2/repository/peltomaa/sukija/sukija/3.0.0/sukija-3.0.0.jar


java -cp ${CP} ${D} peltomaa.sukija.indexer.DeleteAllDocuments ${S}

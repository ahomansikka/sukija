#!/bin/bash

# Indeksoijan asetustiedosto.
F=/var/solr/data/sukija/conf/indexer-config.xml 


# Debug.
#D=-Dorg.apache.tika.service.error.warn=true


SOLR=/opt/solr

# On parasta käyttää samoja jar-paketteja kuin Solr.
CP=.
CP=${CP}:${SOLR}/modules/extraction/lib/*
CP=${CP}:${SOLR}/server/lib/ext/*
CP=${CP}:${SOLR}/server/solr-webapp/webapp/WEB-INF/lib/*
CP=${CP}:/var/solr/data/sukija/lib/*

java -cp ${CP} ${D} peltomaa.sukija.indexer.Indexer ${F}

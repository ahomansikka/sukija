#!/bin/bash


# Debug.
# D=-Dorg.apache.tika.service.error.warn=true


REPO=${HOME}/.m2/repository

CP=.
CP=${CP}:${REPO}/peltomaa/sukija/sukija/3.0.0/sukija-3.0.0.jar
CP=${CP}:${REPO}/jakarta/xml/bind/jakarta.xml.bind-api/4.0.0/jakarta.xml.bind-api-4.0.0.jar 
CP=${CP}:${REPO}/org/glassfish/jaxb/jaxb-runtime/4.0.5/jaxb-runtime-4.0.5.jar
CP=${CP}:${REPO}/com/sun/istack/istack-commons-runtime/4.1.2/istack-commons-runtime-4.1.2.jar
CP=${CP}:${REPO}/org/glassfish/jaxb/jaxb-core/4.0.5/jaxb-core-4.0.5.jar
CP=${CP}:${REPO}/jakarta/activation/jakarta.activation-api/2.1.2/jakarta.activation-api-2.1.2.jar 

# Käytetään samoja jar-tiedostoja kuin Sorj.
CP=${CP}:/opt/solr/server/solr-webapp/webapp/WEB-INF/lib/solr-solrj-10.0.0.jar
CP=${CP}:/opt/solr/modules/extraction/lib/tika-core-3.2.3.jar
CP=${CP}:/opt/solr/server/solr-webapp/webapp/WEB-INF/lib/commons-io-2.20.0.jar 
CP=${CP}:/opt/solr/server/lib/ext/*

java -cp ${CP} ${D} peltomaa.sukija.indexer.DiskIndexer "$@"

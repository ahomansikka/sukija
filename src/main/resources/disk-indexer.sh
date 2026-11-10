#!/bin/bash


# Debug.
# D=-Dorg.apache.tika.service.error.warn=true


REPO=${HOME}/.m2/repository

CP=.
CP=${CP}:${REPO}/peltomaa/sukija/sukija/3.0.0/sukija-3.0.0.jar
CP=${CP}:${REPO}/jakarta/xml/bind/jakarta.xml.bind-api/4.0.2/jakarta.xml.bind-api-4.0.2.jar 
CP=${CP}:${REPO}/org/glassfish/jaxb/jaxb-runtime/4.0.5/jaxb-runtime-4.0.5.jar
CP=${CP}:${REPO}/com/sun/istack/istack-commons-runtime/4.1.2/istack-commons-runtime-4.1.2.jar
CP=${CP}:${REPO}/org/glassfish/jaxb/jaxb-core/4.0.5/jaxb-core-4.0.5.jar
CP=${CP}:${REPO}/jakarta/activation/jakarta.activation-api/2.1.3/jakarta.activation-api-2.1.3.jar 

# Käytetään samoja jar-tiedostoja kuin Sorj.
CP=${CP}:/opt/solr/server/solr-webapp/webapp/WEB-INF/lib/solr-solrj-10.1.0.jar
CP=${CP}:/opt/solr/modules/extraction/lib/tika-core-3.3.2.jar
CP=${CP}:/opt/solr/server/solr-webapp/webapp/WEB-INF/lib/commons-io-2.22.0.jar 
CP=${CP}:/opt/solr/server/lib/ext/*

java -cp ${CP} ${D} peltomaa.sukija.indexer.DiskIndexer "$@"

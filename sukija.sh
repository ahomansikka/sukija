#!/bin/bash


if [ $# == 0 ]; then
  echo Ei ole tiedostoja, mitä indeksoida!
  exit
fi


SOLR_HOME=${HOME}/Lataukset/apache-solr-3.6.1/example/solr
M2_REPO=${HOME}/.m2/repository

# On parasta käyttää samoja jar-tiedostoja, joita Solr käyttää.
CP=.
CP=${CP}:${SOLR_HOME}/lib/sukija-core-1.0.jar
CP=${CP}:${SOLR_HOME}/lib/sukija-malaga-1.0.jar
CP=${CP}:${SOLR_HOME}/lib/sukija-voikko-1.0.jar
CP=${CP}:${SOLR_HOME}/lib/sukija-indexer-1.0.jar
CP=${CP}:${M2_REPO}/org/apache/solr/solr-solrj/3.6.1/solr-solrj-3.6.1.jar
CP=${CP}:${M2_REPO}/commons-httpclient/commons-httpclient/3.1/commons-httpclient-3.1.jar
CP=${CP}:${M2_REPO}/commons-codec/commons-codec/1.6/commons-codec-1.6.jar
CP=${CP}:${M2_REPO}/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar
CP=${CP}:${M2_REPO}/org/slf4j/slf4j-api/1.5.6/slf4j-api-1.5.6.jar
CP=${CP}:${M2_REPO}/org/slf4j/slf4j-jdk14/1.5.6/slf4j-jdk14-1.5.6.jar
CP=${CP}:${M2_REPO}/net/java/dev/jna/jna/3.4.0/jna-3.4.0.jar


java -cp ${CP} peltomaa.sukija.indexer.Indexer "$@"

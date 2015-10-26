#!/bin/bash

# Muuta nämä tarvittaessa.
#
SOLR=${HOME}/Lataukset/solr/solr-5.3.0
MAVEN=${HOME}/.m2/repository
SUKIJA=${SOLR}/server/solr/sukija
JETTY=${SOLR}/server/contexts

if [ ! -e SukijaAsennus.class ]; then
  echo 'Käännä ensin SukijaAsennus.java'
  exit 1
fi

if [ ! -e ${SOLR} ]; then
  echo Hakemistoa ${SOLR} ei ole olemassa.
  exit 1
fi

if [ $# -gt 0 ]; then
  case $1 in
    -s)
      # Muuta nämä tarvittaessa.
      SUKIJA=/var/solr/data/sukija
      JETTY=/opt/solr/server/contexts
    ;;
    *)
      echo Virheellinen argumentti $1.
      exit 1
    ;;
   esac
fi


SUKIJA_LIB=${SUKIJA}/lib
SOLR_BIN=${SOLR}/bin


echo Asennetaan Sukija hakemistoon ${SUKIJA}.

#echo $SUKIJA
#echo $JETTY
#echo $SUKIJA_LIB

echo $1


# Tee Sukijan tarvitsemat hakemistot, mikäli ne eivät ole jo olemassa.
#
mkdir -p ${SUKIJA}/{conf,data,lib}


# Kopioi asetukset.
#
cp -r conf/* ${SUKIJA}/conf
cp conf2/sukija-context.xml ${JETTY}

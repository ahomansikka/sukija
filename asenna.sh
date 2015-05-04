#!/bin/bash

# Muuta nämä tarvittaessa.
#
SOLR=${HOME}/Lataukset/solr/solr-5.1.0
MAVEN=${HOME}/.m2/repository
SUKIJA=${SOLR}/server/solr/sukija
JETTY=${SOLR}/server/contexts


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


# Kopioi jar-tiedostot.
#
#cp ${SOLR}/dist/solr-dataimporthandler-*.jar ${SUKIJA_LIB}
#cp ${SOLR}/contrib/extraction/lib/*jar ${SUKIJA_LIB}
#cp ${SOLR}/dist/solr-cell*jar ${SUKIJA_LIB}
#cp ${SOLR}/contrib/clustering/lib/*.jar ${SUKIJA_LIB}
#cp ${SOLR}/dist/solr-clustering-*.jar ${SUKIJA_LIB}
#cp ${SOLR}/contrib/langid/lib/*.jar ${SUKIJA_LIB}
#cp ${SOLR}/dist/solr-langid-*.jar ${SUKIJA_LIB}
#cp ${SOLR}/contrib/velocity/lib/*.jar ${SUKIJA_LIB}
#cp ${SOLR}/dist/solr-velocity-*.jar ${SUKIJA_LIB}
#cp ${MAVEN}/net/java/dev/jna/jna/3.4.0/jna-3.4.0.jar ${SUKIJA_LIB}
#cp ${MAVEN}/org/puimula/voikko/libvoikko/3.7/libvoikko-3.7.jar ${SUKIJA_LIB}
#cp ${MAVEN}/peltomaa/sukija/sukija-voikko/1.1/sukija-voikko-1.1.jar ${SUKIJA_LIB}
#cp ${MAVEN}/peltomaa/sukija/sukija-malaga/1.1/sukija-malaga-1.1.jar ${SUKIJA_LIB}
#cp ${MAVEN}/peltomaa/sukija/sukija-core/1.1/sukija-core-1.1.jar ${SUKIJA_LIB}


# Käynnistä Solr uudelleen.
#
if [ $# -gt 0 ]; then
  sudo service solr restart
else
  ${SOLR_BIN}/solr start
  ${SOLR_BIN}/solr create -c sukija -d conf
  ${SOLR_BIN}/solr restart
fi

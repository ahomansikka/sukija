# Tehdään Solr:aan tarvittavat hakemistot ja kopioidaan
# niihin tarvittavat tiedostot, kun Solr on asennettu
# samalla käyttäjätunnuksella kuin minkä alla
# indeksoitavat tiedostot ovat.
#
# Tarkista, että versionumerot ja hakemistojen (kansioitten) nimet ovat oikein!

DIR=/var/solr/data/configsets

# Tehdään Solr:n tarvitsemat hakemistot.
mkdir -p ${DIR}/sukija/conf ${DIR}/sukija/lib

REPO=${HOME}/.m2/repository

# Kopioidaan tarvittavat tiedot hakemistoihin.
cp -r conf/* ${DIR}/sukija/conf/
cp ${REPO}/org/puimula/voikko/libvoikko/4.1.1/libvoikko-4.1.1.jar ${DIR}/sukija/lib/
cp ${REPO}/peltomaa/sukija/sukija/3.0.0/sukija-3.0.0.jar ${DIR}/sukija/lib/
cp ${REPO}/org/ahocorasick/ahocorasick/0.6.3/ahocorasick-0.6.3.jar ${DIR}/sukija/lib/
cp ${REPO}/net/java/dev/jna/jna/5.17.0/jna-5.17.0.jar ${DIR}/sukija/lib/
cp ${REPO}/com/sun/istack/istack-commons-runtime/4.1.2/istack-commons-runtime-4.1.2.jar ${DIR}/sukija/lib/

# Nämä tarvitaan vain, jos käyttää oikeinkirjoituksen korjausehdotuksia tai indeksointiohjelmaa indexer.sh.
cp ${REPO}/org/glassfish/jaxb/jaxb-runtime/4.0.5/jaxb-runtime-4.0.5.jar ${DIR}/sukija/lib/
cp ${REPO}/org/glassfish/jaxb/jaxb-core/4.0.5/jaxb-core-4.0.5.jar ${DIR}/sukija/lib/
#cp ${REPO}/jakarta/xml/bind/jakarta.xml.bind-api/4.0.0/jakarta.xml.bind-api-4.0.0.jar ${DIR}/sukija/lib/
#cp ${REPO}/jakarta/annotation/jakarta.annotation-api/2.1.1/jakarta.annotation-api-2.1.1.jar ${DIR}/sukija/lib/
#cp ${REPO}/jakarta/activation/jakarta.activation-api/2.1.2/jakarta.activation-api-2.1.2.jar ${DIR}/sukija/lib/
cp ${REPO}/jakarta/xml/bind/jakarta.xml.bind-api/4.0.2/jakarta.xml.bind-api-4.0.2.jar ${DIR}/sukija/lib/
cp ${REPO}/jakarta/annotation/jakarta.annotation-api/3.0.0/jakarta.annotation-api-3.0.0.jar ${DIR}/sukija/lib/
cp ${REPO}/jakarta/activation/jakarta.activation-api/2.1.3/jakarta.activation-api-2.1.3.jar ${DIR}/sukija/lib/

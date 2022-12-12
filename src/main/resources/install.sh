# Tehdään Solr:aan tarvittavat hakemistot ja kopioidaan
# niihin tarvittavat tiedostot, kun Solr on asennettu
# samalla käyttäjätunnuksella kuin minkä alla
# indeksoitavat tiedostot ovat.
#
# Tarkista, että versionumerot ja hakemistojen (kansioitten) nimet ovat oikein!


# Tätä ei ehkä tarvita, jos Solr:lla indeksoidaan jotain muuta
# kuin kovalevyllä olevia tiedostoja.
#sudo cp conf2/sukija-context.xml /opt/solr/server/contexts/sukija-context.xml

# Tehdään Solr:n tarvitsemat hakemistot.
mkdir -p /var/solr/data/sukija/conf /var/solr/data/sukija/lib

# Kopioidaan tarvittavat tiedot hakemistoihin.
cp -r conf/* /var/solr/data/sukija/conf/
cp $HOME/.m2/repository/org/puimula/voikko/libvoikko/4.1.1/libvoikko-4.1.1.jar /var/solr/data/sukija/lib/
cp $HOME/.m2/repository/peltomaa/sukija/sukija/3.0.0/sukija-3.0.0.jar /var/solr/data/sukija/lib/
cp $HOME/.m2/repository/org/ahocorasick/ahocorasick/0.6.3/ahocorasick-0.6.3.jar /var/solr/data/sukija/lib/

# Nämä tarvitaan vain, jos käyttää oikeinkirjoituksen korjausehdotuksia tai indeksointiohjelmaa indexer.sh.
cp $HOME/.m2/repository/com/sun/istack/istack-commons-runtime/4.0.1/istack-commons-runtime-4.0.1.jar /var/solr/data/sukija/lib/
cp $HOME/.m2/repository/javax/activation/javax.activation-api/1.2.0/javax.activation-api-1.2.0.jar /var/solr/data/sukija/lib/
cp $HOME/.m2/repository/javax/xml/bind/jaxb-api/2.4.0-b180830.0359/jaxb-api-2.4.0-b180830.0359.jar /var/solr/data/sukija/lib/
cp $HOME/.m2/repository/org/glassfish/jaxb/jaxb-runtime/3.0.2/jaxb-runtime-3.0.2.jar /var/solr/data/sukija/lib/

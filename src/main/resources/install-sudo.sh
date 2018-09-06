# Tehdään Solr:ään tarvittavat hakemistot ja kopioidaan
# niihin tarvittavat tiedostot, kun Solr on asennettu
# tunnuksella 'solr'.
#
# Tarkista, että versionumerot ja kansioitten nimet ovat oikein!


# Tätä ei ehkä tarvita, jos Solr:lla indeksoidaan jotain muuta
# kuin kovalevyllä olevia tiedostoja.
sudo cp conf2/sukija-context.xml /opt/solr/server/contexts/sukija-context.xml

# Tehdään Solr:n tarvitsemat hakemistot.
sudo -u solr mkdir -p /var/solr/data/sukija/conf /var/solr/data/sukija/lib

# Kopioidaan tarvittavat tiedot hakemistoihin.
sudo -u solr cp -r conf/* /var/solr/data/sukija/conf/
sudo -u solr cp $HOME/.m2/repository/net/java/dev/jna/jna/4.2.2/jna-4.2.2.jar /var/solr/data/sukija/lib/
sudo -u solr cp $HOME/.m2/repository/org/puimula/voikko/libvoikko/4.1.1/libvoikko-4.1.1.jar /var/solr/data/sukija/lib/
sudo -u solr cp $HOME/.m2/repository/peltomaa/sukija/sukija/2.2.12/sukija-2.2.12.jar /var/solr/data/sukija/lib/
sudo -u solr cp $HOME/.m2/repository/org/ahocorasick/ahocorasick/0.4.0/ahocorasick-0.4.0.jar /var/solr/data/sukija/lib/

# Nämä tarvitaan vain, jos käyttää oikeinkirjoituksen korjausehdotuksia.
sudo -u solr cp $HOME/.m2/repository/javax/xml/bind/jaxb-api/2.3.0/jaxb-api-2.3.0.jar /var/solr/data/sukija/lib/
sudo -u solr cp $HOME/.m2/repository/org/glassfish/jaxb/jaxb-core/2.3.0/jaxb-core-2.3.0.jar /var/solr/data/sukija/lib/
sudo -u solr cp $HOME/.m2/repository/org/glassfish/jaxb/jaxb-runtime/2.4.0-b180725.0644/jaxb-runtime-2.4.0-b180725.0644.jar /var/solr/data/sukija/lib/
sudo -u solr cp $HOME/.m2/repository/com/sun/istack/istack-commons-runtime/3.0.5/istack-commons-runtime-3.0.5.jar /var/solr/data/sukija/lib/
sudo -u solr cp $HOME/.m2/repository/javax/activation/javax.activation-api/1.2.0/javax.activation-api-1.2.0.jar /var/solr/data/sukija/lib/

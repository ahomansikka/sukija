# Tehdään Solr:aan tarvittavat hakemistot ja kopioidaan
# niihin tarvittavat tiedostot, kun Solr on asennettu
# samalla käyttäjätunnuksella kuin minkä alla
# indeksoitavat tiedostot ovat.
#
# Tarkista, että versionumerot ja hakemistojen (kansioitten) nimet ovat oikein!


# Tätä ei ehkä tarvita, jos Solr:lla indeksoidaan jotain muuta
# kuin kovalevyllä olevia tiedostoja.
sudo cp conf2/sukija-context.xml /opt/solr/server/contexts/sukija-context.xml

# Tehdään Solr:n tarvitsemat hakemistot.
mkdir -p /var/solr/data/sukija/conf /var/solr/data/sukija/lib

# Kopioidaan tarvittavat tiedot hakemistoihin.
cp -r conf/* /var/solr/data/sukija/conf/
cp $HOME/.m2/repository/net/java/dev/jna/jna/4.2.2/jna-4.2.2.jar /var/solr/data/sukija/lib/
cp $HOME/.m2/repository/org/puimula/voikko/libvoikko/4.1.1/libvoikko-4.1.1.jar /var/solr/data/sukija/lib/
cp $HOME/.m2/repository/peltomaa/sukija/sukija/2.2.10/sukija-2.2.10.jar /var/solr/data/sukija/lib/
cp $HOME/.m2/repository/org/ahocorasick/ahocorasick/0.4.0/ahocorasick-0.4.0.jar /var/solr/data/sukija/lib/

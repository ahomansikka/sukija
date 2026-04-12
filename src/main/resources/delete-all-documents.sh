#!/bin/bash

# Poistetaan kaikki tietueet Sukija tietokannasta.

curl http://localhost:8983/solr/sukija/update -H "Content-Type: text/xml" --data-binary '<delete><query>*:*</query></delete>'

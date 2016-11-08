#!/bin/bash
wget http://downloads.dbpedia.org/2016-04/core-i18n/en/mappingbased_literals_en.ttl.bz2
wget http://downloads.dbpedia.org/2016-04/core-i18n/en/mappingbased_objects_en.ttl.bz2
wget http://downloads.dbpedia.org/2016-04/core-i18n/en/labels_en.ttl.bz2
bunzip2 mappingbased_literals_en.ttl.bz2
bunzip2 mappingbased_objects_en.ttl.bz2
bunzip2 labels_en.ttl.bz2 

mv labels_en.ttl dbpedia_labels.ttl

wget http://139.18.2.164/rusbeck/hawk/en_surface_forms.ttl
wget https://raw.githubusercontent.com/AKSW/NLIWOD/master/qa.hawk/resources/dbpedia_3Eng_class.ttl
wget https://raw.githubusercontent.com/AKSW/NLIWOD/master/qa.hawk/resources/dbpedia_3Eng_property.ttl

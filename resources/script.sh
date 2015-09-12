#!/bin/bash
wget http://downloads.dbpedia.org/3.9/en/mappingbased_properties_en.ttl.bz2
wget http://downloads.dbpedia.org/3.9/en/labels_en.ttl.bz2
bunzip2 mappingbased_properties_en.ttl.bz2
bunzip2 labels_en.ttl.bz2 

mv labels_en.ttl dbpedia_labels.ttl

wget http://139.18.2.164/rusbeck/hawk/en_surface_forms.ttl
wget https://raw.githubusercontent.com/AKSW/hawk/master/resources/dbpedia_3Eng_class.ttl
wget https://raw.githubusercontent.com/AKSW/hawk/master/resources/dbpedia_3Eng_property.ttl

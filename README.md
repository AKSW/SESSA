# Keyword Search on DBpedia

Keyword Search is a scalable search engine on structured resources provided by DBpedia. It allows user to enter simple queries (like on Google) and then generate results in response to that queries.

```
mvn clean package 
```

* test_1.nt is very tiny part of the original one "dbpedia_2016-10.nt"
* test_2.ttl is very very tiny part of the original one "labels_en.ttl"  
* please note that "dbpedia_2016-10.nt" and "labels_en.ttl" are parts of DBpedia datasets and they are available online
* test_1.nt and test_2.ttl are used to test RdfDictionaryImport.java that loads RDF to SESSA


Needed files:
```
cd src/main
mkdir resources
cd resources
wget downloads.dbpedia.org/2016-10/dbpedia_2016-10.nt
wget downloads.dbpedia.org/2016-10/core-i18n/en/labels_en.ttl.bz2
bunzip2 labels_en.ttl.bz2
```
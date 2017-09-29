# Keyword Search on DBpedia

Keyword Search is a scalable search engine on structured resources provided by DBpedia. It allows user to enter simple queries (like on Google) and then generate results in response to that queries.

### Features

1. A google user interface like web application.
2. Returns the results in decreasing order of relevancy.

### Running it via war creation
you might need to run the script.sh
you might need to run the DBOIndex

```
mvn clean package jetty:run
```
we recommend using Jetty 9.X 

test_1.nt is very tiny part of the original one "dbpedia_2016-10.nt"
test_2.ttl is very very tiny part of the original one "labels_en.ttl"  
please note that "dbpedia_2016-10.nt" and "labels_en.ttl" are parts of Dbpedia datasets and they are available online
test_1.nt and test_2.ttl are used to test RdfDictionaryImport.java that loads RDF to SESSA

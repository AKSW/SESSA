# Keyword Search on DBpedia

Keyword Search is a scalable search engine on structured resources provided by DBpedia. It allows user to enter simple queries (like on Google) and then generate results in response to that queries.

```
mvn clean package 
```
## How to run SESSA
* Load at least one of the FileHandlers (under org.aksw.sessa.helper.files.handler) with a dictionary-file (i.e. for RDF-files, load the RdfFileHandler)
* Load a SESSA object and load the dictionary by using .loadFileToLuceneDictionary(fileHandler) or .loadFileToHashMapDictionary(fileHandler) with the file handler
  * HashMaps can take quite a lot memory, depending on your imported dictionary
  * Lucene-dictionaries do not need a lot of memory, but the internal Lucene-scoring provides non-optimal candidates
* Ask questions by using .answer(question)



Recommended files to run SESSA (and needed files to run SESSAMeasurement):
```
cd src/main
mkdir resources
cd resources
wget downloads.dbpedia.org/2016-10/dbpedia_2016-10.nt
wget downloads.dbpedia.org/2016-10/core-i18n/en/labels_en.ttl.bz2
bunzip2 labels_en.ttl.bz2
```

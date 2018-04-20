# Keyword Search on DBpedia

Keyword Search is a scalable search engine on structured resources provided by DBpedia. It allows user to enter simple queries (like on Google) and then generate results in response to that queries.

The below mentioned files are recommended to use with SESSA.
You can provide your own files, too.
You can change the location of the resources in a configuration file (see under Configuration)
```
mkdir resources
cd resources
wget https://raw.githubusercontent.com/dice-group/NLIWOD/master/qa.hawk/resources/dbpedia_3Eng_class.ttl
wget https://raw.githubusercontent.com/dice-group/NLIWOD/master/qa.hawk/resources/dbpedia_3Eng_property.ttl
wget downloads.dbpedia.org/2016-10/dbpedia_2016-10.nt
wget downloads.dbpedia.org/2016-10/core-i18n/en/labels_en.ttl.bz2
bunzip2 labels_en.ttl.bz2
```
## Configuration
SESSA can be used with a user-specified configuration file.
After the project is build, a user specified configuration file can be used via the system properties.
Example:
```
java -Dconfiguration.location=/path/to/file -jar SESSA.jar 
```
See the [configuration file](https://github.com/dice-group/SESSA/src/main/resources/default.properties) for details on the configuration properties.
## Running SESSA
Because SESSA can be implemented into other projects or act as a standalone web service,
there are multiple ways to use SESSA. 
### Run SESSA as Web Application (e.g. for GerbilQA-Benchmarking)
```
mvn spring-boot:run
```
After that SESSA runs on http://localhost:8080. Test it by simply sending an HTTP POST request to http://localhost:8080/gerbil with the parameters:
- `query`: A UTF-8 encoded String 
- `lang`: language of the question (which will be ignored for now)

Example Request:   
`curl -d "query=Harold and Maude, compose, music?&lang=en" -X POST http://localhost:8080/gerbil`

### Run SESSA as Web Application via docker
SESSA can also be build in a docker container.
It can be build via the 'docker build'-command and is also accessible via [docker hub](https://hub.docker.com/r/dicegroup/sessa/). 
Make sure to expose the 8080 port in the container to the outside.
Example (via docker hub):
```
docker run -p8080:8080 dicegroup/sessa
```

### Using SESSA for your own service
* Load the appropriate FileHandler for your dictionary files (if any)
* Load a SESSA object and load the dictionary by using .loadFileToLuceneDictionary(fileHandler) or .loadFileToHashMapDictionary(fileHandler) with the file handler
  * HashMapDictionary can take quite a lot memory, depending on the size of your dictionary files. HashMapDictionary only uses exact matches
  * LuceneDictionary needs less memory, but the internal Lucene-scoring provides non-optimal candidates
* Ask questions by using sessa.answer(question)
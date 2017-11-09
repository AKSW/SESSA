package org.aksw.sessa.main;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.DictionaryImportInterface;
import org.aksw.sessa.importing.dictionary.implementation.RdfDictionaryImport;

public class ReversedTsvDictionarySaver {

  static String RDF_labels = "src/main/resources/labels_en.ttl";
  static String RDF_ontology = "src/main/resources/dbpedia_2016-10.nt";

  private static final String target = "src/main/resources/dictionary.tsv";

  private static Map<String, Set<String>> dictionary;

  public static void main(String[] args) {

		loadFileToDictionaryRDF(RDF_labels);
		loadFileToDictionaryRDF(RDF_ontology);
    saveDictionary();

  }


  private static void loadFileToDictionaryRDF(String file) {
    DictionaryImportInterface dictImporter = new RdfDictionaryImport();
    loadDictionaryDataset(file, dictImporter);
  }


  private static void loadDictionaryDataset(String file, DictionaryImportInterface dictImporter) {
    if (dictionary == null) {
      dictionary = dictImporter.getDictionary(file);
    } else {
      dictionary.putAll(dictImporter.getDictionary(file));
    }
  }


  private static void saveDictionary(){
    PrintWriter writer = null;
    try{
      writer = new PrintWriter(target);
      for(Entry<String, Set<String>> entry: dictionary.entrySet()){
        writer.print(entry.getKey());
        for(String value : entry.getValue()){
          writer.print("\t" + value);
        }
        writer.println();
      }
    } catch (Exception e){
    } finally {
      writer.close();
    }
  }
}

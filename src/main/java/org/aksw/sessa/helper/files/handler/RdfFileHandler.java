package org.aksw.sessa.helper.files.handler;


import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import org.openrdf.model.vocabulary.RDFS;

/**
 * This class provides a quick and dirty solution to ro read rdf-files.
 * TODO: Rewrite in a nicer way
 */
public class RdfFileHandler implements FileHandlerInterface {

  protected String file;
  private Iterator<Entry<String,Set<String>>> iterator;
  private String key;
  private Stack<String> values;



  public RdfFileHandler(String file) {
    this.file = file;
    values = new Stack<>();
    iterator = createDictionary(file).entrySet().iterator();
  }


  /**
   * Provides next entry, i.e. next key and value pair.
   *
   * @return next key and value pair.
   * @throws IOException If an I/O error occurs
   */
  @Override
  public Entry<String, String> nextEntry() throws IOException {
    if (values.isEmpty()) {
      if (!getNextPair()) {
        return null;
      }
    }
    String value = values.pop();
    return new SimpleEntry<>(key.toLowerCase(), value);
  }


  private boolean getNextPair() throws IOException {
    if(iterator.hasNext()){
      Entry<String, Set<String>> entry = iterator.next();
      key = entry.getKey();
      values = new Stack<>();
      values.addAll(entry.getValue());
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns name of the file that is processed by the handler.
   *
   * @return name of the file that is processed by the handler
   */
  @Override
  public String getFileName() {
    return file;
  }

  private Map<String, Set<String>> createDictionary(String fileName) {

    Map<String, Set<String>> dictionary = new HashMap<>();

    StreamRDF destination = new StreamRDF() {

      @Override
      public void triple(Triple statement) {

        String uri = statement.getPredicate().getURI();
        String rdfslabeluri = RDFS.LABEL.stringValue();
        if (uri.equals(rdfslabeluri)) {
          Node object = statement.getObject();
          String surfaceform = object.getLiteral().getLexicalForm().toLowerCase();
          String subjectURI = statement.getSubject().toString();

          if (dictionary.containsKey(surfaceform)) {
            // we see the uri a second time, and thus add the
            // surfaceform to the set
            Set<String> tmpset = dictionary.get(surfaceform);
            tmpset.add(subjectURI);
            dictionary.put(surfaceform, tmpset);
          } else {
            // we see the (uri,surfaceform) pair for the first time
            Set<String> tmpset = new HashSet<>();
            tmpset.add(subjectURI);
            dictionary.put(surfaceform, tmpset);
          }
        }

      }

      @Override
      public void base(String arg0) {
      }

      @Override
      public void finish() {
      }

      @Override
      public void prefix(String arg0, String arg1) {
      }

      @Override
      public void quad(Quad arg0) {
      }

      @Override
      public void start() {
      }

    };
    RDFDataMgr.parse(destination, fileName);

    return dictionary;
  }

  /**
   * Closes the used readers and releases any system resources associated with it.
   *
   * @throws IOException If an I/O error occurs
   */
  @Override
  public void close() throws IOException {

  }
}

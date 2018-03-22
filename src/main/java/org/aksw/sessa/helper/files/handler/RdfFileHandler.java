package org.aksw.sessa.helper.files.handler;


import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.jena.vocabulary.RDFS;

/**
 * Provides handling for RDF-files. This class is an implementation of the interface {@link
 * DictionaryInterface}.
 */
public class RdfFileHandler implements FileHandlerInterface {

  private String file;
  private PipedRDFIterator<Triple> iter;
  private Node rdfsLabelNode;

  /**
   * Creates a RdfFileHandler that uses the given file and base-URI. Uses {@link
   * org.apache.jena.rdf.model.Model#read(InputStream, String, String)} to read the RDF-file.
   *
   * @param file RDF-file to be handled by this class
   * @param base the base uri to be used when converting relative URI's to absolute URI's.
   * (Resolving relative URIs and fragment IDs is done by prepending the base URI to the relative
   * URI/fragment.) If there are no relative URIs in the source, this argument may safely be null.
   * If the base is the empty string, then relative URIs will be retained in the model. This is
   * typically unwise and will usually generate errors when writing the model back out.
   * @param lang - the language of the serialization null selects the default (i.e. RDF/XML)
   */
  public RdfFileHandler(String file, String base, String lang) throws IOException {
    this.file = file;

    // Setting a node with rdfs:label to filter out triples without it.
    // For some reason I can't declare & define it in the method, there it is here.
    rdfsLabelNode = NodeFactory.createURI((RDFS.label.getURI()));

    // Create a PipedRDFStream to accept input and a PipedRDFIterator to
    // consume it
    // You can optionally supply a buffer size here for the
    // PipedRDFIterator, see the documentation for details about recommended
    // buffer sizes
    iter = new PipedRDFIterator<>();
    final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);
    // PipedRDFStream and PipedRDFIterator need to be on different threads
    ExecutorService executor = Executors.newSingleThreadExecutor();

    // Create a runnable for our parser thread
    Runnable parser = () -> RDFParser.source(file).parse(inputStream);
    // Start the parser on another thread
    executor.submit(parser);

  }

  /**
   * Therefore the given RDF-files has to have no relative URIs in the source and has to be a
   * TTL-formatted file.
   *
   * @param file RDF-file to be handled by this class
   */
  public RdfFileHandler(String file) throws IOException {
    this(file, null, "TTL");
  }


  /**
   * Provides next entry, i.e. next key (n-gram) and value (URI) pair.
   *
   * @return next key and value pair
   * @throws IOException If an I/O error occurs
   */
  @Override
  public Entry<String, String> nextEntry() throws IOException {
    Triple stmt;
    do {
      if (iter.hasNext()) {
        stmt = iter.next();
      } else {
        return null;
      }
    } while (!stmt.getPredicate().matches(rdfsLabelNode));

      Node subject = stmt.getSubject();
      Node object = stmt.getObject();
      return new SimpleEntry<>(
          object.getLiteral().getLexicalForm().toString().toLowerCase(),
          subject.getURI());
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

  /**
   * Closes the used readers and releases any system resources associated with it.
   *
   * @throws IOException If an I/O error occurs
   */
  @Override
  public void close() throws IOException {
    iter.close();
  }
}

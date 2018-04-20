package org.aksw.sessa.helper.files.handler;


import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RiotException;
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
  private String base;
  private String lang;

  /**
   * Creates a RdfFileHandler that uses PipedRDFIterator to read the RDF-file.
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
    this(base, lang);
    loadFile(file);
  }

  /**
   * Creates a RdfFileHandler with given file. No base will be used and the lang will be read from
   * file extension.
   *
   * @param file RDF-file to be handled by this class
   */
  public RdfFileHandler(String file) throws IOException {
    this(file, null, null);
  }

  /**
   * Creates a RdfFileHandler with the given base and language. The file has to be loaded before
   * using this via {@link #loadFile(String)}.
   *
   * @param base the base uri to be used when converting relative URI's to absolute URI's.
   * (Resolving relative URIs and fragment IDs is done by prepending the base URI to the relative
   * URI/fragment.) If there are no relative URIs in the source, this argument may safely be null.
   * If the base is the empty string, then relative URIs will be retained in the model. This is
   * typically unwise and will usually generate errors when writing the model back out.
   * @param lang - the language of the serialization null selects the default (i.e. RDF/XML)
   */
  public RdfFileHandler(String base, String lang) {
    this.file = null;
    this.base = base;
    this.lang = lang;
    this.rdfsLabelNode = null;
    this.iter = null;
  }

  /**
   * Creates a RdfFileHandler. The file has to be loaded before using this via {@link
   * #loadFile(String)} and no base nor language will be given.
   */
  public RdfFileHandler() {
    this(null, null);
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
    try {
      do {
        if (iter.hasNext()) {
          stmt = iter.next();
        } else {
          return null;
        }
      } while (!stmt.getPredicate().matches(rdfsLabelNode));
    } catch (RiotException rE) {
      throw new IOException(
          "Could not parse file '" + getFileName() + "'. It may be malformed or missing.");
    }
    Node subject = stmt.getSubject();
    Node object = stmt.getObject();
    return new SimpleEntry<>(
        object.getLiteral().getLexicalForm().toLowerCase(),
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
   * loads given file into the handler.
   *
   * @param file file to be processed by the handler
   * @throws IOException if error occurs
   */
  @Override
  public void loadFile(String file) throws IOException {
    this.file = file;
    File f = new File(file);
    if (!f.exists()) {
      throw new IOException("File '" + file + "' was not found.");
    }
    // Setting a node with rdfs:label to filter out triples without it.
    // For some reason I can't declare & define it in the method, there it is here.
    rdfsLabelNode = NodeFactory.createURI((RDFS.label.getURI()));

    /*
     * Create a PipedRDFStream to accept input and a PipedRDFIterator to consume it.
     * You can optionally supply a buffer size here for the PipedRDFIterator, see the documentation
     * for details about recommended buffer sizes
     */
    iter = new PipedRDFIterator<>();
    final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);
    // PipedRDFStream and PipedRDFIterator need to be on different threads
    ExecutorService executor = Executors.newSingleThreadExecutor();

    // Create a runnable for our parser thread
    Runnable parser;
    if (lang != null) {
      Lang langObj = RDFLanguages.nameToLang(lang);
      parser = () -> RDFParser.source(file).base(base).lang(langObj).parse(inputStream);
    } else {
      parser = () -> RDFParser.source(file).base(base).parse(inputStream);
    }
    // Start the parser on another thread
    executor.submit(parser);
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

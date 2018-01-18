package org.aksw.sessa.helper.files.handler;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDFS;

/**
 * Provides handling for RDF-files. This class is an implementation of the interface {@link
 * DictionaryInterface}.
 */
public class RdfFileHandler implements FileHandlerInterface {

  private String file;
  private StmtIterator stmtIterator;

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
    InputStream inputStream = new FileInputStream(file);
    Model model = ModelFactory.createDefaultModel();
    model.read(inputStream, base, lang);
    stmtIterator = model.listStatements(null, RDFS.label, (RDFNode) null);
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
    if (stmtIterator.hasNext()) {
      final Statement stmt = stmtIterator.next();
      Resource subject = stmt.getSubject();
      RDFNode object = stmt.getObject();
      return new SimpleEntry<>(
          object.asLiteral().getString(),
          subject.getURI());
    } else {
      return null;
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

  /**
   * Closes the used readers and releases any system resources associated with it.
   *
   * @throws IOException If an I/O error occurs
   */
  @Override
  public void close() throws IOException {
    stmtIterator.close();
  }
}

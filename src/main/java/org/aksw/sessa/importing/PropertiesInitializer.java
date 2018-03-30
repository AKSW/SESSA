package org.aksw.sessa.importing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesInitializer {

  public static final String DEFAULT_PATH_KEY = "dictionary.files.rdf.location";
  public static final String DEFAULT_PATH = "src/main/resources";
  public static final String LUCENE_OVERRIDE_KEY = "dictionary.lucene.override_on_start";
  public static final String LUCENE_OVERRIDE = "false";
  public static final String LUCENE_LOCATION_KEY = "dictionary.lucene.location";
  public static final String LUCENE_LOCATION = "resources";

  private static final Logger log = LoggerFactory.getLogger(PropertiesInitializer.class);

  public static Properties loadProperties(String location) {
    log.info("Trying to load properties file...");
    Properties properties = new Properties();
    // load default value
    properties.setProperty(DEFAULT_PATH_KEY, DEFAULT_PATH);
    properties.setProperty(LUCENE_OVERRIDE_KEY, LUCENE_OVERRIDE);
    properties.setProperty(LUCENE_LOCATION_KEY, LUCENE_LOCATION);

    // load properties from file
    try (FileInputStream in = new FileInputStream(location)) {
      log.info("Properties file found! Loading values...");
      properties.load(in);
    } catch (FileNotFoundException fnfE) {
      log.info("Properties file not found. Using default values instead.");
      log.debug("Path to properties file was {}", location);
    } catch (IOException ioE) {
      log.error(ioE.getLocalizedMessage());
      log.error("Using default values instead.");
    }
    return properties;
  }

  public static Properties loadDefaultProperties() {
    log.info("No file given. Loading default values only.");
    Properties properties = new Properties();
    // load default value
    properties.setProperty(DEFAULT_PATH_KEY, DEFAULT_PATH);
    properties.setProperty(LUCENE_OVERRIDE_KEY, LUCENE_OVERRIDE);
    properties.setProperty(LUCENE_LOCATION_KEY, LUCENE_LOCATION);
    return properties;
  }


}

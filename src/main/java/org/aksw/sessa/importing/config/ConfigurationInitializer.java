package org.aksw.sessa.importing.config;

import java.io.File;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.OverrideCombiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class builds the Configuration object and provides a singleton of it.
 */
public class ConfigurationInitializer {

  private static final String DEFAULT_CONFIG_FILE = "default.properties";
  private static final Logger log = LoggerFactory.getLogger(ConfigurationInitializer.class);
  public static BaseHierarchicalConfiguration configuration = null;

  public static BaseHierarchicalConfiguration getConfiguration() {
    if (configuration == null) {
      configuration = loadConfiguration();
    }
    return configuration;
  }

  public static BaseHierarchicalConfiguration loadConfiguration() {
    File userSpecifiedConfig;
    if (System.getProperty("configuration.location") == null) {
      userSpecifiedConfig = null;
    } else {
      userSpecifiedConfig = new File(System.getProperty("configuration.location"));
    }
    File defaultConfigFile = new File(
        ConfigurationInitializer.class.getClassLoader().getResource(DEFAULT_CONFIG_FILE)
            .getFile());
    return loadConfiguration(userSpecifiedConfig, defaultConfigFile);
  }


  protected static BaseHierarchicalConfiguration loadConfiguration(File userSpecifiedConfig,
      File defaultConfig) {
    OverrideCombiner combiner = new OverrideCombiner();
    CombinedConfiguration combinedConfig = new CombinedConfiguration(combiner);
    Parameters params = new Parameters();

    // Try to load user-specified configuration first
    if (userSpecifiedConfig != null) {
      log.debug("Loading user-specified properties-file.");
      try {
        FileBasedConfigurationBuilder<FileBasedConfiguration> userBuilder =
            new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(params.properties()
                    .setFile(userSpecifiedConfig)
                    .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
        combinedConfig.addConfiguration(userBuilder.getConfiguration());
      } catch (ConfigurationException cE) {
        log.error("Given file not found. Using only default properties only!");
      }
    } else {
      log.debug("No user-specific configuration defined. Using only default.");
    }
    try {
      // loading default configuration for missing keys
      log.info("Loading default configuration...");
      FileBasedConfigurationBuilder<FileBasedConfiguration> defaultBuilder =
          new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
              .configure(params.properties()
                  .setFile(defaultConfig)
                  .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
      combinedConfig.addConfiguration(defaultBuilder.getConfiguration(), "default");
    } catch (ConfigurationException cE) {
      log.error(cE.getLocalizedMessage());
    } catch (NullPointerException npE) {
      log.error("File {} could not be found within the resources.", DEFAULT_CONFIG_FILE);
    }
    return combinedConfig;
  }

}

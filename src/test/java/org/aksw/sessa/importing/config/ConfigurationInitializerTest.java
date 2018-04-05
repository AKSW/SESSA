package org.aksw.sessa.importing.config;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import java.io.File;
import org.apache.commons.configuration2.Configuration;
import org.junit.Assert;
import org.junit.Test;

public class ConfigurationInitializerTest {

  private final String LUCENE_OVERRIDE_KEY = "dictionary.lucene.override_on_start";
  private final String LUCENE_LOCATION_KEY = "dictionary.lucene.location";
  private final String DICTIONARY_FILTER_KEY = "dictionary.filter.filters";


  @Test
  public void testGetConfiguration_BasicTest() throws Exception {
    Configuration config = ConfigurationInitializer.getConfiguration();
    Assert.assertThat(config.getBoolean(LUCENE_OVERRIDE_KEY), is(notNullValue()));
  }

  @Test
  public void testGetConfiguration_ListTest() throws Exception {
    Configuration config = ConfigurationInitializer.getConfiguration();
    Assert.assertThat(config.getList(DICTIONARY_FILTER_KEY), not(empty()));
  }

  @Test
  public void testGetConfiguration_LoadSecond() throws Exception {
    File userSpecifiedConfig = new File("resources/user_specific.properties");
    File defaultConfigFile = new File("resources/default.properties");

    Configuration config = ConfigurationInitializer
        .loadConfiguration(userSpecifiedConfig, defaultConfigFile);
    Assert.assertThat(config.getBoolean(LUCENE_OVERRIDE_KEY), is(true));
    Assert.assertThat(config.getString(LUCENE_LOCATION_KEY), is("lucene_index"));
  }


}
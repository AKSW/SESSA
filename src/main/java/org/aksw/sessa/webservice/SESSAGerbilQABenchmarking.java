package org.aksw.sessa.webservice;

import org.aksw.sessa.importing.config.exception.MalformedConfigurationException;
import org.dice.qa.QASystem;
import org.dice.webservice.WebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main class for using the web service of SESSA. Mainly only overrides the methods of {link
 * org.dice.webservice.WebApplication Webapplication} with the needed classes of SESSA.
 */
@SpringBootApplication
public class SESSAGerbilQABenchmarking extends WebApplication {

  private static final Logger log = LoggerFactory.getLogger(SESSAGerbilQABenchmarking.class);
  @Bean
  public QASystem createSystem() {
    try {
      return new SESSAGerbilWrapper();
    } catch (MalformedConfigurationException e) {
      log.error(e.getLocalizedMessage());
      log.error("Returning null, System will fail!");
      return null;
    }
  }

  public static void main(final String[] args) {
    SpringApplication.run(SESSAGerbilQABenchmarking.class, args);
  }
}
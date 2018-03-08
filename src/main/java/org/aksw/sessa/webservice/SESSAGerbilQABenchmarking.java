package org.aksw.sessa.webservice;

import org.dice.qa.QASystem;
import org.dice.webservice.WebApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main class for using the web service of SESSA. Mainly only overrides the methods of {link
 * org.dice.webservice.WebApplication Webapplication} with the needed classes of SESSA.
 */
@SpringBootApplication
public class SESSAGerbilQABenchmarking extends WebApplication {

  @Bean
  public QASystem createSystem() {
    return new SESSAGerbilWrapper();
  }

  public static void main(final String[] args) {
    SpringApplication.run(SESSAGerbilQABenchmarking.class, args);
  }
}
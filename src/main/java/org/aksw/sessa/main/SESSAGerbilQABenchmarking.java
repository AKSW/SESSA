package org.aksw.sessa.main;

import org.dice.qa.QASystem;
import org.dice.webservice.WebApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SESSAGerbilQABenchmarking extends WebApplication {

  @Bean
  public QASystem createSystem() {
    /*
		 * This is an Example QA System providing a static response.
		 * Implement your System as a QASystem and create it here
		 *
		 * CREATE YOUR SYSTEM HERE
		 */
    return new SESSAGerbilWrapper();
  }

  public static void main(final String[] args) {
    SpringApplication.run(SESSAGerbilQABenchmarking.class, args);
  }
}
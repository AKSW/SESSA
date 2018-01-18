package org.aksw.sessa.importing.dictionary.energy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;

import org.junit.Assert;
import org.junit.Test;

public class LevenshteinDistanceFunctionTest {

  @Test
  public void testCalculateEnergyScore_CompareExactMatch() {
    EnergyFunctionInterface energyFunction = new LevenshteinDistanceFunction();
    float score1 = energyFunction
        .calculateEnergyScore("stadium", "http://dbpedia.org/resource/Stadium", "stadium");
    Assert.assertThat(score1, equalTo(1.0F));
  }

  @Test
  public void testCalculateEnergyScore_Differences() {
    EnergyFunctionInterface energyFunction = new LevenshteinDistanceFunction();
    float score1 = energyFunction
        .calculateEnergyScore("stadium", "http://dbpedia.org/resource/StadiumX", "stadiumx");
    Assert.assertThat(score1, not(equalTo(1.0F)));
  }

  @Test
  public void testCalculateEnergyScore_Compare1() {
    EnergyFunctionInterface energyFunction = new LevenshteinDistanceFunction();
    float score1 = energyFunction
        .calculateEnergyScore("stadium", "http://dbpedia.org/resource/Stadium", "stadium");
    float score2 = energyFunction
        .calculateEnergyScore("stadium", "http://dbpedia.org/resource/StadiumX", "stadiumx");
    Assert.assertThat(score1, greaterThan(score2));
  }

  @Test
  public void testCalculateEnergyScore_Compare2() {
    EnergyFunctionInterface energyFunction = new LevenshteinDistanceFunction();
    float score1 = energyFunction
        .calculateEnergyScore("stadium", "http://dbpedia.org/resource/Stadium2", "stadium 2");
    float score2 = energyFunction
        .calculateEnergyScore("stadium", "http://dbpedia.org/resource/StadiumX", "stadiumx");
    Assert.assertThat(score1, lessThan(score2));
  }

  @Test
  public void testCalculateEnergyScore_Compare3() {
    EnergyFunctionInterface energyFunction = new LevenshteinDistanceFunction();
    float score1 = energyFunction
        .calculateEnergyScore("stadium", "http://dbpedia.org/resource/Stadium3", "stadium3");
    float score2 = energyFunction
        .calculateEnergyScore("stadium", "http://dbpedia.org/resource/StadiumX", "stadiumx");
    Assert.assertThat(score1, equalTo(score2));
  }


}
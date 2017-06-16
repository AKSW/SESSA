package org.aksw.sessa.query.models;

import org.junit.Test;

/**
 * Created by Simon Bordewisch on 16.06.17.
 */
public class NGramEntryPositionTest {


  @Test
  public void TestGetAllDecendants_VariousValidTests(){
    NGramEntryPosition pos1 = new NGramEntryPosition(0,4);
    NGramEntryPosition[] descandantsPos1 = pos1.getAllDescendants();
    for(NGramEntryPosition pos : descandantsPos1){
      System.out.println(pos.toString());
    }

  }

}

package org.aksw.sessa.query.models;

import java.util.Set;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 16.06.17.
 */
public class NGramEntryPositionTest {


  @Test
  public void TestGetAllDecendants_VariousValidTests(){
    NGramEntryPosition pos1 = new NGramEntryPosition(4, 0);
    Set<NGramEntryPosition> descandantsPos1 = pos1.getAllDescendants();
    for(NGramEntryPosition pos : descandantsPos1){
      System.out.println(pos.toString());
    }

  }

}

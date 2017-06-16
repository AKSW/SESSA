package org.aksw.sessa.query.models;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Simon Bordewisch on 15.06.17.
 */
public class NGramEntryPosition {

  int position;
  int length;

  public NGramEntryPosition(int position, int length){
    this.position = position;
    this.length = length;
  }

  public int getPosition() {
    return position;
  }

  public NGramEntryPosition[] getAllDescendants(){
    NGramEntryPosition[] decendants = new NGramEntryPosition[(length * length +1) / 2 -1];
    return getAllDescendants(this).toArray(decendants);
  }

  private static Set<NGramEntryPosition> getAllDescendants(NGramEntryPosition pos){
    if(pos.getLength() == 1){
      return new HashSet<>();
    } else {
      Set<NGramEntryPosition> decendants = new HashSet<>();
      NGramEntryPosition child1 = new NGramEntryPosition(pos.getPosition(), pos.getLength() - 1);
      NGramEntryPosition child2 = new NGramEntryPosition(pos.getPosition() + 1, pos.getLength() - 1);
      decendants.add(child1);
      decendants.add(child2);
      decendants.addAll(getAllDescendants(child1));
      decendants.addAll(getAllDescendants(child2));
      return decendants;
    }
  }


  public int getLength() {
    return length;
  }

  @Override
  public String toString(){
    return "Entry( position: " + position + ", length: " + length + ")";
  }

  @Override
  public int hashCode() {
    return position * 10000 + length;
  }

  @Override
  public boolean equals(Object other) {
    if(this == other){
      return true;
    }
    if(other.getClass() != this.getClass()){
      return false;
    }
    if(((NGramEntryPosition)other).getLength() != this.getLength()){
      return false;
    }
    if(((NGramEntryPosition)other).getPosition() != this.getPosition()){
      return false;
    }
    return true;

  }



}

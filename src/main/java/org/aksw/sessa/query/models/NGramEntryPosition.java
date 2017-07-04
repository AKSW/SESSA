package org.aksw.sessa.query.models;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a position in the n-gram hierarchy.
 * @author Simon Bordewisch
 */
public class NGramEntryPosition {

  /**
   * Defines the length of the n-gram.
   */
  private int length;

  /**
   * Defines the position in it's "row".
   * All n-grams with the same length are in the same row.
   */
  private int position;


  /**
   * Initializes object with length and position in the "row".
   * I.e. if e.g. length=2, position=2, this object would
   * represent the third bigram.
   * If the whole n-gram is "birthplace bill gates wife",
   * the object would represent "gates wife".
   * @param length length of the n-gram
   * @param position position in the "row"
   */
  public NGramEntryPosition(int length, int position){
    this.position = position;
    this.length = length;
  }

  public int getPosition() {
    return position;
  }

  /**
   * Returns a set of all positional information of descendants of this n-gram entry.
   * @return all positional informtion of descendats of this entry
   */
  public Set<NGramEntryPosition> getAllDescendants(){
    return getAllDescendants(this);
  }

  private static Set<NGramEntryPosition> getAllDescendants(NGramEntryPosition pos){
    if(pos.getLength() == 1){
      return new HashSet<>();
    } else {
      Set<NGramEntryPosition> decendants = new HashSet<>();
      NGramEntryPosition child1 = new NGramEntryPosition(pos.getLength() - 1, pos.getPosition());
      NGramEntryPosition child2 = new NGramEntryPosition(pos.getLength() - 1, pos.getPosition() + 1);
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

  /**
   * Override for hashcode to get a good and easy hash for the entries.
   * As long as the n-gram does not contain more than 10000 words, it should be unique.
   * @return hash representation of the n-gram position
   */
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

package org.aksw.sessa.query.models;

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

  public int getLength() {
    return length;
  }


}

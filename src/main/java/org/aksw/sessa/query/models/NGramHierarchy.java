package org.aksw.sessa.query.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class represents the n-gram hierarchy.
 * @author Simon Bordewisch
 */
public class NGramHierarchy {

  private String[] ngram;

  /**
   * Initializes with already splitted n-gram.
   * The split has to be between the words.
   * E.g. "birthplace bill gates" should become ["birthplace", "bill", "gates"].
   * @param ngram already splitted n-gram
   */
  public NGramHierarchy(String[] ngram){
    this.ngram = ngram;
  }

  /**
   * Initializes with n-gram sentences in normal String-representation.
   * @param ngram n-gram in String-representation
   */
  public NGramHierarchy(String ngram){
    this.ngram = ngram.split(" ");
  }

  /**
   * Returns the n-gram for which the positional data was given.
   * @param pos the n-gram entry position
   * @return n-gram at the given position
   */
 public String getNGram(NGramEntryPosition pos){
    return getNGram(pos.getPosition(), pos.getLength());
  }

  /**
   * Returns the n-gram for which the positional data was given.
   * @param index represents the position within the n-gram of given length
   * @param length represents the length of the n-gram, e.g. length=2 is a bigram
   * @return n-gram at the given position
   */
  public String getNGram(int index, int length){
    if(length == 1){
      return ngram[index];
    } else {
      StringBuilder sb = new StringBuilder();
      for(int i=index;i<length+index-1;i++){
        sb.append(ngram[i] + " ");
      }
      sb.append(ngram[length+index-1]);
      return sb.toString();
    }
  }


  /**
   * Returns the parents of given n-gram.
   * @param index represents the position within the n-gram of given length
   * @param length represents the length of the n-gram, e.g. length=2 is a bigram
   * @return parents of given n-gram
   */
  public String[] getParents(int index, int length){
    String parents[];
    if (index == 0){
      if (index + length == ngram.length){
        return null;
      } else {
        String parent = getNGram(index, length + 1);
        parents = new String[1];
        parents[0] = parent;
      }
    } else if (index + length == ngram.length) {
      String parent = getNGram(index-1, length + 1);
      parents = new String[1];
      parents[0] = parent;
    } else {
      String parent1 = getNGram(index-1, length + 1);
      String parent2 = getNGram(index, length + 1);
      parents = new String[2];
      parents[0] = parent1;
      parents[1] = parent2;
    }
    return parents;
  }


  /**
   * Given the position and length for a n-gram, returns direct children,
   * i.e. the children directly connected to the n-gram within the n-gram hierarchy.
   * @param index represents the position within the n-gram of given length
   * @param length represents the length of the n-gram, e.g. length=2 is a bigram
   * @return directly connected children
   */
  public String[] getDirectChildren(int index, int length){
    if (length==1) {
      return null;
    } else {
      String[] children = new String[2];
      children[0] = getNGram( index, length - 1);
      children[1] = getNGram(index+1, length - 1);
      return children;
    }
  }

  /**
   * Returns the whole n-gram hierarchy sorted by length, then by position.
   * E.g. "birthplace bill gates" would return
   * ["birthplace bill gates", "birthplace bill", "bill gates", "birthplace", "bill", "gates"]
   * @return n-gram hierarchy represented as array
   */
  public String[] toStringArray(){
    String[] hierarchy = new String[(ngram.length*(ngram.length+1))/2];
    int hierarchyIndex = 0;
    for(int l = ngram.length; l > 0; l--)
    {
      for(int i = 0; i+l<=ngram.length; i++) {
        hierarchy[hierarchyIndex] = getNGram(i,l);
        hierarchyIndex++;
      }
    }
    return  hierarchy;
  }

  /**
   * Returns length of initial n-gram,
   * i.e. how many words it has.
   * @return number of words within the initial n-gram
   */
  public int getNGramLength(){
    return ngram.length;
  }

}

package org.aksw.sessa.query.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Simon Bordewisch on 02.06.17.
 */
public class NGramHierarchy {

  private String[] ngram;

  public NGramHierarchy(String[] ngram){
    this.ngram = ngram;
  }

  public NGramHierarchy(String ngram){
    this.ngram = ngram.split(" ");
  }


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

  public int getNGramLength(){
    return ngram.length;
  }

}
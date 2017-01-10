package org.lucene46.search;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;

/**
 * The Scorer for DisjunctionMaxQuery.  The union of all documents generated by the the subquery scorers
 * is generated in document number order.  The score for each document is the maximum of the scores computed
 * by the subquery scorers that generate that document, plus tieBreakerMultiplier times the sum of the scores
 * for the other subqueries that generate the document.
 */
class DisjunctionMaxScorer extends DisjunctionScorer {
  /* Multiplier applied to non-maximum-scoring subqueries for a document as they are summed into the result. */
  private final float tieBreakerMultiplier;
  private int freq = -1;

  /* Used when scoring currently matching doc. */
  private float scoreSum;
  private float scoreMax;

  /**
   * Creates a new instance of DisjunctionMaxScorer
   * 
   * @param weight
   *          The Weight to be used.
   * @param tieBreakerMultiplier
   *          Multiplier applied to non-maximum-scoring subqueries for a
   *          document as they are summed into the result.
   * @param subScorers
   *          The sub scorers this Scorer should iterate on
   */
  public DisjunctionMaxScorer(Weight weight, float tieBreakerMultiplier,
      Scorer[] subScorers) {
    super(weight, subScorers);
    this.tieBreakerMultiplier = tieBreakerMultiplier;
  }

  /** Determine the current document score.  Initially invalid, until {@link #nextDoc()} is called the first time.
   * @return the score of the current generated document
   */
  @Override
  public float score() throws IOException {
    return scoreMax + (scoreSum - scoreMax) * tieBreakerMultiplier;
  }
  
  @Override
  protected void afterNext() throws IOException {
    doc = subScorers[0].docID();
    if (doc != NO_MORE_DOCS) {
      scoreSum = scoreMax = subScorers[0].score();
      freq = 1;
      scoreAll(1);
      scoreAll(2);
    }
  }

  // Recursively iterate all subScorers that generated last doc computing sum and max
  private void scoreAll(int root) throws IOException {
    if (root < numScorers && subScorers[root].docID() == doc) {
      float sub = subScorers[root].score();
      freq++;
      scoreSum += sub;
      scoreMax = Math.max(scoreMax, sub);
      scoreAll((root<<1)+1);
      scoreAll((root<<1)+2);
    }
  }

  @Override
  public int freq() throws IOException {
    return freq;
  }
}

package org.lucene46.index;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.lucene46.index.MergePolicy.MergeTrigger;
import org.lucene46.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/** This {@link MergePolicy} is used for upgrading all existing segments of
  * an index when calling {@link IndexWriter#forceMerge(int)}.
  * All other methods delegate to the base {@code MergePolicy} given to the constructor.
  * This allows for an as-cheap-as possible upgrade of an older index by only upgrading segments that
  * are created by previous Lucene versions. forceMerge does no longer really merge;
  * it is just used to &quot;forceMerge&quot; older segment versions away.
  * <p>In general one would use {@link IndexUpgrader}, but for a fully customizeable upgrade,
  * you can use this like any other {@code MergePolicy} and call {@link IndexWriter#forceMerge(int)}:
  * <pre class="prettyprint lang-java">
  *  IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_XX, new KeywordAnalyzer());
  *  iwc.setMergePolicy(new UpgradeIndexMergePolicy(iwc.getMergePolicy()));
  *  IndexWriter w = new IndexWriter(dir, iwc);
  *  w.forceMerge(1);
  *  w.close();
  * </pre>
  * <p><b>Warning:</b> This merge policy may reorder documents if the index was partially
  * upgraded before calling forceMerge (e.g., documents were added). If your application relies
  * on &quot;monotonicity&quot; of doc IDs (which means that the order in which the documents
  * were added to the index is preserved), do a forceMerge(1) instead. Please note, the
  * delegate {@code MergePolicy} may also reorder documents.
  * @lucene.experimental
  * @see IndexUpgrader
  */
public class UpgradeIndexMergePolicy extends MergePolicy {

  /** Wrapped {@link MergePolicy}. */
  protected final MergePolicy base;

  /** Wrap the given {@link MergePolicy} and intercept forceMerge requests to
   * only upgrade segments written with previous Lucene versions. */
  public UpgradeIndexMergePolicy(MergePolicy base) {
    this.base = base;
  }
  
  /** Returns if the given segment should be upgraded. The default implementation
   * will return {@code !Constants.LUCENE_MAIN_VERSION.equals(si.getVersion())},
   * so all segments created with a different version number than this Lucene version will
   * get upgraded.
   */
  protected boolean shouldUpgradeSegment(SegmentCommitInfo si) {
    return !Constants.LUCENE_MAIN_VERSION.equals(si.info.getVersion());
  }

  @Override
  public void setIndexWriter(IndexWriter writer) {
    super.setIndexWriter(writer);
    base.setIndexWriter(writer);
  }
  
  @Override
  public MergeSpecification findMerges(MergeTrigger mergeTrigger, SegmentInfos segmentInfos) throws IOException {
    return base.findMerges(null, segmentInfos);
  }
  
  @Override
  public MergeSpecification findForcedMerges(SegmentInfos segmentInfos, int maxSegmentCount, Map<SegmentCommitInfo,Boolean> segmentsToMerge) throws IOException {
    // first find all old segments
    final Map<SegmentCommitInfo,Boolean> oldSegments = new HashMap<SegmentCommitInfo,Boolean>();
    for (final SegmentCommitInfo si : segmentInfos) {
      final Boolean v = segmentsToMerge.get(si);
      if (v != null && shouldUpgradeSegment(si)) {
        oldSegments.put(si, v);
      }
    }
    
    if (verbose()) {
      message("findForcedMerges: segmentsToUpgrade=" + oldSegments);
    }
      
    if (oldSegments.isEmpty())
      return null;

    MergeSpecification spec = base.findForcedMerges(segmentInfos, maxSegmentCount, oldSegments);
    
    if (spec != null) {
      // remove all segments that are in merge specification from oldSegments,
      // the resulting set contains all segments that are left over
      // and will be merged to one additional segment:
      for (final OneMerge om : spec.merges) {
        oldSegments.keySet().removeAll(om.segments);
      }
    }

    if (!oldSegments.isEmpty()) {
      if (verbose()) {
        message("findForcedMerges: " +  base.getClass().getSimpleName() +
        " does not want to merge all old segments, merge remaining ones into new segment: " + oldSegments);
      }
      final List<SegmentCommitInfo> newInfos = new ArrayList<SegmentCommitInfo>();
      for (final SegmentCommitInfo si : segmentInfos) {
        if (oldSegments.containsKey(si)) {
          newInfos.add(si);
        }
      }
      // add the final merge
      if (spec == null) {
        spec = new MergeSpecification();
      }
      spec.add(new OneMerge(newInfos));
    }

    return spec;
  }
  
  @Override
  public MergeSpecification findForcedDeletesMerges(SegmentInfos segmentInfos) throws IOException {
    return base.findForcedDeletesMerges(segmentInfos);
  }
  
  @Override
  public boolean useCompoundFile(SegmentInfos segments, SegmentCommitInfo newSegment) throws IOException {
    return base.useCompoundFile(segments, newSegment);
  }
  
  @Override
  public void close() {
    base.close();
  }
  
  @Override
  public String toString() {
    return "[" + getClass().getSimpleName() + "->" + base + "]";
  }
  
  private boolean verbose() {
    final IndexWriter w = writer.get();
    return w != null && w.infoStream.isEnabled("UPGMP");
  }

  private void message(String message) {
    writer.get().infoStream.message("UPGMP", message);
  }
}

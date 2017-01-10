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

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.lucene46.codecs.DocValuesConsumer;
import org.lucene46.util.Counter;
import org.lucene46.util.OpenBitSet;
import org.lucene46.util.RamUsageEstimator;
import org.lucene46.util.packed.AppendingDeltaPackedLongBuffer;
import org.lucene46.util.packed.PackedInts;

/** Buffers up pending long per doc, then flushes when
 *  segment flushes. */
class NumericDocValuesWriter extends DocValuesWriter {

  private final static long MISSING = 0L;

  private AppendingDeltaPackedLongBuffer pending;
  private final Counter iwBytesUsed;
  private long bytesUsed;
  private final OpenBitSet docsWithField;
  private final FieldInfo fieldInfo;
  private final boolean trackDocsWithField;

  public NumericDocValuesWriter(FieldInfo fieldInfo, Counter iwBytesUsed, boolean trackDocsWithField) {
    pending = new AppendingDeltaPackedLongBuffer(PackedInts.COMPACT);
    docsWithField = new OpenBitSet();
    bytesUsed = pending.ramBytesUsed() + docsWithFieldBytesUsed();
    this.fieldInfo = fieldInfo;
    this.iwBytesUsed = iwBytesUsed;
    iwBytesUsed.addAndGet(bytesUsed);
    this.trackDocsWithField = trackDocsWithField;
  }

  public void addValue(int docID, long value) {
    if (docID < pending.size()) {
      throw new IllegalArgumentException("DocValuesField \"" + fieldInfo.name + "\" appears more than once in this document (only one value is allowed per field)");
    }

    // Fill in any holes:
    for (int i = (int)pending.size(); i < docID; ++i) {
      pending.add(MISSING);
    }

    pending.add(value);
    if (trackDocsWithField) {
      docsWithField.set(docID);
    }

    updateBytesUsed();
  }
  
  private long docsWithFieldBytesUsed() {
    // size of the long[] + some overhead
    return RamUsageEstimator.sizeOf(docsWithField.getBits()) + 64;
  }

  private void updateBytesUsed() {
    final long newBytesUsed = pending.ramBytesUsed() + docsWithFieldBytesUsed();
    iwBytesUsed.addAndGet(newBytesUsed - bytesUsed);
    bytesUsed = newBytesUsed;
  }

  @Override
  public void finish(int maxDoc) {
  }

  @Override
  public void flush(SegmentWriteState state, DocValuesConsumer dvConsumer) throws IOException {

    final int maxDoc = state.segmentInfo.getDocCount();

    dvConsumer.addNumericField(fieldInfo,
                               new Iterable<Number>() {
                                 @Override
                                 public Iterator<Number> iterator() {
                                   return new NumericIterator(maxDoc);
                                 }
                               });
  }

  @Override
  public void abort() {
  }
  
  // iterates over the values we have in ram
  private class NumericIterator implements Iterator<Number> {
    final AppendingDeltaPackedLongBuffer.Iterator iter = pending.iterator();
    final int size = (int)pending.size();
    final int maxDoc;
    int upto;
    
    NumericIterator(int maxDoc) {
      this.maxDoc = maxDoc;
    }
    
    @Override
    public boolean hasNext() {
      return upto < maxDoc;
    }

    @Override
    public Number next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      Long value;
      if (upto < size) {
        long v = iter.next();
        if (!trackDocsWithField || docsWithField.get(upto)) {
          value = v;
        } else {
          value = null;
        }
      } else {
        value = trackDocsWithField ? null : MISSING;
      }
      upto++;
      return value;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}

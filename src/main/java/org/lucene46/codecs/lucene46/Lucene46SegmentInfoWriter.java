package org.lucene46.codecs.lucene46;

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

import org.lucene46.codecs.CodecUtil;
import org.lucene46.codecs.SegmentInfoWriter;
import org.lucene46.index.FieldInfos;
import org.lucene46.index.IndexFileNames;
import org.lucene46.index.SegmentInfo;
import org.lucene46.store.Directory;
import org.lucene46.store.IOContext;
import org.lucene46.store.IndexOutput;
import org.lucene46.util.IOUtils;

/**
 * Lucene 4.0 implementation of {@link SegmentInfoWriter}.
 * 
 * @see Lucene46SegmentInfoFormat
 * @lucene.experimental
 */
public class Lucene46SegmentInfoWriter extends SegmentInfoWriter {

  /** Sole constructor. */
  public Lucene46SegmentInfoWriter() {
  }

  /** Save a single segment's info. */
  @Override
  public void write(Directory dir, SegmentInfo si, FieldInfos fis, IOContext ioContext) throws IOException {
    final String fileName = IndexFileNames.segmentFileName(si.name, "", Lucene46SegmentInfoFormat.SI_EXTENSION);
    si.addFile(fileName);

    final IndexOutput output = dir.createOutput(fileName, ioContext);

    boolean success = false;
    try {
      CodecUtil.writeHeader(output, Lucene46SegmentInfoFormat.CODEC_NAME, Lucene46SegmentInfoFormat.VERSION_CURRENT);
      // Write the Lucene version that created this segment, since 3.1
      output.writeString(si.getVersion());
      output.writeInt(si.getDocCount());

      output.writeByte((byte) (si.getUseCompoundFile() ? SegmentInfo.YES : SegmentInfo.NO));
      output.writeStringStringMap(si.getDiagnostics());
      output.writeStringSet(si.files());

      success = true;
    } finally {
      if (!success) {
        IOUtils.closeWhileHandlingException(output);
        si.dir.deleteFile(fileName);
      } else {
        output.close();
      }
    }
  }
}

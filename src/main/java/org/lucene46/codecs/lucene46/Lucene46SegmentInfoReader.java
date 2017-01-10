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
import java.util.Map;
import java.util.Set;

import org.lucene46.codecs.CodecUtil;
import org.lucene46.codecs.SegmentInfoReader;
import org.lucene46.index.CorruptIndexException;
import org.lucene46.index.IndexFileNames;
import org.lucene46.index.SegmentInfo;
import org.lucene46.store.Directory;
import org.lucene46.store.IOContext;
import org.lucene46.store.IndexInput;
import org.lucene46.util.IOUtils;

/**
 * Lucene 4.6 implementation of {@link SegmentInfoReader}.
 * 
 * @see Lucene46SegmentInfoFormat
 * @lucene.experimental
 */
public class Lucene46SegmentInfoReader extends SegmentInfoReader {

  /** Sole constructor. */
  public Lucene46SegmentInfoReader() {
  }

  @Override
  public SegmentInfo read(Directory dir, String segment, IOContext context) throws IOException {
    final String fileName = IndexFileNames.segmentFileName(segment, "", Lucene46SegmentInfoFormat.SI_EXTENSION);
    final IndexInput input = dir.openInput(fileName, context);
    boolean success = false;
    try {
      CodecUtil.checkHeader(input, Lucene46SegmentInfoFormat.CODEC_NAME,
                                   Lucene46SegmentInfoFormat.VERSION_START,
                                   Lucene46SegmentInfoFormat.VERSION_CURRENT);
      final String version = input.readString();
      final int docCount = input.readInt();
      if (docCount < 0) {
        throw new CorruptIndexException("invalid docCount: " + docCount + " (resource=" + input + ")");
      }
      final boolean isCompoundFile = input.readByte() == SegmentInfo.YES;
      final Map<String,String> diagnostics = input.readStringStringMap();
      final Set<String> files = input.readStringSet();
      
      if (input.getFilePointer() != input.length()) {
        throw new CorruptIndexException("did not read all bytes from file \"" + fileName + "\": read " + input.getFilePointer() + " vs size " + input.length() + " (resource: " + input + ")");
      }

      final SegmentInfo si = new SegmentInfo(dir, version, segment, docCount, isCompoundFile, null, diagnostics);
      si.setFiles(files);

      success = true;

      return si;

    } finally {
      if (!success) {
        IOUtils.closeWhileHandlingException(input);
      } else {
        input.close();
      }
    }
  }
}

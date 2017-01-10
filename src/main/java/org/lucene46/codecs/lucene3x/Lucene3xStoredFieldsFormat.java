package org.lucene46.codecs.lucene3x;

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

import org.lucene46.codecs.StoredFieldsFormat;
import org.lucene46.codecs.StoredFieldsReader;
import org.lucene46.codecs.StoredFieldsWriter;
import org.lucene46.index.FieldInfos;
import org.lucene46.index.SegmentInfo;
import org.lucene46.store.Directory;
import org.lucene46.store.IOContext;

/** @deprecated Only for reading existing 3.x indexes */
@Deprecated
class Lucene3xStoredFieldsFormat extends StoredFieldsFormat {

  @Override
  public StoredFieldsReader fieldsReader(Directory directory, SegmentInfo si,
      FieldInfos fn, IOContext context) throws IOException {
    return new Lucene3xStoredFieldsReader(directory, si, fn, context);
  }

  @Override
  public StoredFieldsWriter fieldsWriter(Directory directory, SegmentInfo si,
      IOContext context) throws IOException {
    throw new UnsupportedOperationException("this codec can only be used for reading");
  }
}

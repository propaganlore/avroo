/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.logicaltypes;

import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.IndexedRecord;

/**
 * Used to read Records.
 *
 */
public class AvroRecord implements AvroDatatype {
  public static final String NAME = "RECORD";
  static final AvroRecord element = new AvroRecord();

  private AvroRecord() {
    super();
  }

  public static AvroRecord create() {
    return element;
  }

  @Override
  public IndexedRecord convertToRawType(Object value) {
    if (value == null) {
      return null;
    } else if (value instanceof IndexedRecord) {
      return (IndexedRecord) value;
    }
    throw new AvroTypeException("Cannot convert a value of type \"" + value.getClass().getSimpleName()
        + "\" into a IndexedRecord (SpecificRecord, GenericRecord)");
  }

  @Override
  public IndexedRecord convertToLogicalType(Object value) {
    if (value == null) {
      return null;
    } else if (value instanceof IndexedRecord) {
      return (IndexedRecord) value;
    }
    throw new AvroTypeException("Cannot convert a value of type \"" + value.getClass().getSimpleName()
        + "\" into a IndexedRecord (SpecificRecord, GenericRecord)");
  }

  @Override
  public Type getBackingType() {
    return Type.RECORD;
  }

  @Override
  public Schema getRecommendedSchema() {
    return null;
  }

  @Override
  public AvroType getAvroType() {
    return AvroType.AVRORECORD;
  }

  @Override
  public String toString() {
    return NAME;
  }

  @Override
  public Class<?> getConvertedType() {
    return IndexedRecord.class;
  }

}

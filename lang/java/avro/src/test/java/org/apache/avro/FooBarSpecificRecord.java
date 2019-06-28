/*
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 *
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
package org.apache.avro;

import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;
import org.apache.avro.specific.SpecificData;

@org.apache.avro.specific.AvroGenerated
public class FooBarSpecificRecord extends org.apache.avro.specific.SpecificRecordBase
    implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 1031933828916876443L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
      "{\"type\":\"record\",\"name\":\"FooBarSpecificRecord\",\"namespace\":\"org.apache.avro\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"nicknames\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}},{\"name\":\"relatedids\",\"type\":{\"type\":\"array\",\"items\":\"int\"}},{\"name\":\"typeEnum\",\"type\":[\"null\",{\"type\":\"enum\",\"name\":\"TypeEnum\",\"symbols\":[\"a\",\"b\",\"c\"]}],\"default\":null}]}");

  public static org.apache.avro.Schema getClassSchema() {
    return SCHEMA$;
  }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<FooBarSpecificRecord> ENCODER = new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<FooBarSpecificRecord> DECODER = new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * 
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<FooBarSpecificRecord> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the
   * specified {@link SchemaStore}.
   * 
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given
   *         SchemaStore
   */
  public static BinaryMessageDecoder<FooBarSpecificRecord> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this FooBarSpecificRecord to a ByteBuffer.
   * 
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a FooBarSpecificRecord from a ByteBuffer.
   * 
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a FooBarSpecificRecord instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into
   *         an instance of this class
   */
  public static FooBarSpecificRecord fromByteBuffer(java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated
  public int id;
  @Deprecated
  public java.lang.String name;
  @Deprecated
  public java.util.List<java.lang.String> nicknames;
  @Deprecated
  public java.util.List<java.lang.Integer> relatedids;
  @Deprecated
  public org.apache.avro.TypeEnum typeEnum;

  /**
   * Default constructor. Note that this does not initialize fields to their
   * default values from the schema. If that is desired then one should use
   * <code>newBuilder()</code>.
   */
  public FooBarSpecificRecord() {
  }

  /**
   * All-args constructor.
   * 
   * @param id         The new value for id
   * @param name       The new value for name
   * @param nicknames  The new value for nicknames
   * @param relatedids The new value for relatedids
   * @param typeEnum   The new value for typeEnum
   */
  public FooBarSpecificRecord(java.lang.Integer id, java.lang.String name, java.util.List<java.lang.String> nicknames,
      java.util.List<java.lang.Integer> relatedids, org.apache.avro.TypeEnum typeEnum) {
    this.id = id;
    this.name = name;
    this.nicknames = nicknames;
    this.relatedids = relatedids;
    this.typeEnum = typeEnum;
  }

  @Override
  public org.apache.avro.Schema getSchema() {
    return SCHEMA$;
  }

  // Used by DatumWriter. Applications should not call.
  @Override
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0:
      return id;
    case 1:
      return name;
    case 2:
      return nicknames;
    case 3:
      return relatedids;
    case 4:
      return typeEnum;
    default:
      throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader. Applications should not call.
  @SuppressWarnings(value = "unchecked")
  @Override
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0:
      id = (java.lang.Integer) value$;
      break;
    case 1:
      name = (java.lang.String) value$;
      break;
    case 2:
      nicknames = (java.util.List<java.lang.String>) value$;
      break;
    case 3:
      relatedids = (java.util.List<java.lang.Integer>) value$;
      break;
    case 4:
      typeEnum = (org.apache.avro.TypeEnum) value$;
      break;
    default:
      throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'id' field.
   * 
   * @return The value of the 'id' field.
   */
  public java.lang.Integer getId() {
    return id;
  }

  /**
   * Sets the value of the 'id' field.
   * 
   * @param value the value to set.
   */
  public void setId(java.lang.Integer value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'name' field.
   * 
   * @return The value of the 'name' field.
   */
  public java.lang.String getName() {
    return name;
  }

  /**
   * Sets the value of the 'name' field.
   * 
   * @param value the value to set.
   */
  public void setName(java.lang.String value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'nicknames' field.
   * 
   * @return The value of the 'nicknames' field.
   */
  public java.util.List<java.lang.String> getNicknames() {
    return nicknames;
  }

  /**
   * Sets the value of the 'nicknames' field.
   * 
   * @param value the value to set.
   */
  public void setNicknames(java.util.List<java.lang.String> value) {
    this.nicknames = value;
  }

  /**
   * Gets the value of the 'relatedids' field.
   * 
   * @return The value of the 'relatedids' field.
   */
  public java.util.List<java.lang.Integer> getRelatedids() {
    return relatedids;
  }

  /**
   * Sets the value of the 'relatedids' field.
   * 
   * @param value the value to set.
   */
  public void setRelatedids(java.util.List<java.lang.Integer> value) {
    this.relatedids = value;
  }

  /**
   * Gets the value of the 'typeEnum' field.
   * 
   * @return The value of the 'typeEnum' field.
   */
  public org.apache.avro.TypeEnum getTypeEnum() {
    return typeEnum;
  }

  /**
   * Sets the value of the 'typeEnum' field.
   * 
   * @param value the value to set.
   */
  public void setTypeEnum(org.apache.avro.TypeEnum value) {
    this.typeEnum = value;
  }

  /**
   * Creates a new FooBarSpecificRecord RecordBuilder.
   * 
   * @return A new FooBarSpecificRecord RecordBuilder
   */
  public static org.apache.avro.FooBarSpecificRecord.Builder newBuilder() {
    return new org.apache.avro.FooBarSpecificRecord.Builder();
  }

  /**
   * Creates a new FooBarSpecificRecord RecordBuilder by copying an existing
   * Builder.
   * 
   * @param other The existing builder to copy.
   * @return A new FooBarSpecificRecord RecordBuilder
   */
  public static org.apache.avro.FooBarSpecificRecord.Builder newBuilder(
      org.apache.avro.FooBarSpecificRecord.Builder other) {
    if (other == null) {
      return new org.apache.avro.FooBarSpecificRecord.Builder();
    } else {
      return new org.apache.avro.FooBarSpecificRecord.Builder(other);
    }
  }

  /**
   * Creates a new FooBarSpecificRecord RecordBuilder by copying an existing
   * FooBarSpecificRecord instance.
   * 
   * @param other The existing instance to copy.
   * @return A new FooBarSpecificRecord RecordBuilder
   */
  public static org.apache.avro.FooBarSpecificRecord.Builder newBuilder(org.apache.avro.FooBarSpecificRecord other) {
    if (other == null) {
      return new org.apache.avro.FooBarSpecificRecord.Builder();
    } else {
      return new org.apache.avro.FooBarSpecificRecord.Builder(other);
    }
  }

  /**
   * RecordBuilder for FooBarSpecificRecord instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<FooBarSpecificRecord>
      implements org.apache.avro.data.RecordBuilder<FooBarSpecificRecord> {

    private int id;
    private java.lang.String name;
    private java.util.List<java.lang.String> nicknames;
    private java.util.List<java.lang.Integer> relatedids;
    private org.apache.avro.TypeEnum typeEnum;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * 
     * @param other The existing Builder to copy.
     */
    private Builder(org.apache.avro.FooBarSpecificRecord.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.name)) {
        this.name = data().deepCopy(fields()[1].schema(), other.name);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.nicknames)) {
        this.nicknames = data().deepCopy(fields()[2].schema(), other.nicknames);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.relatedids)) {
        this.relatedids = data().deepCopy(fields()[3].schema(), other.relatedids);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.typeEnum)) {
        this.typeEnum = data().deepCopy(fields()[4].schema(), other.typeEnum);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
    }

    /**
     * Creates a Builder by copying an existing FooBarSpecificRecord instance
     * 
     * @param other The existing instance to copy.
     */
    private Builder(org.apache.avro.FooBarSpecificRecord other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.name)) {
        this.name = data().deepCopy(fields()[1].schema(), other.name);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.nicknames)) {
        this.nicknames = data().deepCopy(fields()[2].schema(), other.nicknames);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.relatedids)) {
        this.relatedids = data().deepCopy(fields()[3].schema(), other.relatedids);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.typeEnum)) {
        this.typeEnum = data().deepCopy(fields()[4].schema(), other.typeEnum);
        fieldSetFlags()[4] = true;
      }
    }

    /**
     * Gets the value of the 'id' field.
     * 
     * @return The value.
     */
    public java.lang.Integer getId() {
      return id;
    }

    /**
     * Sets the value of the 'id' field.
     * 
     * @param value The value of 'id'.
     * @return This builder.
     */
    public org.apache.avro.FooBarSpecificRecord.Builder setId(int value) {
      validate(fields()[0], value);
      this.id = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
     * Checks whether the 'id' field has been set.
     * 
     * @return True if the 'id' field has been set, false otherwise.
     */
    public boolean hasId() {
      return fieldSetFlags()[0];
    }

    /**
     * Clears the value of the 'id' field.
     * 
     * @return This builder.
     */
    public org.apache.avro.FooBarSpecificRecord.Builder clearId() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
     * Gets the value of the 'name' field.
     * 
     * @return The value.
     */
    public java.lang.String getName() {
      return name;
    }

    /**
     * Sets the value of the 'name' field.
     * 
     * @param value The value of 'name'.
     * @return This builder.
     */
    public org.apache.avro.FooBarSpecificRecord.Builder setName(java.lang.String value) {
      validate(fields()[1], value);
      this.name = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
     * Checks whether the 'name' field has been set.
     * 
     * @return True if the 'name' field has been set, false otherwise.
     */
    public boolean hasName() {
      return fieldSetFlags()[1];
    }

    /**
     * Clears the value of the 'name' field.
     * 
     * @return This builder.
     */
    public org.apache.avro.FooBarSpecificRecord.Builder clearName() {
      name = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
     * Gets the value of the 'nicknames' field.
     * 
     * @return The value.
     */
    public java.util.List<java.lang.String> getNicknames() {
      return nicknames;
    }

    /**
     * Sets the value of the 'nicknames' field.
     * 
     * @param value The value of 'nicknames'.
     * @return This builder.
     */
    public org.apache.avro.FooBarSpecificRecord.Builder setNicknames(java.util.List<java.lang.String> value) {
      validate(fields()[2], value);
      this.nicknames = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
     * Checks whether the 'nicknames' field has been set.
     * 
     * @return True if the 'nicknames' field has been set, false otherwise.
     */
    public boolean hasNicknames() {
      return fieldSetFlags()[2];
    }

    /**
     * Clears the value of the 'nicknames' field.
     * 
     * @return This builder.
     */
    public org.apache.avro.FooBarSpecificRecord.Builder clearNicknames() {
      nicknames = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
     * Gets the value of the 'relatedids' field.
     * 
     * @return The value.
     */
    public java.util.List<java.lang.Integer> getRelatedids() {
      return relatedids;
    }

    /**
     * Sets the value of the 'relatedids' field.
     * 
     * @param value The value of 'relatedids'.
     * @return This builder.
     */
    public org.apache.avro.FooBarSpecificRecord.Builder setRelatedids(java.util.List<java.lang.Integer> value) {
      validate(fields()[3], value);
      this.relatedids = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
     * Checks whether the 'relatedids' field has been set.
     * 
     * @return True if the 'relatedids' field has been set, false otherwise.
     */
    public boolean hasRelatedids() {
      return fieldSetFlags()[3];
    }

    /**
     * Clears the value of the 'relatedids' field.
     * 
     * @return This builder.
     */
    public org.apache.avro.FooBarSpecificRecord.Builder clearRelatedids() {
      relatedids = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
     * Gets the value of the 'typeEnum' field.
     * 
     * @return The value.
     */
    public org.apache.avro.TypeEnum getTypeEnum() {
      return typeEnum;
    }

    /**
     * Sets the value of the 'typeEnum' field.
     * 
     * @param value The value of 'typeEnum'.
     * @return This builder.
     */
    public org.apache.avro.FooBarSpecificRecord.Builder setTypeEnum(org.apache.avro.TypeEnum value) {
      validate(fields()[4], value);
      this.typeEnum = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
     * Checks whether the 'typeEnum' field has been set.
     * 
     * @return True if the 'typeEnum' field has been set, false otherwise.
     */
    public boolean hasTypeEnum() {
      return fieldSetFlags()[4];
    }

    /**
     * Clears the value of the 'typeEnum' field.
     * 
     * @return This builder.
     */
    public org.apache.avro.FooBarSpecificRecord.Builder clearTypeEnum() {
      typeEnum = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public FooBarSpecificRecord build() {
      try {
        FooBarSpecificRecord record = new FooBarSpecificRecord();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.Integer) defaultValue(fields()[0]);
        record.name = fieldSetFlags()[1] ? this.name : (java.lang.String) defaultValue(fields()[1]);
        record.nicknames = fieldSetFlags()[2] ? this.nicknames
            : (java.util.List<java.lang.String>) defaultValue(fields()[2]);
        record.relatedids = fieldSetFlags()[3] ? this.relatedids
            : (java.util.List<java.lang.Integer>) defaultValue(fields()[3]);
        record.typeEnum = fieldSetFlags()[4] ? this.typeEnum : (org.apache.avro.TypeEnum) defaultValue(fields()[4]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<FooBarSpecificRecord> WRITER$ = (org.apache.avro.io.DatumWriter<FooBarSpecificRecord>) MODEL$
      .createDatumWriter(SCHEMA$);

  @Override
  public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<FooBarSpecificRecord> READER$ = (org.apache.avro.io.DatumReader<FooBarSpecificRecord>) MODEL$
      .createDatumReader(SCHEMA$);

  @Override
  public void readExternal(java.io.ObjectInput in) throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}

/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package org.apache.avro.specific;

import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;

@AvroGenerated
public class int$ extends SpecificRecordBase implements SpecificRecord {
  private static final long serialVersionUID = 3003385205621277651L;

  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser()
      .parse("{\"type\":\"record\",\"name\":\"int\",\"namespace\":\"org.apache.avro.specific\",\"fields\":[]}");

  public static org.apache.avro.Schema getClassSchema() {
    return SCHEMA$;
  }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<int$> ENCODER = new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<int$> DECODER = new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * 
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<int$> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * 
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<int$> getDecoder() {
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
  public static BinaryMessageDecoder<int$> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this int to a ByteBuffer.
   * 
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a int from a ByteBuffer.
   * 
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a int instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into
   *                             an instance of this class
   */
  public static int$ fromByteBuffer(java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  public SpecificData getSpecificData() {
    return MODEL$;
  }

  public org.apache.avro.Schema getSchema() {
    return SCHEMA$;
  }

  // Used by DatumWriter. Applications should not call.
  public Object get(int field$) {
    switch (field$) {
    default:
      throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader. Applications should not call.
  @SuppressWarnings(value = "unchecked")
  public void put(int field$, Object value$) {
    switch (field$) {
    default:
      throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Creates a new int$ RecordBuilder.
   * 
   * @return A new int$ RecordBuilder
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Creates a new int$ RecordBuilder by copying an existing Builder.
   * 
   * @param other The existing builder to copy.
   * @return A new int$ RecordBuilder
   */
  public static Builder newBuilder(Builder other) {
    if (other == null) {
      return new Builder();
    } else {
      return new Builder(other);
    }
  }

  /**
   * Creates a new int$ RecordBuilder by copying an existing int$ instance.
   * 
   * @param other The existing instance to copy.
   * @return A new int$ RecordBuilder
   */
  public static Builder newBuilder(int$ other) {
    if (other == null) {
      return new Builder();
    } else {
      return new Builder(other);
    }
  }

  /**
   * RecordBuilder for int$ instances.
   */
  @AvroGenerated
  public static class Builder extends SpecificRecordBuilderBase<int$>
      implements org.apache.avro.data.RecordBuilder<int$> {

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * 
     * @param other The existing Builder to copy.
     */
    private Builder(Builder other) {
      super(other);
    }

    /**
     * Creates a Builder by copying an existing int$ instance
     * 
     * @param other The existing instance to copy.
     */
    private Builder(int$ other) {
      super(SCHEMA$, MODEL$);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int$ build() {
      try {
        int$ record = new int$();
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<int$> WRITER$ = (org.apache.avro.io.DatumWriter<int$>) MODEL$
      .createDatumWriter(SCHEMA$);

  @Override
  public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<int$> READER$ = (org.apache.avro.io.DatumReader<int$>) MODEL$
      .createDatumReader(SCHEMA$);

  @Override
  public void readExternal(java.io.ObjectInput in) throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override
  protected boolean hasCustomCoders() {
    return true;
  }

  @Override
  public void customEncode(org.apache.avro.io.Encoder out) throws java.io.IOException {
  }

  @Override
  public void customDecode(org.apache.avro.io.ResolvingDecoder in) throws java.io.IOException {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
    } else {
      for (int i = 0; i < 0; i++) {
        switch (fieldOrder[i].pos()) {
        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}

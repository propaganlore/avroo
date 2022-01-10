/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package avro.examples.baseball;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

/** Test that setters are omitted */
@org.apache.avro.specific.AvroGenerated
public class NoSettersTest extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 8604146783520861700L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"NoSettersTest\",\"namespace\":\"avro.examples.baseball\",\"doc\":\"Test that setters are omitted\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"favorite_number\",\"type\":[\"int\",\"null\"]}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<NoSettersTest> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<NoSettersTest> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<NoSettersTest> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<NoSettersTest> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<NoSettersTest> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this NoSettersTest to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a NoSettersTest from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a NoSettersTest instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static NoSettersTest fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.lang.CharSequence name;
  private java.lang.Integer favorite_number;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public NoSettersTest() {}

  /**
   * All-args constructor.
   * @param name The new value for name
   * @param favorite_number The new value for favorite_number
   */
  public NoSettersTest(java.lang.CharSequence name, java.lang.Integer favorite_number) {
    this.name = name;
    this.favorite_number = favorite_number;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return name;
    case 1: return favorite_number;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: name = (java.lang.CharSequence)value$; break;
    case 1: favorite_number = (java.lang.Integer)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'name' field.
   * @return The value of the 'name' field.
   */
  public java.lang.CharSequence getName() {
    return name;
  }



  /**
   * Gets the value of the 'favorite_number' field.
   * @return The value of the 'favorite_number' field.
   */
  public java.lang.Integer getFavoriteNumber() {
    return favorite_number;
  }



  /**
   * Creates a new NoSettersTest RecordBuilder.
   * @return A new NoSettersTest RecordBuilder
   */
  public static avro.examples.baseball.NoSettersTest.Builder newBuilder() {
    return new avro.examples.baseball.NoSettersTest.Builder();
  }

  /**
   * Creates a new NoSettersTest RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new NoSettersTest RecordBuilder
   */
  public static avro.examples.baseball.NoSettersTest.Builder newBuilder(avro.examples.baseball.NoSettersTest.Builder other) {
    if (other == null) {
      return new avro.examples.baseball.NoSettersTest.Builder();
    } else {
      return new avro.examples.baseball.NoSettersTest.Builder(other);
    }
  }

  /**
   * Creates a new NoSettersTest RecordBuilder by copying an existing NoSettersTest instance.
   * @param other The existing instance to copy.
   * @return A new NoSettersTest RecordBuilder
   */
  public static avro.examples.baseball.NoSettersTest.Builder newBuilder(avro.examples.baseball.NoSettersTest other) {
    if (other == null) {
      return new avro.examples.baseball.NoSettersTest.Builder();
    } else {
      return new avro.examples.baseball.NoSettersTest.Builder(other);
    }
  }

  /**
   * RecordBuilder for NoSettersTest instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<NoSettersTest>
    implements org.apache.avro.data.RecordBuilder<NoSettersTest> {

    private java.lang.CharSequence name;
    private java.lang.Integer favorite_number;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(avro.examples.baseball.NoSettersTest.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.name)) {
        this.name = data().deepCopy(fields()[0].schema(), other.name);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.favorite_number)) {
        this.favorite_number = data().deepCopy(fields()[1].schema(), other.favorite_number);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
    }

    /**
     * Creates a Builder by copying an existing NoSettersTest instance
     * @param other The existing instance to copy.
     */
    private Builder(avro.examples.baseball.NoSettersTest other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.name)) {
        this.name = data().deepCopy(fields()[0].schema(), other.name);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.favorite_number)) {
        this.favorite_number = data().deepCopy(fields()[1].schema(), other.favorite_number);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'name' field.
      * @return The value.
      */
    public java.lang.CharSequence getName() {
      return name;
    }


    /**
      * Sets the value of the 'name' field.
      * @param value The value of 'name'.
      * @return This builder.
      */
    public avro.examples.baseball.NoSettersTest.Builder setName(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.name = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'name' field has been set.
      * @return True if the 'name' field has been set, false otherwise.
      */
    public boolean hasName() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'name' field.
      * @return This builder.
      */
    public avro.examples.baseball.NoSettersTest.Builder clearName() {
      name = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'favorite_number' field.
      * @return The value.
      */
    public java.lang.Integer getFavoriteNumber() {
      return favorite_number;
    }


    /**
      * Sets the value of the 'favorite_number' field.
      * @param value The value of 'favorite_number'.
      * @return This builder.
      */
    public avro.examples.baseball.NoSettersTest.Builder setFavoriteNumber(java.lang.Integer value) {
      validate(fields()[1], value);
      this.favorite_number = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'favorite_number' field has been set.
      * @return True if the 'favorite_number' field has been set, false otherwise.
      */
    public boolean hasFavoriteNumber() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'favorite_number' field.
      * @return This builder.
      */
    public avro.examples.baseball.NoSettersTest.Builder clearFavoriteNumber() {
      favorite_number = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NoSettersTest build() {
      try {
        NoSettersTest record = new NoSettersTest();
        record.name = fieldSetFlags()[0] ? this.name : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.favorite_number = fieldSetFlags()[1] ? this.favorite_number : (java.lang.Integer) defaultValue(fields()[1]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<NoSettersTest>
    WRITER$ = (org.apache.avro.io.DatumWriter<NoSettersTest>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<NoSettersTest>
    READER$ = (org.apache.avro.io.DatumReader<NoSettersTest>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.name);

    if (this.favorite_number == null) {
      out.writeIndex(1);
      out.writeNull();
    } else {
      out.writeIndex(0);
      out.writeInt(this.favorite_number);
    }

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.name = in.readString(this.name instanceof Utf8 ? (Utf8)this.name : null);

      if (in.readIndex() != 0) {
        in.readNull();
        this.favorite_number = null;
      } else {
        this.favorite_number = in.readInt();
      }

    } else {
      for (int i = 0; i < 2; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.name = in.readString(this.name instanceof Utf8 ? (Utf8)this.name : null);
          break;

        case 1:
          if (in.readIndex() != 0) {
            in.readNull();
            this.favorite_number = null;
          } else {
            this.favorite_number = in.readInt();
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}











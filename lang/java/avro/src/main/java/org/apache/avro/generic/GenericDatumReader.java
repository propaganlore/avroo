/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.generic;

import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Collection;
import java.nio.ByteBuffer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.ResolvingDecoder;
import org.apache.avro.util.Utf8;
import org.apache.avro.util.WeakIdentityHashMap;

/** {@link DatumReader} for generic Java objects. */
public class GenericDatumReader<D> implements DatumReader<D> {
  private final GenericData data;
  private Schema actual;
  private Schema expected;
  
  private ResolvingDecoder creatorResolver = null;
  private final Thread creator;

  public GenericDatumReader() {
    this(null, null, GenericData.get());
  }

  /** Construct where the writer's and reader's schemas are the same. */
  public GenericDatumReader(Schema schema) {
    this(schema, schema, GenericData.get());
  }

  /** Construct given writer's and reader's schema. */
  public GenericDatumReader(Schema writer, Schema reader) {
    this(writer, reader, GenericData.get());
  }

  public GenericDatumReader(Schema writer, Schema reader, GenericData data) {
    this(data);
    this.actual = writer;
    this.expected = reader;
  }

  protected GenericDatumReader(GenericData data) {
    this.data = data;
    this.creator = Thread.currentThread();
  }

  /** Return the {@link GenericData} implementation. */
  public GenericData getData() { return data; }

  /** Return the writer's schema. */
  public Schema getSchema() { return actual; }

  @Override
  public void setSchema(Schema writer) {
    this.actual = writer;
    if (expected == null) {
      expected = actual;
    }
    creatorResolver = null;
  }

  /** Get the reader's schema. */
  public Schema getExpected() { return expected; }

  /** Set the reader's schema. */
  public void setExpected(Schema reader) {
    this.expected = reader;
    creatorResolver = null;
  }

  private static final ThreadLocal<Map<Schema,Map<Schema,ResolvingDecoder>>>
    RESOLVER_CACHE =
    new ThreadLocal<Map<Schema,Map<Schema,ResolvingDecoder>>>() {
    protected Map<Schema,Map<Schema,ResolvingDecoder>> initialValue() {
      return new WeakIdentityHashMap<Schema,Map<Schema,ResolvingDecoder>>();
    }
  };

  /** Gets a resolving decoder for use by this GenericDatumReader.
   *  Unstable API.
   *  Currently uses a thread local cache to prevent constructing the
   *  resolvers too often, because that is very expensive.
   */
  protected final ResolvingDecoder getResolver(Schema actual, Schema expected)
    throws IOException {
    Thread currThread = Thread.currentThread();
    ResolvingDecoder resolver;
    if (currThread == creator && creatorResolver != null) {
      return creatorResolver;
    } 

    Map<Schema,ResolvingDecoder> cache = RESOLVER_CACHE.get().get(actual);
    if (cache == null) {
      cache = new WeakIdentityHashMap<Schema,ResolvingDecoder>();
      RESOLVER_CACHE.get().put(actual, cache);
    }
    resolver = cache.get(expected);
    if (resolver == null) {
      resolver = DecoderFactory.get().resolvingDecoder(
          Schema.applyAliases(actual, expected), expected, null);
      cache.put(expected, resolver);
    }
    
    if (currThread == creator){
      creatorResolver = resolver;
    }

    return resolver;
  }

  @Override
  @SuppressWarnings("unchecked")
  public D read(D reuse, Decoder in) throws IOException {
    ResolvingDecoder resolver = getResolver(actual, expected);
    resolver.configure(in);
    D result = (D) read(reuse, expected, resolver);
    resolver.drain();
    return result;
  }
  
  /** Called to read data.*/
  protected Object read(Object old, Schema expected,
      ResolvingDecoder in) throws IOException {
    Conversion<?> conversion = getData().getConversionFor(expected);
    Object datum = readWithoutConversion(old, expected, in);
    if (conversion != null) {
      LogicalType logicalType = LogicalTypes.fromSchema(expected);
      return convert(datum, expected, logicalType, conversion);
    }
    return datum;
  }

  protected Object readWithoutConversion(Object old, Schema expected,
      ResolvingDecoder in) throws IOException {
    switch (expected.getType()) {
    case RECORD:  return readRecord(old, expected, in);
    case ENUM:    return readEnum(expected, in);
    case ARRAY:   return readArray(old, expected, in);
    case MAP:     return readMap(old, expected, in);
    case UNION:   return read(old, expected.getTypes().get(in.readIndex()), in);
    case FIXED:   return readFixed(old, expected, in);
    case STRING:  return readString(old, expected, in);
    case BYTES:   return readBytes(old, expected, in);
    case INT:     return readInt(old, expected, in);
    case LONG:    return in.readLong();
    case FLOAT:   return in.readFloat();
    case DOUBLE:  return in.readDouble();
    case BOOLEAN: return in.readBoolean();
    case NULL:    in.readNull(); return null;
    default: throw new AvroRuntimeException("Unknown type: " + expected);
    }
  }

  protected Object convert(Object datum, Schema schema, LogicalType type,
                           Conversion<?> conversion) {
    if (conversion == null) {
      return datum;
    }

    try {
      switch (schema.getType()) {
      case RECORD:  return conversion.fromRecord((IndexedRecord) datum, schema, type);
      case ENUM:    return conversion.fromEnumSymbol((GenericEnumSymbol) datum, schema, type);
      case ARRAY:   return conversion.fromArray(getData().getArrayAsCollection(datum), schema, type);
      case MAP:     return conversion.fromMap((Map<?, ?>) datum, schema, type);
      case FIXED:   return conversion.fromFixed((GenericFixed) datum, schema, type);
      case STRING:  return conversion.fromCharSequence((CharSequence) datum, schema, type);
      case BYTES:   return conversion.fromBytes((ByteBuffer) datum, schema, type);
      case INT:     return conversion.fromInt((Integer) datum, schema, type);
      case LONG:    return conversion.fromLong((Long) datum, schema, type);
      case FLOAT:   return conversion.fromFloat((Float) datum, schema, type);
      case DOUBLE:  return conversion.fromDouble((Double) datum, schema, type);
      case BOOLEAN: return conversion.fromBoolean((Boolean) datum, schema, type);
      }
      return datum;
    } catch (ClassCastException e) {
      throw new AvroRuntimeException("Cannot convert " + datum + ":" +
          datum.getClass().getSimpleName() + ": expected generic type", e);
    }
  }

  /** Called to read a record instance. May be overridden for alternate record
   * representations.*/
  protected Object readRecord(Object old, Schema expected, 
      ResolvingDecoder in) throws IOException {
    Object r = data.newRecord(old, expected);
    Object state = data.getRecordState(r, expected);
    
    for (Field f : in.readFieldOrder()) {
      int pos = f.pos();
      String name = f.name();
      Object oldDatum = null;
      if (old!=null) {
        oldDatum = data.getField(r, name, pos, state);
      }
      readField(r, f, oldDatum, in, state);
    }

    return r;
  }
  
  /** Called to read a single field of a record. May be overridden for more 
   * efficient or alternate implementations.*/
  protected void readField(Object r, Field f, Object oldDatum,
    ResolvingDecoder in, Object state) throws IOException {
    data.setField(r, f.name(), f.pos(), read(oldDatum, f.schema(), in), state);
  }
  
  /** Called to read an enum value. May be overridden for alternate enum
   * representations.  By default, returns a GenericEnumSymbol. */
  protected Object readEnum(Schema expected, Decoder in) throws IOException {
    return createEnum(expected.getEnumSymbols().get(in.readEnum()), expected);
  }

  /** Called to create an enum value. May be overridden for alternate enum
   * representations.  By default, returns a GenericEnumSymbol. */
  protected Object createEnum(String symbol, Schema schema) {
    return data.createEnum(symbol, schema);
  }

  /** Called to read an array instance.  May be overridden for alternate array
   * representations.*/
  protected Object readArray(Object old, Schema expected,
      ResolvingDecoder in) throws IOException {
    Schema expectedType = expected.getElementType();
    long l = in.readArrayStart();
    long base = 0;
    if (l > 0) {
      Object array = newArray(old, (int) l, expected);
      do {
        for (long i = 0; i < l; i++) {
          addToArray(array, base + i, read(peekArray(array), expectedType, in));
        }
        base += l;
      } while ((l = in.arrayNext()) > 0);
      return array;
    } else {
      return newArray(old, 0, expected);
    }
  }

  /** Called by the default implementation of {@link #readArray} to retrieve a
   * value from a reused instance.  The default implementation is for {@link
   * GenericArray}.*/
  @SuppressWarnings("unchecked")
  protected Object peekArray(Object array) {
    return (array instanceof GenericArray)
      ? ((GenericArray)array).peek()
      : null;
  }

  /** Called by the default implementation of {@link #readArray} to add a
   * value.  The default implementation is for {@link Collection}.*/
  @SuppressWarnings("unchecked")
  protected void addToArray(Object array, long pos, Object e) {
    ((Collection) array).add(e);
  }
  
  /** Called to read a map instance.  May be overridden for alternate map
   * representations.*/
  protected Object readMap(Object old, Schema expected,
      ResolvingDecoder in) throws IOException {
    Schema eValue = expected.getValueType();
    long l = in.readMapStart();
    Object map = newMap(old, (int) l);
    if (l > 0) {
      do {
        for (int i = 0; i < l; i++) {
          addToMap(map, readMapKey(null, expected, in), read(null, eValue, in));
        }
      } while ((l = in.mapNext()) > 0);
    }
    return map;
  }

  /** Called by the default implementation of {@link #readMap} to read a
   * key value.  The default implementation returns delegates to
   * {@link #readString(Object, org.apache.avro.io.Decoder)}.*/
  protected Object readMapKey(Object old, Schema expected, Decoder in)
    throws IOException{
    return readString(old, expected, in);
  }

  /** Called by the default implementation of {@link #readMap} to add a
   * key/value pair.  The default implementation is for {@link Map}.*/
  @SuppressWarnings("unchecked")
  protected void addToMap(Object map, Object key, Object value) {
    ((Map) map).put(key, value);
  }
  
  /** Called to read a fixed value. May be overridden for alternate fixed
   * representations.  By default, returns {@link GenericFixed}. */
  protected Object readFixed(Object old, Schema expected, Decoder in)
    throws IOException {
    GenericFixed fixed = (GenericFixed)data.createFixed(old, expected);
    in.readFixed(fixed.bytes(), 0, expected.getFixedSize());
    return fixed;
  }
  
  /** 
   * Called to create an fixed value. May be overridden for alternate fixed
   * representations.  By default, returns {@link GenericFixed}.
   * @deprecated As of Avro 1.6.0 this method has been moved to 
   * {@link GenericData#createFixed(Object, Schema)}
   */
  @Deprecated
  protected Object createFixed(Object old, Schema schema) {
    return data.createFixed(old, schema);
  }

  /** 
   * Called to create an fixed value. May be overridden for alternate fixed
   * representations.  By default, returns {@link GenericFixed}.
   * @deprecated As of Avro 1.6.0 this method has been moved to 
   * {@link GenericData#createFixed(Object, byte[], Schema)}
   */
  @Deprecated
  protected Object createFixed(Object old, byte[] bytes, Schema schema) {
    return data.createFixed(old, bytes, schema);
  }
  
  /**
   * Called to create new record instances. Subclasses may override to use a
   * different record implementation. The returned instance must conform to the
   * schema provided. If the old object contains fields not present in the
   * schema, they should either be removed from the old object, or it should
   * create a new instance that conforms to the schema. By default, this returns
   * a {@link GenericData.Record}.
   * @deprecated As of Avro 1.6.0 this method has been moved to 
   * {@link GenericData#newRecord(Object, Schema)}
   */
  @Deprecated
  protected Object newRecord(Object old, Schema schema) {
    return data.newRecord(old, schema);
  }

  /** Called to create new array instances.  Subclasses may override to use a
   * different array implementation.  By default, this returns a {@link
   * GenericData.Array}.*/
  @SuppressWarnings("unchecked")
  protected Object newArray(Object old, int size, Schema schema) {
    if (old instanceof Collection) {
      ((Collection) old).clear();
      return old;
    } else return new GenericData.Array(size, schema);
  }

  /** Called to create new array instances.  Subclasses may override to use a
   * different map implementation.  By default, this returns a {@link
   * HashMap}.*/
  @SuppressWarnings("unchecked")
  protected Object newMap(Object old, int size) {
    if (old instanceof Map) {
      ((Map) old).clear();
      return old;
    } else return new HashMap<Object, Object>(size);
  }

  /** Called to read strings.  Subclasses may override to use a different
   * string representation.  By default, this calls {@link
   * #readString(Object,Decoder)}.*/
  protected Object readString(Object old, Schema expected,
                              Decoder in) throws IOException {
    Class stringClass = getStringClass(expected);
    if (stringClass == String.class)
      return in.readString();
    if (stringClass == CharSequence.class)
      return readString(old, in);
    return newInstanceFromString(stringClass, in.readString());
  }                  

  /** Called to read strings.  Subclasses may override to use a different
   * string representation.  By default, this calls {@link
   * Decoder#readString(Utf8)}.*/
  protected Object readString(Object old, Decoder in) throws IOException {
    return in.readString(old instanceof Utf8 ? (Utf8)old : null);
  }

  /** Called to create a string from a default value.  Subclasses may override
   * to use a different string representation.  By default, this calls {@link
   * Utf8#Utf8(String)}.*/
  protected Object createString(String value) { return new Utf8(value); }

  /** Determines the class to used to represent a string Schema.  By default
   * uses {@link GenericData#STRING_PROP} to determine whether {@link Utf8} or
   * {@link String} is used.  Subclasses may override for alternate
   * representations.
   */
  protected Class findStringClass(Schema schema) {
    String name = schema.getProp(GenericData.STRING_PROP);
    if (name == null) return CharSequence.class;

    switch (GenericData.StringType.valueOf(name)) {
      case String:
        return String.class;
      default:
        return CharSequence.class;
    }
  }

  private Map<Schema,Class> stringClassCache =
    new IdentityHashMap<Schema,Class>();

  private Class getStringClass(Schema s) {
    Class c = stringClassCache.get(s);
    if (c == null) {
      c = findStringClass(s);
      stringClassCache.put(s, c);
    }
    return c;
  }

  private final Map<Class,Constructor> stringCtorCache =
    new HashMap<Class,Constructor>();

  @SuppressWarnings("unchecked")
  protected Object newInstanceFromString(Class c, String s) {
    try {
      Constructor ctor = stringCtorCache.get(c);
      if (ctor == null) {
        ctor = c.getDeclaredConstructor(String.class);
        ctor.setAccessible(true);
        stringCtorCache.put(c, ctor);
      }
      return ctor.newInstance(s);
    } catch (NoSuchMethodException e) {
      throw new AvroRuntimeException(e);
    } catch (InstantiationException e) {
      throw new AvroRuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new AvroRuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new AvroRuntimeException(e);
    }
  }

  /** Called to read byte arrays.  Subclasses may override to use a different
   * byte array representation.  By default, this calls {@link
   * Decoder#readBytes(ByteBuffer)}.*/
  protected Object readBytes(Object old, Schema s, Decoder in)
    throws IOException {
    return readBytes(old, in);
  }

  /** Called to read byte arrays.  Subclasses may override to use a different
   * byte array representation.  By default, this calls {@link
   * Decoder#readBytes(ByteBuffer)}.*/
  protected Object readBytes(Object old, Decoder in) throws IOException {
    return in.readBytes(old instanceof ByteBuffer ? (ByteBuffer) old : null);
  }

  /** Called to read integers.  Subclasses may override to use a different
   * integer representation.  By default, this calls {@link
   * Decoder#readInt()}.*/
  protected Object readInt(Object old, Schema expected, Decoder in)
    throws IOException {
    return in.readInt();
  }

  /** Called to create byte arrays from default values.  Subclasses may
   * override to use a different byte array representation.  By default, this
   * calls {@link ByteBuffer#wrap(byte[])}.*/
  protected Object createBytes(byte[] value) { return ByteBuffer.wrap(value); }

  /** Skip an instance of a schema. */
  public static void skip(Schema schema, Decoder in) throws IOException {
    switch (schema.getType()) {
    case RECORD:
      for (Field field : schema.getFields())
        skip(field.schema(), in);
      break;
    case ENUM:
      in.readInt();
      break;
    case ARRAY:
      Schema elementType = schema.getElementType();
      for (long l = in.skipArray(); l > 0; l = in.skipArray()) {
        for (long i = 0; i < l; i++) {
          skip(elementType, in);
        }
      }
      break;
    case MAP:
      Schema value = schema.getValueType();
      for (long l = in.skipMap(); l > 0; l = in.skipMap()) {
        for (long i = 0; i < l; i++) {
          in.skipString();
          skip(value, in);
        }
      }
      break;
    case UNION:
      skip(schema.getTypes().get((int)in.readIndex()), in);
      break;
    case FIXED:
      in.skipFixed(schema.getFixedSize());
      break;
    case STRING:
      in.skipString();
      break;
    case BYTES:
      in.skipBytes();
      break;
    case INT:     in.readInt();           break;
    case LONG:    in.readLong();          break;
    case FLOAT:   in.readFloat();         break;
    case DOUBLE:  in.readDouble();        break;
    case BOOLEAN: in.readBoolean();       break;
    case NULL:                            break;
    default: throw new RuntimeException("Unknown type: "+schema);
    }
  }

}


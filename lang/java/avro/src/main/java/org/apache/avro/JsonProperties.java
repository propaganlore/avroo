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
package org.apache.avro;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import java.io.IOException;

import org.apache.avro.reflect.MapEntry;
import org.apache.avro.util.internal.JacksonUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.node.TextNode;

/**
 * Base class for objects that have JSON-valued properties. Avro and JSON values are
 * represented in Java using the following mapping:
 *
 * <table>
 *   <th>
 *     <td>Avro type</td>
 *     <td>JSON type</td>
 *     <td>Java type</td>
 *   </th>
 *   <tr>
 *     <td><code>null</code></td>
 *     <td><code>null</code></td>
 *     <td>{@link #NULL_VALUE}</td>
 *   </tr>
 *   <tr>
 *     <td><code>boolean</code></td>
 *     <td>Boolean</td>
 *     <td><code>boolean</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>int</code></td>
 *     <td>Number</td>
 *     <td><code>int</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>long</code></td>
 *     <td>Number</td>
 *     <td><code>long</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>float</code></td>
 *     <td>Number</td>
 *     <td><code>float</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>double</code></td>
 *     <td>Number</td>
 *     <td><code>double</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>bytes</code></td>
 *     <td>String</td>
 *     <td><code>byte[]</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>string</code></td>
 *     <td>String</td>
 *     <td>{@link java.lang.String}</td>
 *   </tr>
 *   <tr>
 *     <td><code>record</code></td>
 *     <td>Object</td>
 *     <td>{@link java.util.Map}</td>
 *   </tr>
 *   <tr>
 *     <td><code>enum</code></td>
 *     <td>String</td>
 *     <td>{@link java.lang.String}</td>
 *   </tr>
 *   <tr>
 *     <td><code>array</code></td>
 *     <td>Array</td>
 *     <td>{@link java.util.Collection}</td>
 *   </tr>
 *   <tr>
 *     <td><code>map</code></td>
 *     <td>Object</td>
 *     <td>{@link java.util.Map}</td>
 *   </tr>
 *   <tr>
 *     <td><code>fixed</code></td>
 *     <td>String</td>
 *     <td><code>byte[]</code></td>
 *   </tr>
 * </table>
 *
 * @see org.apache.avro.data.Json
 */
public abstract class JsonProperties {
  public static class Null {
    private Null() {}
  }
  /** A value representing a JSON <code>null</code>. */
  public static final Null NULL_VALUE = new Null();

  // use a ConcurrentHashMap for speed and thread safety, but keep a Queue of the entries to maintain order
  // the queue is always updated after the main map and is thus is potentially a subset of the map.
  // By making props private, we can control access and only implement/override the methods
  // we need.  We don't ever remove anything so we don't need to implement the clear/remove functionality.
  // Also, we only ever ADD to the collection, never changing a value, so putWithAbsent is the
  // only modifier
  private ConcurrentMap<String,JsonNode> props = new ConcurrentHashMap<String,JsonNode>() {
    private static final long serialVersionUID = 1L;
    private Queue<MapEntry<String, JsonNode>> propOrder = new ConcurrentLinkedQueue<MapEntry<String, JsonNode>>();
    public JsonNode putIfAbsent(String key,  JsonNode value) {
      JsonNode r = super.putIfAbsent(key, value);
      if (r == null) {
        propOrder.add(new MapEntry<String, JsonNode>(key, value));
      }
      return r;
    }
    public Set<Map.Entry<String, JsonNode>> entrySet() {
      return new AbstractSet<Map.Entry<String, JsonNode>>() {
        @Override
        public Iterator<Map.Entry<String, JsonNode>> iterator() {
          return new Iterator<Map.Entry<String, JsonNode>>() {
            Iterator<MapEntry<String, JsonNode>> it = propOrder.iterator();
            @Override
            public boolean hasNext() {
              return it.hasNext();
            }
            @Override
            public java.util.Map.Entry<String, JsonNode> next() {
              return it.next();
            }
          };
        }
        @Override
        public int size() {
          return propOrder.size();
        }
      };
    }
  };

  private Set<String> reserved;

  JsonProperties(Set<String> reserved) {
    this.reserved = reserved;
  }

  /**
   * Returns the value of the named, string-valued property in this schema.
   * Returns <tt>null</tt> if there is no string-valued property with that name.
   */
  public String getProp(String name) {
    JsonNode value = getJsonProp(name);
    return value != null && value.isTextual() ? value.getTextValue() : null;
  }

  /**
   * Returns the value of the named property in this schema.
   * Returns <tt>null</tt> if there is no property with that name.
   * @deprecated use {@link #getObjectProp(String)}
   */
  @Deprecated
  public JsonNode getJsonProp(String name) {
    return props.get(name);
  }

  /**
   * Returns the value of the named property in this schema.
   * Returns <tt>null</tt> if there is no property with that name.
   */
  public Object getObjectProp(String name) {
    return JacksonUtils.toObject(props.get(name));
  }

  /**
   * Adds a property with the given name <tt>name</tt> and
   * value <tt>value</tt>. Neither <tt>name</tt> nor <tt>value</tt> can be
   * <tt>null</tt>. It is illegal to add a property if another with
   * the same name but different value already exists in this schema.
   *
   * @param name The name of the property to add
   * @param value The value for the property to add
   */
  public void addProp(String name, String value) {
    addProp(name, TextNode.valueOf(value));
  }

  /**
   * Adds a property with the given name <tt>name</tt> and
   * value <tt>value</tt>. Neither <tt>name</tt> nor <tt>value</tt> can be
   * <tt>null</tt>. It is illegal to add a property if another with
   * the same name but different value already exists in this schema.
   *
   * @param name The name of the property to add
   * @param value The value for the property to add
   * @deprecated use {@link #addProp(String, Object)}
   */
  @Deprecated
  public void addProp(String name, JsonNode value) {
    if (reserved.contains(name))
      throw new AvroRuntimeException("Can't set reserved property: " + name);

    if (value == null)
      throw new AvroRuntimeException("Can't set a property to null: " + name);

    JsonNode old = props.putIfAbsent(name,  value);
    if (old != null && !old.equals(value)) {
      throw new AvroRuntimeException("Can't overwrite property: " + name);
    }
  }
  public void addProp(String name, Object value) {
    addProp(name, JacksonUtils.toJsonNode(value));
  }
  public void putAll(JsonProperties np) {
    for (Map.Entry<? extends String, ? extends JsonNode> e : np.props.entrySet())
      addProp(e.getKey(), e.getValue());
  }


  /** Return the defined properties that have string values. */
  @Deprecated public Map<String,String> getProps() {
    Map<String,String> result = new LinkedHashMap<String,String>();
    for (Map.Entry<String,JsonNode> e : props.entrySet())
      if (e.getValue().isTextual())
        result.put(e.getKey(), e.getValue().getTextValue());
    return result;
  }

  /**
   * Return the defined properties as an unmodifieable Map.
   * @deprecated use {@link #getObjectProps()}
   */
  @Deprecated
  public Map<String,JsonNode> getJsonProps() {
    return Collections.unmodifiableMap(props);
  }

  /** Return the defined properties as an unmodifieable Map. */
  public Map<String,Object> getObjectProps() {
    Map<String,Object> result = new LinkedHashMap<String,Object>();
    for (Map.Entry<String,JsonNode> e : props.entrySet())
      result.put(e.getKey(), JacksonUtils.toObject(e.getValue()));
    return result;
  }

  void writeProps(JsonGenerator gen) throws IOException {
    for (Map.Entry<String,JsonNode> e : props.entrySet())
      gen.writeObjectField(e.getKey(), e.getValue());
  }


  int propsHashCode() {
    return props.hashCode();
  }
  boolean propsEqual(JsonProperties np) {
    return props.equals(np.props);
  }
  public boolean hasProps() {
    return !props.isEmpty();
  }

}

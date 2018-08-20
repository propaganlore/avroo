/*
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
package org.apache.avro.io.fastreader.readers;

import java.io.IOException;
import org.apache.avro.io.Decoder;

public class BooleanReader implements FieldReader<Boolean> {

  private static final BooleanReader INSTANCE = new BooleanReader();

  public static BooleanReader get() {
    return INSTANCE;
  }

  private BooleanReader() {}

  @Override
  public Boolean read(Boolean reuse, Decoder decoder) throws IOException {
    return decoder.readBoolean();
  }

  @Override
  public void skip(Decoder decoder) throws IOException {
    decoder.readBoolean();
  }
}

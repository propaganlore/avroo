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

package org.apache.avro.perf.test.specific;

import org.apache.avro.perf.test.BasicRecord;
import org.apache.avro.perf.test.BasicState;
import org.apache.avro.perf.test.specific.avro.SpecificBasicRecord;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

public class SpecficTest {

  @Benchmark
  @OperationsPerInvocation(BasicState.BATCH_SIZE)
  public void hashCode(final TestState state, Blackhole blackhole) {
    for (final SpecificBasicRecord r : state.testData) {
      blackhole.consume(r.hashCode());
    }
  }

  @Benchmark
  @OperationsPerInvocation(BasicState.BATCH_SIZE)
  public void equals(final TestState state, Blackhole blackhole) {
    for (int i = 0; i < state.testData.length; i++) {
      blackhole.consume(state.testData[i].equals(state.testDataCopy[i]));
    }
  }

  @State(Scope.Thread)
  public static class TestState extends BasicState {
    private SpecificBasicRecord[] testData;
    private SpecificBasicRecord[] testDataCopy;

    @Setup(Level.Trial)
    public void doSetupTrial() throws Exception {
      this.testData = new SpecificBasicRecord[getBatchSize()];
      this.testDataCopy = new SpecificBasicRecord[testData.length];
      for (int i = 0; i < testData.length; i++) {
        testData[i] = BasicRecord.newSpecific(super.getRandom());
        testDataCopy[i] = SpecificBasicRecord.newBuilder(testData[i]).build();
      }
    }
  }
}

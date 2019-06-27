/*  Copyright 2019 Pitney Bowes Inc.
 *
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

using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Concurrent;

namespace Avro.Reflect
{
    /// <summary>
    /// Class to help serialize and deserialize arrays. Arrays need the following methods Count(), Add(), Clear().true
    /// This class allows these methods to be specified externally to the collection.
    /// </summary>
    public class ArrayHelper
    {
        private static Type _defaultType = typeof(List<>);
        public static Type DefaultType
        {
            get => _defaultType;
            set => _defaultType = value;
        }
        /// <summary>
        /// The array
        /// </summary>
        /// <value></value>
        public IEnumerable Enumerable { get; set; }
        /// <summary>
        /// Return the number of elements in the array.
        /// </summary>
        /// <value></value>
        public virtual int Count()
        {
            IList e = (IList)Enumerable;
            return e.Count;
        }
        /// <summary>
        /// Add an element to the array.
        /// </summary>
        /// <value></value>
        public virtual void Add(object o)
        {
            IList e = (IList)Enumerable;
            e.Add(o);
        }
        /// <summary>
        /// Clear the array.
        /// </summary>
        /// <value></value>
        public virtual void Clear()
        {
            IList e = (IList)Enumerable;
            e.Clear();
        }

        /// <summary>
        /// Type of the array to create when deserializing
        /// </summary>
        /// <value></value>
        public virtual Type ArrayType
        {
            get => _defaultType;
        }

        /// <summary>
        /// Constructor
        /// </summary>
        public ArrayHelper(IEnumerable enumerable)
        {
            Enumerable = enumerable;
        }
    }
}

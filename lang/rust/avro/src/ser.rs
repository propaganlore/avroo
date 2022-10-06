// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

//! Logic for serde-compatible serialization.
use crate::{types::Value, Error};
use ref_thread_local::ref_thread_local;
use ref_thread_local::RefThreadLocal;
use serde::{ser, Serialize};
use std::{collections::HashMap, iter::once};

ref_thread_local! {
    /// A thread local that is used to decide how to serialize
    /// a byte array into Avro `types::Value`.
    ///
    /// Depends on the fact that serde's serialization process is single-threaded!
    static managed BYTES_TYPE: BytesType = BytesType::Bytes;
}

/// A hint helping in the serialization of a byte arrays (&[u8], [u8; N])
enum BytesType {
    Bytes,
    Fixed,
}

#[derive(Clone, Default)]
pub struct Serializer {}

pub struct SeqSerializer {
    items: Vec<Value>,
}

pub struct SeqVariantSerializer<'a> {
    index: u32,
    variant: &'a str,
    items: Vec<Value>,
}

pub struct MapSerializer {
    indices: HashMap<String, usize>,
    values: Vec<Value>,
}

pub struct StructSerializer {
    fields: Vec<(String, Value)>,
}

pub struct StructVariantSerializer<'a> {
    index: u32,
    variant: &'a str,
    fields: Vec<(String, Value)>,
}

impl SeqSerializer {
    pub fn new(len: Option<usize>) -> SeqSerializer {
        let items = match len {
            Some(len) => Vec::with_capacity(len),
            None => Vec::new(),
        };

        SeqSerializer { items }
    }
}

impl<'a> SeqVariantSerializer<'a> {
    pub fn new(index: u32, variant: &'a str, len: Option<usize>) -> SeqVariantSerializer {
        let items = match len {
            Some(len) => Vec::with_capacity(len),
            None => Vec::new(),
        };
        SeqVariantSerializer {
            index,
            variant,
            items,
        }
    }
}

impl MapSerializer {
    pub fn new(len: Option<usize>) -> MapSerializer {
        let (indices, values) = match len {
            Some(len) => (HashMap::with_capacity(len), Vec::with_capacity(len)),
            None => (HashMap::new(), Vec::new()),
        };

        MapSerializer { indices, values }
    }
}

impl StructSerializer {
    pub fn new(len: usize) -> StructSerializer {
        StructSerializer {
            fields: Vec::with_capacity(len),
        }
    }
}

impl<'a> StructVariantSerializer<'a> {
    pub fn new(index: u32, variant: &'a str, len: usize) -> StructVariantSerializer {
        StructVariantSerializer {
            index,
            variant,
            fields: Vec::with_capacity(len),
        }
    }
}

impl<'b> ser::Serializer for &'b mut Serializer {
    type Ok = Value;
    type Error = Error;
    type SerializeSeq = SeqSerializer;
    type SerializeTuple = SeqSerializer;
    type SerializeTupleStruct = SeqSerializer;
    type SerializeTupleVariant = SeqVariantSerializer<'b>;
    type SerializeMap = MapSerializer;
    type SerializeStruct = StructSerializer;
    type SerializeStructVariant = StructVariantSerializer<'b>;

    fn serialize_bool(self, v: bool) -> Result<Self::Ok, Self::Error> {
        Ok(Value::Boolean(v))
    }

    fn serialize_i8(self, v: i8) -> Result<Self::Ok, Self::Error> {
        self.serialize_i32(i32::from(v))
    }

    fn serialize_i16(self, v: i16) -> Result<Self::Ok, Self::Error> {
        self.serialize_i32(i32::from(v))
    }

    fn serialize_i32(self, v: i32) -> Result<Self::Ok, Self::Error> {
        Ok(Value::Int(v))
    }

    fn serialize_i64(self, v: i64) -> Result<Self::Ok, Self::Error> {
        Ok(Value::Long(v))
    }

    fn serialize_u8(self, v: u8) -> Result<Self::Ok, Self::Error> {
        self.serialize_i32(i32::from(v))
    }

    fn serialize_u16(self, v: u16) -> Result<Self::Ok, Self::Error> {
        self.serialize_i32(i32::from(v))
    }

    fn serialize_u32(self, v: u32) -> Result<Self::Ok, Self::Error> {
        if v <= i32::MAX as u32 {
            self.serialize_i32(v as i32)
        } else {
            self.serialize_i64(i64::from(v))
        }
    }

    fn serialize_u64(self, v: u64) -> Result<Self::Ok, Self::Error> {
        if v <= i64::MAX as u64 {
            self.serialize_i64(v as i64)
        } else {
            Err(ser::Error::custom("u64 is too large"))
        }
    }

    fn serialize_f32(self, v: f32) -> Result<Self::Ok, Self::Error> {
        Ok(Value::Float(v))
    }

    fn serialize_f64(self, v: f64) -> Result<Self::Ok, Self::Error> {
        Ok(Value::Double(v))
    }

    fn serialize_char(self, v: char) -> Result<Self::Ok, Self::Error> {
        self.serialize_str(&once(v).collect::<String>())
    }

    fn serialize_str(self, v: &str) -> Result<Self::Ok, Self::Error> {
        Ok(Value::String(v.to_owned()))
    }

    fn serialize_bytes(self, v: &[u8]) -> Result<Self::Ok, Self::Error> {
        match *BYTES_TYPE.borrow() {
            BytesType::Bytes => Ok(Value::Bytes(v.to_owned())),
            BytesType::Fixed => Ok(Value::Fixed(v.len(), v.to_owned())),
        }
    }

    fn serialize_none(self) -> Result<Self::Ok, Self::Error> {
        Ok(Value::from(None::<Self::Ok>))
    }

    fn serialize_some<T>(self, value: &T) -> Result<Self::Ok, Self::Error>
    where
        T: Serialize + ?Sized,
    {
        let v = value.serialize(&mut Serializer::default())?;
        Ok(Value::from(Some(v)))
    }

    fn serialize_unit(self) -> Result<Self::Ok, Self::Error> {
        Ok(Value::Null)
    }

    fn serialize_unit_struct(self, _: &'static str) -> Result<Self::Ok, Self::Error> {
        self.serialize_unit()
    }

    fn serialize_unit_variant(
        self,
        _: &'static str,
        _variant_index: u32,
        variant: &'static str,
    ) -> Result<Self::Ok, Self::Error> {
        Ok(Value::String(variant.to_string()))
    }

    fn serialize_newtype_struct<T>(
        self,
        _: &'static str,
        value: &T,
    ) -> Result<Self::Ok, Self::Error>
    where
        T: Serialize + ?Sized,
    {
        value.serialize(self)
    }

    fn serialize_newtype_variant<T>(
        self,
        _: &'static str,
        index: u32,
        variant: &'static str,
        value: &T,
    ) -> Result<Self::Ok, Self::Error>
    where
        T: Serialize + ?Sized,
    {
        Ok(Value::Record(vec![
            ("type".to_owned(), Value::Enum(index, variant.to_owned())),
            (
                "value".to_owned(),
                Value::Union(index, Box::new(value.serialize(self)?)),
            ),
        ]))
    }

    fn serialize_seq(self, len: Option<usize>) -> Result<Self::SerializeSeq, Self::Error> {
        Ok(SeqSerializer::new(len))
    }

    fn serialize_tuple(self, len: usize) -> Result<Self::SerializeTuple, Self::Error> {
        self.serialize_seq(Some(len))
    }

    fn serialize_tuple_struct(
        self,
        _: &'static str,
        len: usize,
    ) -> Result<Self::SerializeTupleStruct, Self::Error> {
        self.serialize_seq(Some(len))
    }

    fn serialize_tuple_variant(
        self,
        _: &'static str,
        index: u32,
        variant: &'static str,
        len: usize,
    ) -> Result<Self::SerializeTupleVariant, Self::Error> {
        Ok(SeqVariantSerializer::new(index, variant, Some(len)))
    }

    fn serialize_map(self, len: Option<usize>) -> Result<Self::SerializeMap, Self::Error> {
        Ok(MapSerializer::new(len))
    }

    fn serialize_struct(
        self,
        _: &'static str,
        len: usize,
    ) -> Result<Self::SerializeStruct, Self::Error> {
        Ok(StructSerializer::new(len))
    }

    fn serialize_struct_variant(
        self,
        _: &'static str,
        index: u32,
        variant: &'static str,
        len: usize,
    ) -> Result<Self::SerializeStructVariant, Self::Error> {
        Ok(StructVariantSerializer::new(index, variant, len))
    }

    fn is_human_readable(&self) -> bool {
        crate::util::is_human_readable()
    }
}

impl ser::SerializeSeq for SeqSerializer {
    type Ok = Value;
    type Error = Error;

    fn serialize_element<T>(&mut self, value: &T) -> Result<(), Self::Error>
    where
        T: Serialize + ?Sized,
    {
        self.items
            .push(value.serialize(&mut Serializer::default())?);
        Ok(())
    }

    fn end(self) -> Result<Self::Ok, Self::Error> {
        Ok(Value::Array(self.items))
    }
}

impl ser::SerializeTuple for SeqSerializer {
    type Ok = Value;
    type Error = Error;

    fn serialize_element<T>(&mut self, value: &T) -> Result<(), Self::Error>
    where
        T: Serialize + ?Sized,
    {
        ser::SerializeSeq::serialize_element(self, value)
    }

    fn end(self) -> Result<Self::Ok, Self::Error> {
        ser::SerializeSeq::end(self)
    }
}

impl ser::SerializeTupleStruct for SeqSerializer {
    type Ok = Value;
    type Error = Error;

    fn serialize_field<T>(&mut self, value: &T) -> Result<(), Self::Error>
    where
        T: Serialize + ?Sized,
    {
        ser::SerializeSeq::serialize_element(self, value)
    }

    fn end(self) -> Result<Self::Ok, Self::Error> {
        ser::SerializeSeq::end(self)
    }
}

impl<'a> ser::SerializeSeq for SeqVariantSerializer<'a> {
    type Ok = Value;
    type Error = Error;

    fn serialize_element<T>(&mut self, value: &T) -> Result<(), Self::Error>
    where
        T: Serialize + ?Sized,
    {
        self.items.push(Value::Union(
            self.index,
            Box::new(value.serialize(&mut Serializer::default())?),
        ));
        Ok(())
    }

    fn end(self) -> Result<Self::Ok, Self::Error> {
        Ok(Value::Record(vec![
            (
                "type".to_owned(),
                Value::Enum(self.index, self.variant.to_owned()),
            ),
            ("value".to_owned(), Value::Array(self.items)),
        ]))
    }
}

impl<'a> ser::SerializeTupleVariant for SeqVariantSerializer<'a> {
    type Ok = Value;
    type Error = Error;

    fn serialize_field<T>(&mut self, value: &T) -> Result<(), Self::Error>
    where
        T: Serialize + ?Sized,
    {
        ser::SerializeSeq::serialize_element(self, value)
    }

    fn end(self) -> Result<Self::Ok, Self::Error> {
        ser::SerializeSeq::end(self)
    }
}

impl ser::SerializeMap for MapSerializer {
    type Ok = Value;
    type Error = Error;

    fn serialize_key<T>(&mut self, key: &T) -> Result<(), Self::Error>
    where
        T: Serialize + ?Sized,
    {
        let key = key.serialize(&mut Serializer::default())?;

        if let Value::String(key) = key {
            self.indices.insert(key, self.values.len());
            Ok(())
        } else {
            Err(ser::Error::custom("map key is not a string"))
        }
    }

    fn serialize_value<T>(&mut self, value: &T) -> Result<(), Self::Error>
    where
        T: Serialize + ?Sized,
    {
        self.values
            .push(value.serialize(&mut Serializer::default())?);
        Ok(())
    }

    fn end(self) -> Result<Self::Ok, Self::Error> {
        let mut items = HashMap::new();
        for (key, index) in self.indices {
            if let Some(value) = self.values.get(index) {
                items.insert(key, value.clone());
            }
        }

        Ok(Value::Map(items))
    }
}

impl ser::SerializeStruct for StructSerializer {
    type Ok = Value;
    type Error = Error;

    fn serialize_field<T>(&mut self, name: &'static str, value: &T) -> Result<(), Self::Error>
    where
        T: Serialize + ?Sized,
    {
        self.fields.push((
            name.to_owned(),
            value.serialize(&mut Serializer::default())?,
        ));
        Ok(())
    }

    fn end(self) -> Result<Self::Ok, Self::Error> {
        Ok(Value::Record(self.fields))
    }
}

impl<'a> ser::SerializeStructVariant for StructVariantSerializer<'a> {
    type Ok = Value;
    type Error = Error;

    fn serialize_field<T>(&mut self, name: &'static str, value: &T) -> Result<(), Self::Error>
    where
        T: Serialize + ?Sized,
    {
        self.fields.push((
            name.to_owned(),
            value.serialize(&mut Serializer::default())?,
        ));
        Ok(())
    }

    fn end(self) -> Result<Self::Ok, Self::Error> {
        Ok(Value::Record(vec![
            (
                "type".to_owned(),
                Value::Enum(self.index, self.variant.to_owned()),
            ),
            (
                "value".to_owned(),
                Value::Union(self.index, Box::new(Value::Record(self.fields))),
            ),
        ]))
    }
}

/// Interpret a serializable instance as a `Value`.
///
/// This conversion can fail if the value is not valid as per the Avro specification.
/// e.g: HashMap with non-string keys
pub fn to_value<S: Serialize>(value: S) -> Result<Value, Error> {
    let mut serializer = Serializer::default();
    value.serialize(&mut serializer)
}

/// A function that could be used by #[serde(serialize_with = ...)] to give a
/// hint to Avro's `Serializer` how to serialize a byte array like `[u8; N]` to
/// `Value::Fixed`
#[allow(dead_code)]
pub fn avro_serialize_fixed<S>(value: &[u8], serializer: S) -> Result<S::Ok, S::Error>
where
    S: ser::Serializer,
{
    serialize_bytes_type(value, serializer, BytesType::Fixed)
}

/// A function that could be used by #[serde(serialize_with = ...)] to give a
/// hint to Avro's `Serializer` how to serialize a byte array like `&[u8]` to
/// `Value::Bytes`
#[allow(dead_code)]
pub fn avro_serialize_bytes<S>(value: &[u8], serializer: S) -> Result<S::Ok, S::Error>
where
    S: ser::Serializer,
{
    serialize_bytes_type(value, serializer, BytesType::Bytes)
}

fn serialize_bytes_type<S>(
    value: &[u8],
    serializer: S,
    bytes_type: BytesType,
) -> Result<S::Ok, S::Error>
where
    S: ser::Serializer,
{
    *BYTES_TYPE.borrow_mut() = bytes_type;
    let res = serializer.serialize_bytes(value);
    *BYTES_TYPE.borrow_mut() = BytesType::Bytes;
    res
}

#[cfg(test)]
mod tests {
    use super::*;
    use apache_avro_test_helper::TestResult;
    use pretty_assertions::assert_eq;
    use serde::{Deserialize, Serialize};
    use serial_test::serial;
    use std::sync::atomic::Ordering;
    use serde_bytes::ByteArray;

    #[derive(Debug, Deserialize, Serialize, Clone)]
    struct Test {
        a: i64,
        b: String,
    }

    #[derive(Debug, Deserialize, Serialize)]
    struct TestInner {
        a: Test,
        b: i32,
    }

    #[derive(Debug, Deserialize, Serialize)]
    struct TestUnitExternalEnum {
        a: UnitExternalEnum,
    }

    #[derive(Debug, Deserialize, Serialize)]
    enum UnitExternalEnum {
        Val1,
        Val2,
    }

    #[derive(Debug, Deserialize, Serialize)]
    struct TestUnitInternalEnum {
        a: UnitInternalEnum,
    }

    #[derive(Debug, Deserialize, Serialize)]
    #[serde(tag = "t")]
    enum UnitInternalEnum {
        Val1,
        Val2,
    }

    #[derive(Debug, Deserialize, Serialize)]
    struct TestUnitAdjacentEnum {
        a: UnitAdjacentEnum,
    }

    #[derive(Debug, Deserialize, Serialize)]
    #[serde(tag = "t", content = "v")]
    enum UnitAdjacentEnum {
        Val1,
        Val2,
    }

    #[derive(Debug, Deserialize, Serialize)]
    struct TestUnitUntaggedEnum {
        a: UnitUntaggedEnum,
    }

    #[derive(Debug, Deserialize, Serialize)]
    #[serde(untagged)]
    enum UnitUntaggedEnum {
        Val1,
        Val2,
    }

    #[derive(Debug, Serialize, Deserialize)]
    struct TestSingleValueExternalEnum {
        a: SingleValueExternalEnum,
    }

    #[derive(Debug, Serialize, Deserialize)]
    enum SingleValueExternalEnum {
        Double(f64),
        String(String),
    }

    #[derive(Debug, Serialize, Deserialize)]
    struct TestSingleValueInternalEnum {
        a: SingleValueInternalEnum,
    }

    #[derive(Debug, Serialize, Deserialize)]
    #[serde(tag = "t")]
    enum SingleValueInternalEnum {
        Double(f64),
        String(String),
    }

    #[derive(Debug, Serialize, Deserialize)]
    struct TestSingleValueAdjacentEnum {
        a: SingleValueAdjacentEnum,
    }
    #[derive(Debug, Serialize, Deserialize)]
    #[serde(tag = "t", content = "v")]
    enum SingleValueAdjacentEnum {
        Double(f64),
        String(String),
    }

    #[derive(Debug, Serialize, Deserialize)]
    struct TestSingleValueUntaggedEnum {
        a: SingleValueUntaggedEnum,
    }

    #[derive(Debug, Serialize, Deserialize)]
    #[serde(untagged)]
    enum SingleValueUntaggedEnum {
        Double(f64),
        String(String),
    }

    #[derive(Debug, Serialize, Deserialize)]
    struct TestStructExternalEnum {
        a: StructExternalEnum,
    }

    #[derive(Debug, Serialize, Deserialize)]
    enum StructExternalEnum {
        Val1 { x: f32, y: f32 },
        Val2 { x: f32, y: f32 },
    }

    #[derive(Debug, Serialize, Deserialize)]
    struct TestStructInternalEnum {
        a: StructInternalEnum,
    }

    #[derive(Debug, Serialize, Deserialize)]
    #[serde(tag = "type")]
    enum StructInternalEnum {
        Val1 { x: f32, y: f32 },
        Val2 { x: f32, y: f32 },
    }

    #[derive(Debug, Serialize, Deserialize)]
    struct TestStructAdjacentEnum {
        a: StructAdjacentEnum,
    }

    #[derive(Debug, Serialize, Deserialize)]
    #[serde(tag = "t", content = "v")]
    enum StructAdjacentEnum {
        Val1 { x: f32, y: f32 },
        Val2 { x: f32, y: f32 },
    }

    #[derive(Debug, Serialize, Deserialize)]
    struct TestStructUntaggedEnum {
        a: StructUntaggedEnum,
    }

    #[derive(Debug, Serialize, Deserialize)]
    #[serde(untagged)]
    enum StructUntaggedEnum {
        Val1 { x: f32, y: f32 },
        Val2 { x: f32, y: f32, z: f32 },
    }

    #[derive(Debug, Serialize, Deserialize)]
    struct TestTupleExternalEnum {
        a: TupleExternalEnum,
    }

    #[derive(Debug, Serialize, Deserialize)]
    enum TupleExternalEnum {
        Val1(f32, f32),
        Val2(f32, f32, f32),
    }

    // Tuple Internal Enum cannot be instantiated

    #[derive(Debug, Serialize, Deserialize)]
    struct TestTupleAdjacentEnum {
        a: TupleAdjacentEnum,
    }

    #[derive(Debug, Serialize, Deserialize)]
    #[serde(tag = "t", content = "v")]
    enum TupleAdjacentEnum {
        Val1(f32, f32),
        Val2(f32, f32, f32),
    }

    #[derive(Debug, Serialize, Deserialize)]
    struct TestTupleUntaggedEnum {
        a: TupleUntaggedEnum,
    }

    #[derive(Debug, Serialize, Deserialize)]
    #[serde(untagged)]
    enum TupleUntaggedEnum {
        Val1(f32, f32),
        Val2(f32, f32, f32),
    }

    #[test]
    fn test_to_value() -> TestResult {
        let test = Test {
            a: 27,
            b: "foo".to_owned(),
        };
        let expected = Value::Record(vec![
            ("a".to_owned(), Value::Long(27)),
            ("b".to_owned(), Value::String("foo".to_owned())),
        ]);

        assert_eq!(to_value(test.clone())?, expected);

        let test_inner = TestInner { a: test, b: 35 };

        let expected_inner = Value::Record(vec![
            (
                "a".to_owned(),
                Value::Record(vec![
                    ("a".to_owned(), Value::Long(27)),
                    ("b".to_owned(), Value::String("foo".to_owned())),
                ]),
            ),
            ("b".to_owned(), Value::Int(35)),
        ]);

        assert_eq!(to_value(test_inner)?, expected_inner);

        Ok(())
    }

    #[test]
    fn test_to_value_unit_enum() -> TestResult {
        let test = TestUnitExternalEnum {
            a: UnitExternalEnum::Val1,
        };

        let expected = Value::Record(vec![("a".to_owned(), Value::String("Val1".to_owned()))]);

        assert_eq!(
            to_value(test)?,
            expected,
            "Error serializing unit external enum"
        );

        let test = TestUnitInternalEnum {
            a: UnitInternalEnum::Val1,
        };

        let expected = Value::Record(vec![(
            "a".to_owned(),
            Value::Record(vec![("t".to_owned(), Value::String("Val1".to_owned()))]),
        )]);

        assert_eq!(
            to_value(test)?,
            expected,
            "Error serializing unit internal enum"
        );

        let test = TestUnitAdjacentEnum {
            a: UnitAdjacentEnum::Val1,
        };

        let expected = Value::Record(vec![(
            "a".to_owned(),
            Value::Record(vec![("t".to_owned(), Value::String("Val1".to_owned()))]),
        )]);

        assert_eq!(
            to_value(test)?,
            expected,
            "Error serializing unit adjacent enum"
        );

        let test = TestUnitUntaggedEnum {
            a: UnitUntaggedEnum::Val1,
        };

        let expected = Value::Record(vec![("a".to_owned(), Value::Null)]);

        assert_eq!(
            to_value(test)?,
            expected,
            "Error serializing unit untagged enum"
        );

        Ok(())
    }

    #[test]
    fn test_to_value_single_value_enum() -> TestResult {
        let test = TestSingleValueExternalEnum {
            a: SingleValueExternalEnum::Double(64.0),
        };

        let expected = Value::Record(vec![(
            "a".to_owned(),
            Value::Record(vec![
                ("type".to_owned(), Value::Enum(0, "Double".to_owned())),
                (
                    "value".to_owned(),
                    Value::Union(0, Box::new(Value::Double(64.0))),
                ),
            ]),
        )]);

        assert_eq!(
            to_value(test)?,
            expected,
            "Error serializing single value external enum"
        );

        // It is not possible to serialize an internal Single Value enum...
        let test = TestSingleValueInternalEnum {
            a: SingleValueInternalEnum::Double(64.0),
        };

        assert!(to_value(test).is_err(), "{}", true);

        let test = TestSingleValueAdjacentEnum {
            a: SingleValueAdjacentEnum::Double(64.0),
        };

        let expected = Value::Record(vec![(
            "a".to_owned(),
            Value::Record(vec![
                ("t".to_owned(), Value::String("Double".to_owned())),
                ("v".to_owned(), Value::Double(64.0)),
            ]),
        )]);

        assert_eq!(
            to_value(test)?,
            expected,
            "Error serializing single value adjacent enum"
        );

        let test = TestSingleValueUntaggedEnum {
            a: SingleValueUntaggedEnum::Double(64.0),
        };

        let expected = Value::Record(vec![("a".to_owned(), Value::Double(64.0))]);

        assert_eq!(
            to_value(test)?,
            expected,
            "Error serializing single value untagged enum"
        );

        Ok(())
    }

    #[test]
    fn test_to_value_struct_enum() -> TestResult {
        let test = TestStructExternalEnum {
            a: StructExternalEnum::Val1 { x: 1.0, y: 2.0 },
        };
        let expected = Value::Record(vec![(
            "a".to_owned(),
            Value::Record(vec![
                ("type".to_owned(), Value::Enum(0, "Val1".to_owned())),
                (
                    "value".to_owned(),
                    Value::Union(
                        0,
                        Box::new(Value::Record(vec![
                            ("x".to_owned(), Value::Float(1.0)),
                            ("y".to_owned(), Value::Float(2.0)),
                        ])),
                    ),
                ),
            ]),
        )]);

        assert_eq!(
            to_value(test)?,
            expected,
            "error serializing struct external enum"
        );

        // I don't think that this is feasible in avro

        let test = TestStructInternalEnum {
            a: StructInternalEnum::Val1 { x: 1.0, y: 2.0 },
        };
        let expected = Value::Record(vec![(
            "a".to_owned(),
            Value::Record(vec![
                ("type".to_owned(), Value::String("Val1".to_owned())),
                ("x".to_owned(), Value::Float(1.0)),
                ("y".to_owned(), Value::Float(2.0)),
            ]),
        )]);

        assert_eq!(
            to_value(test)?,
            expected,
            "error serializing struct internal enum"
        );

        let test = TestStructAdjacentEnum {
            a: StructAdjacentEnum::Val1 { x: 1.0, y: 2.0 },
        };
        let expected = Value::Record(vec![(
            "a".to_owned(),
            Value::Record(vec![
                ("t".to_owned(), Value::String("Val1".to_owned())),
                (
                    "v".to_owned(),
                    Value::Record(vec![
                        ("x".to_owned(), Value::Float(1.0)),
                        ("y".to_owned(), Value::Float(2.0)),
                    ]),
                ),
            ]),
        )]);

        assert_eq!(
            to_value(test)?,
            expected,
            "error serializing struct adjacent enum"
        );

        let test = TestStructUntaggedEnum {
            a: StructUntaggedEnum::Val1 { x: 1.0, y: 2.0 },
        };
        let expected = Value::Record(vec![(
            "a".to_owned(),
            Value::Record(vec![
                ("x".to_owned(), Value::Float(1.0)),
                ("y".to_owned(), Value::Float(2.0)),
            ]),
        )]);

        assert_eq!(
            to_value(test)?,
            expected,
            "error serializing struct untagged enum"
        );

        let test = TestStructUntaggedEnum {
            a: StructUntaggedEnum::Val2 {
                x: 1.0,
                y: 2.0,
                z: 3.0,
            },
        };
        let expected = Value::Record(vec![(
            "a".to_owned(),
            Value::Record(vec![
                ("x".to_owned(), Value::Float(1.0)),
                ("y".to_owned(), Value::Float(2.0)),
                ("z".to_owned(), Value::Float(3.0)),
            ]),
        )]);

        assert_eq!(
            to_value(test)?,
            expected,
            "error serializing struct untagged enum variant"
        );

        Ok(())
    }

    #[test]
    fn test_to_value_tuple_enum() -> TestResult {
        let test = TestTupleExternalEnum {
            a: TupleExternalEnum::Val2(1.0, 2.0, 3.0),
        };

        let expected = Value::Record(vec![(
            "a".to_owned(),
            Value::Record(vec![
                ("type".to_owned(), Value::Enum(1, "Val2".to_owned())),
                (
                    "value".to_owned(),
                    Value::Array(vec![
                        Value::Union(1, Box::new(Value::Float(1.0))),
                        Value::Union(1, Box::new(Value::Float(2.0))),
                        Value::Union(1, Box::new(Value::Float(3.0))),
                    ]),
                ),
            ]),
        )]);

        assert_eq!(
            to_value(test)?,
            expected,
            "error serializing tuple external enum"
        );

        let test = TestTupleAdjacentEnum {
            a: TupleAdjacentEnum::Val1(1.0, 2.0),
        };

        let expected = Value::Record(vec![(
            "a".to_owned(),
            Value::Record(vec![
                ("t".to_owned(), Value::String("Val1".to_owned())),
                (
                    "v".to_owned(),
                    Value::Array(vec![Value::Float(1.0), Value::Float(2.0)]),
                ),
            ]),
        )]);

        assert_eq!(
            to_value(test)?,
            expected,
            "error serializing tuple adjacent enum"
        );

        let test = TestTupleUntaggedEnum {
            a: TupleUntaggedEnum::Val1(1.0, 2.0),
        };

        let expected = Value::Record(vec![(
            "a".to_owned(),
            Value::Array(vec![Value::Float(1.0), Value::Float(2.0)]),
        )]);

        assert_eq!(
            to_value(test)?,
            expected,
            "error serializing tuple untagged enum"
        );

        Ok(())
    }

    #[test]
    #[serial(avro_3747)]
    fn avro_3747_human_readable_false() {
        use serde::ser::Serializer as SerdeSerializer;

        crate::util::SERDE_HUMAN_READABLE.store(false, Ordering::Release);

        let ser = &mut Serializer {};

        assert_eq!(ser.is_human_readable(), false);
    }

    #[test]
    #[serial(avro_3747)]
    fn avro_3747_human_readable_true() {
        use serde::ser::Serializer as SerdeSerializer;

        crate::util::SERDE_HUMAN_READABLE.store(true, Ordering::Release);

        let ser = &mut Serializer {};

        assert!(ser.is_human_readable());
    }

    #[test]
    fn avro_3631_test_to_value_fixed_field() {
        #[derive(Debug, Serialize, Deserialize)]
        struct TestStructFixedField<'a> {
            // will be serialized as Value::Array<Vec<Value::Int>>
            array_field: &'a [u8],

            // will be serialized as Value::Fixed
            #[serde(serialize_with = "avro_serialize_fixed")]
            fixed_field: [u8; 6],
            #[serde(serialize_with = "avro_serialize_fixed")]
            fixed_field2: &'a [u8],

            // will be serialized as Value::Bytes
            #[serde(serialize_with = "avro_serialize_bytes")]
            bytes_field: &'a [u8],
            #[serde(serialize_with = "avro_serialize_bytes")]
            bytes_field2: [u8; 6],

            // will be serialized as Value::Array<Vec<Value::Int>>
            vec_field: Vec<u8>,
        }

        let test = TestStructFixedField {
            array_field: &[1, 11, 111],
            bytes_field: &[2, 22, 222],
            bytes_field2: [2; 6],
            fixed_field: [1; 6],
            fixed_field2: &[6, 66],
            vec_field: vec![3, 33],
        };
        let expected = Value::Record(vec![
            (
                "array_field".to_owned(),
                Value::Array(
                    test.array_field
                        .iter()
                        .map(|i| Value::Int(*i as i32))
                        .collect(),
                ),
            ),
            (
                "fixed_field".to_owned(),
                Value::Fixed(6, Vec::from(test.fixed_field.clone())),
            ),
            (
                "fixed_field2".to_owned(),
                Value::Fixed(2, Vec::from(test.fixed_field2.clone())),
            ),
            (
                "bytes_field".to_owned(),
                Value::Bytes(Vec::from(test.bytes_field.clone())),
            ),
            (
                "bytes_field2".to_owned(),
                Value::Bytes(Vec::from(test.bytes_field2.clone())),
            ),
            (
                "vec_field".to_owned(),
                Value::Array(
                    test.vec_field
                        .iter()
                        .map(|i| Value::Int(*i as i32))
                        .collect(),
                ),
            ),
        ]);
        assert_eq!(expected, to_value(test).unwrap());
    }
}

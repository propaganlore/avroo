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

use crate::{
    schema::{
        ArraySchema, DecimalSchema, EnumSchema, FixedSchema, MapSchema, RecordField, RecordSchema,
        UnionSchema,
    },
    Schema,
};
use std::sync::OnceLock;

/// A trait that compares two schemata.
/// To register a custom one use [set_schemata_equality_comparator].
pub trait SchemataEq: Send + Sync {
    /// Compares two schemata for equality.
    fn compare(&self, schema_one: &Schema, schema_two: &Schema) -> bool;
}

/// Compares two schemas according to the Avro specification by using
/// their canonical forms.
/// See https://avro.apache.org/docs/1.11.1/specification/#parsing-canonical-form-for-schemas
struct SpecificationEq;
impl SchemataEq for SpecificationEq {
    fn compare(&self, schema_one: &Schema, schema_two: &Schema) -> bool {
        schema_one.canonical_form() == schema_two.canonical_form()
    }
}

/// Compares two schemas field by field, using only the fields that
/// are used to construct their canonical forms.
/// See <https://avro.apache.org/docs/1.11.1/specification/#parsing-canonical-form-for-schemas>
pub struct StructFieldEq;
impl SchemataEq for StructFieldEq {
    fn compare(&self, schema_one: &Schema, schema_two: &Schema) -> bool {
        macro_rules! compare_primitive {
            ($primitive:ident) => {
                if let Schema::$primitive = schema_one {
                    if let Schema::$primitive = schema_two {
                        return true;
                    }
                    return false;
                }
            };
        }

        if schema_one.name() != schema_two.name() {
            return false;
        }

        compare_primitive!(Null);
        compare_primitive!(Boolean);
        compare_primitive!(Int);
        compare_primitive!(Int);
        compare_primitive!(Long);
        compare_primitive!(Float);
        compare_primitive!(Double);
        compare_primitive!(Bytes);
        compare_primitive!(String);
        compare_primitive!(Uuid);
        compare_primitive!(BigDecimal);
        compare_primitive!(Date);
        compare_primitive!(Duration);
        compare_primitive!(TimeMicros);
        compare_primitive!(TimeMillis);
        compare_primitive!(TimestampMicros);
        compare_primitive!(TimestampMillis);
        compare_primitive!(TimestampNanos);
        compare_primitive!(LocalTimestampMicros);
        compare_primitive!(LocalTimestampMillis);
        compare_primitive!(LocalTimestampNanos);

        if let Schema::Record(RecordSchema {
            fields: fields_one, ..
        }) = schema_one
        {
            if let Schema::Record(RecordSchema {
                fields: fields_two, ..
            }) = schema_two
            {
                return self.compare_fields(fields_one, fields_two);
            }
            return false;
        }

        if let Schema::Enum(EnumSchema {
            symbols: symbols_one,
            ..
        }) = schema_one
        {
            if let Schema::Enum(EnumSchema {
                symbols: symbols_two,
                ..
            }) = schema_two
            {
                return symbols_one == symbols_two;
            }
            return false;
        }

        if let Schema::Fixed(FixedSchema { size: size_one, .. }) = schema_one {
            if let Schema::Fixed(FixedSchema { size: size_two, .. }) = schema_two {
                return size_one == size_two;
            }
            return false;
        }

        if let Schema::Union(UnionSchema {
            schemas: schemas_one,
            ..
        }) = schema_one
        {
            if let Schema::Union(UnionSchema {
                schemas: schemas_two,
                ..
            }) = schema_two
            {
                return schemas_one.len() == schemas_two.len()
                    && schemas_one
                        .iter()
                        .zip(schemas_two.iter())
                        .all(|(s1, s2)| self.compare(s1, s2));
            }
            return false;
        }

        if let Schema::Decimal(DecimalSchema {
            precision: precision_one,
            scale: scale_one,
            ..
        }) = schema_one
        {
            if let Schema::Decimal(DecimalSchema {
                precision: precision_two,
                scale: scale_two,
                ..
            }) = schema_two
            {
                return precision_one == precision_two && scale_one == scale_two;
            }
            return false;
        }

        if let Schema::Array(ArraySchema {
            items: items_one, ..
        }) = schema_one
        {
            if let Schema::Array(ArraySchema {
                items: items_two, ..
            }) = schema_two
            {
                return items_one == items_two;
            }
            return false;
        }

        if let Schema::Map(MapSchema {
            types: types_one, ..
        }) = schema_one
        {
            if let Schema::Map(MapSchema {
                types: types_two, ..
            }) = schema_two
            {
                return self.compare(types_one, types_two);
            }
            return false;
        }

        if let Schema::Ref { name: name_one } = schema_one {
            if let Schema::Ref { name: name_two } = schema_two {
                return name_one == name_two;
            }
            return false;
        }

        false
    }
}

impl StructFieldEq {
    fn compare_fields(&self, fields_one: &[RecordField], fields_two: &[RecordField]) -> bool {
        fields_one.len() == fields_two.len()
            && fields_one
                .iter()
                .zip(fields_two.iter())
                .all(|(f1, f2)| self.compare(&f1.schema, &f2.schema))
    }
}

static SCHEMATA_COMPARATOR_ONCE: OnceLock<Box<dyn SchemataEq + Send + Sync>> = OnceLock::new();

/// Sets a custom schemata comparator.
///
/// Returns a unit if the registration was successful or the already
/// registered comparator if the registration failed.
///
/// **Note**: This function must be called before parsing any schema because this will
/// register the default comparator and the registration is one time only!
pub fn set_schemata_equality_comparator(
    comparator: Box<dyn SchemataEq + Send + Sync>,
) -> Result<(), Box<dyn SchemataEq + Send + Sync>> {
    debug!("Setting a custom schemata equality comparator.");
    SCHEMATA_COMPARATOR_ONCE.set(comparator)
}

pub(crate) fn compare_schemata(schema_one: &Schema, schema_two: &Schema) -> bool {
    SCHEMATA_COMPARATOR_ONCE
        .get_or_init(|| {
            // debug!("Going to use the default schemata comparator.");
            // Box::new(SpecificationComparator)
            Box::new(StructFieldEq) // TEMPORARY
        })
        .compare(schema_one, schema_two)
}

#[cfg(test)]
#[allow(non_snake_case)]
mod tests {
    use super::*;
    use crate::Schema;

    macro_rules! test_primitives {
        ($primitive:ident) => {
            paste::item! {
                #[test]
                fn [<test_avro_3939_compare_schemata_$primitive>]() {
                    assert!(compare_schemata(&Schema::$primitive, &Schema::$primitive))
                }
            }
        };
    }

    test_primitives!(Null);
    test_primitives!(Boolean);
    test_primitives!(Int);
    test_primitives!(Long);
    test_primitives!(Float);
    test_primitives!(Double);
    test_primitives!(Bytes);
    test_primitives!(String);
    test_primitives!(Uuid);
    test_primitives!(BigDecimal);
    test_primitives!(Date);
    test_primitives!(Duration);
    test_primitives!(TimeMicros);
    test_primitives!(TimeMillis);
    test_primitives!(TimestampMicros);
    test_primitives!(TimestampMillis);
    test_primitives!(TimestampNanos);
    test_primitives!(LocalTimestampMicros);
    test_primitives!(LocalTimestampMillis);
    test_primitives!(LocalTimestampNanos);
}

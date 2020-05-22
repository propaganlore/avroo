<?php

/**
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

namespace Apache\Avro\Schema;

/**
 * Field of an {@link AvroRecordSchema}
 * @package Avro
 */
class AvroField extends AvroSchema
{
    /**
     * @var string fields name attribute name
     */
    const FIELD_NAME_ATTR = 'name';

    /**
     * @var string
     */
    const DEFAULT_ATTR = 'default';

    /**
     * @var string
     */
    const ORDER_ATTR = 'order';

    /**
     * @var string
     */
    const ASC_SORT_ORDER = 'ascending';

    /**
     * @var string
     */
    const DESC_SORT_ORDER = 'descending';

    /**
     * @var string
     */
    const IGNORE_SORT_ORDER = 'ignore';

    /**
     * @var array list of valid field sort order values
     */
    private static $valid_field_sort_orders = array(
        self::ASC_SORT_ORDER,
        self::DESC_SORT_ORDER,
        self::IGNORE_SORT_ORDER
    );
    /**
     * @var string
     */
    private $name;
    /**
     * @var boolean whether or no there is a default value
     */
    private $has_default;
    /**
     * @var string field default value
     */
    private $default;
    /**
     * @var string sort order of this field
     */
    private $order;
    /**
     * @var boolean whether or not the AvroNamedSchema of this field is
     *              defined in the AvroNamedSchemata instance
     */
    private $is_type_from_schemata;

    /**
     * @param string $type
     * @param string $name
     * @param AvroSchema $schema
     * @param boolean $is_type_from_schemata
     * @param string $default
     * @param string $order
     * @todo Check validity of $default value
     * @todo Check validity of $order value
     */
    public function __construct(
        $name,
        $schema,
        $is_type_from_schemata,
        $has_default,
        $default,
        $order = null
    ) {
        if (!AvroName::is_well_formed_name($name)) {
            throw new AvroSchemaParseException('Field requires a "name" attribute');
        }

        $this->type = $schema;
        $this->is_type_from_schemata = $is_type_from_schemata;
        $this->name = $name;
        $this->has_default = $has_default;
        if ($this->has_default) {
            $this->default = $default;
        }
        self::check_order_value($order);
        $this->order = $order;
    }

    /**
     * @param string $order
     * @throws AvroSchemaParseException if $order is not a valid
     *                                  field order value.
     */
    private static function check_order_value($order)
    {
        if (!is_null($order) && !self::is_valid_field_sort_order($order)) {
            throw new AvroSchemaParseException(
                sprintf('Invalid field sort order %s', $order)
            );
        }
    }

    /**
     * @param string $order
     * @returns boolean
     */
    private static function is_valid_field_sort_order($order)
    {
        return in_array($order, self::$valid_field_sort_orders);
    }

    /**
     * @returns mixed
     */
    public function to_avro()
    {
        $avro = array(AvroField::FIELD_NAME_ATTR => $this->name);

        $avro[AvroSchema::TYPE_ATTR] = ($this->is_type_from_schemata)
            ? $this->type->qualified_name() : $this->type->to_avro();

        if (isset($this->default)) {
            $avro[AvroField::DEFAULT_ATTR] = $this->default;
        }

        if ($this->order) {
            $avro[AvroField::ORDER_ATTR] = $this->order;
        }

        return $avro;
    }

    /**
     * @returns string the name of this field
     */
    public function name()
    {
        return $this->name;
    }

    /**
     * @returns mixed the default value of this field
     */
    public function default_value()
    {
        return $this->default;
    }

    /**
     * @returns boolean true if the field has a default and false otherwise
     */
    public function has_default_value()
    {
        return $this->has_default;
    }
}

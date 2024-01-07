# JSON Schema to AVRO Schema Conversion Guide

## Primitive types

### Avro Primitive Types

From AVRO Spec <sup>[\[1\]](https://avro.apache.org/docs/1.10.2/spec.html#schema_primitive)</sup>:

> **Primitive Types**
>
> The set of primitive type names is:
> 
> * **null**: no value
> * **boolean**: a binary value
> * **int**: 32-bit signed integer
> * **long**: 64-bit signed integer
> * **float**: single precision (32-bit) IEEE 754 floating-point number
> * **double**: double precision (64-bit) IEEE 754 floating-point number
> * **bytes**: sequence of 8-bit unsigned bytes
> * **string**: unicode character sequence
>
> **Complex Types**
>
> Avro supports six kinds of complex types: **records**, **enums**, **arrays**, **maps**, **unions** and **fixed**.

### Json Schema Primitive Types

From Json Schema reference <sup>[\[2\]](https://json-schema.org/understanding-json-schema/reference/type)</sup>:

The type keyword is fundamental to JSON Schema. It specifies the data type for a schema.

> At its core, JSON Schema defines the following basic types:
> 
> * **string**
> * **number**
> * **integer**
> * **object**
> * **array**
> * **boolean**
> * **null**


### Conversion table

All Json Types will be automatically converted based on the table.

 | Json Schema | Avro Schema                      |
 |:-----------:|:--------------------------------:|
 | `string`    | `string`                         |
 | `number`    | `int`, `long`, `float`, `double` |
 | `integer`   | `int`, `long`                    |
 | `object`    | `record`                         |
 | `array`     | `array`                          |
 | `boolean`   | `boolean`                        |
 | `null`      | `null`                           |

#### Features conversion

Json Schema has some features that needs to be converted in types

1. Fields in Json Schema are by default optional. They are required to exists, only if they exists in `required` list. Be a required field does not mean it cannot be null, but only ity should exists. <sup>[\[3\]](https://json-schema.org/draft/2020-12/draft-bhutton-json-schema-validation-01#name-required)</sup>

   What does this imply during type conversion? Fields not mentioned in `required` must automatically accept `null` type. The one that are mentioned should only have `null` type if `null` is defined as a type.
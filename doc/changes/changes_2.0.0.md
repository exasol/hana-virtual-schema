# Virtual Schema for SAP Hana 2.0.0, released 2021-02-10

Code name: Removed SQL_DIALECT property, fixed String datatypes mapping and mapping for literal boolean

## Summary

The `SQL_DIALECT` property used when executing a `CREATE VIRTUAL SCHEMA` from the Exasol database is obsolete from this version. Please, do not provide this property anymore.

## Bug Fixes

* #17: Fixed mapping for literal boolean.
* #6: Fixed mapping for String datatypes.

## Dependence Updates

### Runtime Dependencies

* Updated `com.exasol:virtual-schema-common-jdbc:9.0.0` to `9.0.1`

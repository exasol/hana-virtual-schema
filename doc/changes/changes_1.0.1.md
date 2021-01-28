# Virtual Schema for SAP Hana 1.0.1, released 2021-01-28

Code name: Restricted the amount of mapped tables to 1000 and fixed SELECT * with unsupported data types behavior

## Summary

This release includes the following changes from the updated common libraries:

- Restricted the amount of mapped tables in the remote schema to 1000;
- Fixed the problem with SELECT * and unsupported data types;

## Refactoring

* #8: Updated to the latest `virtual-schema-common-jdbc`.

## Dependence Updates

### Runtime Dependencies

* Updated `com.exasol:virtual-schema-common-jdbc:7.0.0` to `9.0.0`
* Updated `com.exasol:error-reporting-java:0.2.0` to `0.2.2`

### Test Dependencies

* Updated ` org.mockito:mockito-junit-jupiter:3.6.0` to `3.7.7`
* Removed `com.exasol:test-db-builder-java:2.0.0`
* Removed `org.testcontainers:junit-jupiter:1.15.0`
* Removed `com.exasol:exasol-testcontainers:3.3.1`
* Removed `com.exasol:hamcrest-resultset-matcher:1.2.1`

### Plugin Dependencies

* Updated `com.exasol:project-keeper-maven-plugin:0.4.1` to `0.4.2`
* Removed `org.jacoco.agent:org.jacoco:0.8.5`
* Removed `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M3`
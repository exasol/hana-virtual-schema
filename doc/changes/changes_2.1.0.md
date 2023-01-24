# Virtual Schema for SAP Hana 2.1.0, released 2023-01-24

Code name: Enhanced Data Type Detection for Result Sets Latest

## Summary

Starting with version 7.1.14 Exasol database uses the capabilities reported by each virtual schema to provide select list data types for each push down request. Based on this information the JDBC virtual schemas no longer need to infer the data types of the result set by inspecting its values. Instead the JDBC virtual schemas can now use the information provided by the database.

This release provides enhanced data type detection for result sets by updating virtual-schema-common-jdbc to version [10.1.0](https://github.com/exasol/virtual-schema-common-jdbc/releases/tag/10.1.0). If this new detection mechanism causes issues (e.g. with encoding of `CHAR` and `VARCHAR` types) you can disable it by setting `IMPORT_DATA_TYPES` to value `FROM_RESULT_SET` when creating the virtual schema. See the documentation of [JDBC adapter properties](https://github.com/exasol/virtual-schema-common-jdbc/blob/main/README.md#adapter-properties-for-jdbc-based-virtual-schemas) for details.

We also updated dependencies and added integration tests using the [saplabs/hanaexpress](https://hub.docker.com/r/saplabs/hanaexpress) Docker image.

## Features

* #29: Updated to VSCJDBC 10.1.0

## Refactoring

* #5: Added integration tests

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:0.4.1` to `1.0.0`
* Updated `com.exasol:virtual-schema-common-jdbc:9.0.4` to `10.1.0`

### Test Dependency Updates

* Added `com.exasol:exasol-testcontainers:6.5.0`
* Added `com.exasol:hamcrest-resultset-matcher:1.5.2`
* Added `com.exasol:test-db-builder-java:3.4.2`
* Added `com.exasol:udf-debugging-java:0.6.7`
* Added `com.sap.cloud.db.jdbc:ngdbc:2.15.10`
* Updated `org.junit.jupiter:junit-jupiter:5.8.1` to `5.9.2`
* Updated `org.mockito:mockito-junit-jupiter:4.1.0` to `5.0.0`
* Added `org.slf4j:slf4j-jdk14:2.0.6`
* Added `org.testcontainers:jdbc:1.17.6`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.4.0` to `0.4.2`
* Updated `com.exasol:error-code-crawler-maven-plugin:0.1.1` to `1.2.1`
* Updated `com.exasol:project-keeper-maven-plugin:1.3.2` to `2.9.1`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.13` to `0.16`
* Updated `org.apache.maven.plugins:maven-assembly-plugin:3.3.0` to `3.4.2`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.8.1` to `3.10.1`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0` to `3.1.0`
* Added `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M7`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.2.0` to `3.3.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M5` to `3.0.0-M7`
* Added `org.codehaus.mojo:flatten-maven-plugin:1.3.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.8.1` to `2.13.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.7` to `0.8.8`
* Added `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184`
* Updated `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.1.0` to `3.2.0`

# Hana Virtual Schema

[![Build Status](https://travis-ci.com/exasol/hana-virtual-schema.svg?branch=main)](https://travis-ci.com/exasol/hana-virtual-schema)

SonarCloud results:

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Ahana-virtual-schema&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.exasol%3Ahana-virtual-schema)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Ahana-virtual-schema&metric=security_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Ahana-virtual-schema)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Ahana-virtual-schema&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Ahana-virtual-schema)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Ahana-virtual-schema&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Ahana-virtual-schema)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Ahana-virtual-schema&metric=sqale_index)](https://sonarcloud.io/dashboard?id=com.exasol%3Ahana-virtual-schema)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Ahana-virtual-schema&metric=code_smells)](https://sonarcloud.io/dashboard?id=com.exasol%3Ahana-virtual-schema)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Ahana-virtual-schema&metric=coverage)](https://sonarcloud.io/dashboard?id=com.exasol%3Ahana-virtual-schema)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Ahana-virtual-schema&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=com.exasol%3Ahana-virtual-schema)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Ahana-virtual-schema&metric=ncloc)](https://sonarcloud.io/dashboard?id=com.exasol%3Ahana-virtual-schema)

# Overview

The **Hana Virtual Schema** provides an abstraction layer that makes an external [Hana](https://www.sap.com/products/hana.html) accessible from an Exasol database through regular SQL commands. The contents of the external Hana database are mapped to virtual tables which look like and can be queried as any regular Exasol table.

If you want to set up a Virtual Schema for a different database system, please head over to the [Virtual Schemas Repository][virtual-schemas].

## Features

* Access a Hana database in read only mode from an Exasol database, using a Virtual Schema.

## Table of Contents

### Information for Users

* [Hana Dialect User Guide](doc/user_guide/user_guide.md)
* [Changelog](doc/changes/changelog.md)

Find all the documentation in the [Virtual Schemas project][virtual-schemas].

## Information for Developers

* [Virtual Schema API Documentation][vs-api]

## Dependencies

### Use of Third-Party Software 

The Virtual Schema requires downloading and usage of a third-party JDBC driver.
If you use a third-party resource you will be a subject to its terms and licenses which may differ from our terms and licenses. 
We recommend you to familiarize yourself with JDBC driver's license and use terms.

### Run Time Dependencies

Running the Virtual Schema requires a Java Runtime version 11 or later.

| Dependency                                                         | Purpose                                              | License                    |
|--------------------------------------------------------------------|------------------------------------------------------|----------------------------|
| [Exasol Virtual Schema JDBC][virtual-schema-common-jdbc]           | Common JDBC functions for Virtual Schemas adapters   | MIT License                |
| [Exasol Error Reporting][exasol-error-reporting]                   | Creating unified error messages                      | MIT License                |
| [Hana JDBC Driver][hana-jdbc-driver]                               | JDBC driver for DB2 database                         | Check JDBC driver license  |

### Test Dependencies

| Dependency                                                         | Purpose                                              | License                    |
|--------------------------------------------------------------------|------------------------------------------------------|----------------------------|
| [Apache Maven](https://maven.apache.org/)                          | Build tool                                           | Apache License 2.0         |
| [Java Hamcrest](http://hamcrest.org/JavaHamcrest/)                 | Checking for conditions in code via matchers         | BSD License                |
| [JUnit](https://junit.org/junit5)                                  | Unit testing framework                               | Eclipse Public License 1.0 |
| [Mockito](http://site.mockito.org/)                                | Mocking framework                                    | MIT License                |

### Maven Plug-in

| Plug-in                                                            | Purpose                                              | License                    |
|--------------------------------------------------------------------|------------------------------------------------------|----------------------------|
| [Maven Surefire Plugin][maven-surefire-plugin]                     | Unit testing                                         | Apache License 2.0         |
| [Maven Jacoco Plugin][maven-jacoco-plugin]                         | Code coverage metering                               | Eclipse Public License 2.0 |
| [Maven Compiler Plugin][maven-compiler-plugin]                     | Setting required Java version                        | Apache License 2.0         |
| [Maven Assembly Plugin][maven-assembly-plugin]                     | Creating JAR                                         | Apache License 2.0         |
| [Sonatype OSS Index Maven Plugin][sonatype-oss-index-maven-plugin] | Checking Dependencies Vulnerability                  | ASL2                       |
| [Versions Maven Plugin][versions-maven-plugin]                     | Checking if dependencies updates are available       | Apache License 2.0         |
| [Maven Enforcer Plugin][maven-enforcer-plugin]                     | Controlling environment constants                    | Apache License 2.0         |
| [Artifact Reference Checker Plugin][artifact-ref-checker-plugin]   | Check if artifact is referenced with correct version | MIT License                |
| [Project Keeper Maven Plugin][project-keeper-maven-plugin]         | Checking project structure                           | MIT License                |

[virtual-schema-common-jdbc]: https://github.com/exasol/virtual-schema-common-jdbc
[exasol-error-reporting]: https://github.com/exasol/error-reporting-java/
[hana-jdbc-driver]: https://search.maven.org/search?q=g:com.sap.cloud.db.jdbc%20AND%20a:ngdbc&core=gav

[maven-surefire-plugin]: https://maven.apache.org/surefire/maven-surefire-plugin/
[maven-jacoco-plugin]: https://www.eclemma.org/jacoco/trunk/doc/maven.html
[maven-compiler-plugin]: https://maven.apache.org/plugins/maven-compiler-plugin/
[maven-assembly-plugin]: https://maven.apache.org/plugins/maven-assembly-plugin/
[sonatype-oss-index-maven-plugin]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[versions-maven-plugin]: https://www.mojohaus.org/versions-maven-plugin/
[maven-enforcer-plugin]: http://maven.apache.org/enforcer/maven-enforcer-plugin/
[artifact-ref-checker-plugin]: https://github.com/exasol/artifact-reference-checker-maven-plugin
[project-keeper-maven-plugin]: https://github.com/exasol/project-keeper-maven-plugin

[virtual-schemas]: https://github.com/exasol/virtual-schemas
[vs-api]: https://github.com/exasol/virtual-schema-common-java/blob/master/doc/development/api/virtual_schema_api.md
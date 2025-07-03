# Virtual Schema for SAP Hana 3.0.1, released 2025-07-03

Code name: Security updates

## Summary

This release is a security update. We updated the dependencies of the project to fix transitive security issues.

We also added an exception for the OSSIndex for CVE-2024-55551, which is a false positive in Exasol's JDBC driver.
This issue has been fixed quite a while back now, but the OSSIndex unfortunately does not contain the fix version of 24.2.1 (2024-12-10) set.

## Security

* #42: Fix CVE-2024-55551 in com.exasol:exasol-jdbc:jar:7.1.20:test

## Dependency Updates

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:7.0.1` to `7.1.6`
* Updated `com.exasol:hamcrest-resultset-matcher:1.6.5` to `1.7.1`
* Updated `com.exasol:test-db-builder-java:3.5.4` to `3.6.2`
* Updated `com.exasol:udf-debugging-java:0.6.12` to `0.6.16`
* Updated `com.sap.cloud.db.jdbc:ngdbc:2.19.16` to `2.25.9`
* Updated `org.hamcrest:hamcrest:2.2` to `3.0`
* Updated `org.junit.jupiter:junit-jupiter:5.10.2` to `5.13.2`
* Updated `org.mockito:mockito-junit-jupiter:5.11.0` to `5.18.0`
* Updated `org.slf4j:slf4j-jdk14:2.0.12` to `2.0.17`
* Updated `org.testcontainers:jdbc:1.19.7` to `1.21.3`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.4.2` to `0.4.3`
* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.1` to `2.0.3`
* Updated `com.exasol:project-keeper-maven-plugin:4.2.0` to `5.2.2`
* Added `com.exasol:quality-summarizer-maven-plugin:0.2.0`
* Added `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.1`
* Removed `io.github.zlika:reproducible-build-maven-plugin:0.16`
* Added `org.apache.maven.plugins:maven-artifact-plugin:3.6.0`
* Updated `org.apache.maven.plugins:maven-assembly-plugin:3.6.0` to `3.7.1`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.2.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.12.1` to `3.14.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.2.5` to `3.5.3`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.2` to `3.1.4`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.3.0` to `3.4.2`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.12.1` to `3.21.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.2.5` to `3.5.3`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.1.0` to `3.2.0`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.6.0` to `1.7.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.16.2` to `2.18.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.11` to `0.8.13`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.10.0.2594` to `5.1.0.4751`

# Hana Virtual Schema

[![Build Status](https://github.com/exasol/hana-virtual-schema/actions/workflows/ci-build.yml/badge.svg)](https://github.com/exasol/hana-virtual-schema/actions/workflows/ci-build.yml)

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
* [Dependencies](dependencies.md)

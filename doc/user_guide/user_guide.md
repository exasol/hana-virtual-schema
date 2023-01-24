# Hana SQL Dialect User Guide

The Hana SQL dialect allows you to access [Hana](https://www.sap.com/products/hana.html) databases via Virtual Schemas.

## Registering the JDBC Driver in EXAOperation

Download the latest version of the [SAP HANA JDBC driver](https://search.maven.org/search?q=g:com.sap.cloud.db.jdbc%20AND%20a:ngdbc&core=gav).

Now register the driver in EXAOperation:

1. Click "Software"
1. Switch to tab "JDBC Drivers"
1. Click "Browse..."
1. Select JDBC driver file
1. Click "Upload"
1. Click "Add"
1. In dialog "Add EXACluster JDBC driver" configure the JDBC driver (see below)

You need to specify the following settings when adding the JDBC driver via EXAOperation.

| Parameter | Value                                               |
|-----------|-----------------------------------------------------|
| Name      | `SAPHANA`                                           |
| Main      | `com.sap.db.jdbc.Driver`                            |
| Prefix    | `jdbc:sap:`                                         |
| Files     | `ngdbc-<JDBC driver version>.jar`                   |

## Uploading the JDBC Driver to EXAOperation

1. [Create a bucket in BucketFS](https://docs.exasol.com/administration/on-premise/bucketfs/create_new_bucket_in_bucketfs_service.htm) 
1. Upload the driver to BucketFS

This step is necessary since the UDF container the adapter runs in has no access to the JDBC drivers installed via EXAOperation but it can access BucketFS.

## Installing the Adapter Script

Upload the latest available release of [Hana Virtual Schema](https://github.com/exasol/hana-virtual-schema/releases) to Bucket FS.

Then create a schema to hold the adapter script.

```sql
CREATE SCHEMA ADAPTER;
```

The SQL statement below creates the adapter script, defines the Java class that serves as entry point and tells the UDF framework where to find the libraries (JAR files) for Virtual Schema and database driver.

```sql
CREATE JAVA ADAPTER SCRIPT ADAPTER.JDBC_ADAPTER AS
     %scriptclass com.exasol.adapter.RequestDispatcher;
     %jar /buckets/<BFS service>/<bucket>/virtual-schema-dist-10.1.0-hana-2.1.0.jar;
     %jar /buckets/<BFS service>/<bucket>/ngdbc-<JDBC driver version>.jar;
/
;
```

## Defining a Named Connection
    
```sql
CREATE OR REPLACE CONNECTION HANA_CONNECTION 
TO 'jdbc:sap://<HANA host or IP address>:<port>' 
USER '<user>' 
IDENTIFIED BY '<password>';
```

## Creating a Virtual Schema

Below you see how a Hana Virtual Schema is created. Please note that you have to provide the name of the database in the property `SCHEMA_NAME`.

```sql
CREATE VIRTUAL SCHEMA <virtual schema name>
    USING ADAPTER.JDBC_ADAPTER 
    WITH
    CONNECTION_NAME = 'HANA_CONNECTION'
    SCHEMA_NAME = '<schema name>';
```

## Data Types Conversion

| Hana Data Type | Supported | Converted Exasol Data Type | Known limitations
|----------------|---------- |----------------------------|-------------------
| ALPHANUM       | ✓         | VARCHAR UTF-8              |
| ARRAY          | ×         |                            |
| BIGINT         | ✓         | DECIMAL(19,0)              |
| BLOB           | ×         |                            |
| BOOLEAN        | ✓         | BOOLEAN                    |
| CLOB           | ×         |                            |
| DATE           | ✓         | DATE                       |
| DECIMAL        | ✓         | DECIMAL                    |
| DOUBLE         | ✓         | DOUBLE PRECISION           |
| INTEGER        | ✓         | DECIMAL(10,0)              |
| NCLOB          | ×         |                            |
| NVARCHAR       | ✓         | VARCHAR UTF-8              |
| REAL           | ✓         | DOUBLE PRECISION           |
| SECONDDATE     | ✓         | TIMESTAMP                  |
| SHORTTEXT      | ✓         | VARCHAR ASCII              |
| SMALLDECIMAL   | ✓         | DECIMAL                    |
| SMALLINT       | ✓         | DECIMAL(5,0)               |
| ST_GEOMETRY    | ×         |                            |
| ST_POINT       | ×         |                            |
| TEXT           | ×         |                            |
| TIME           | ✓         | VARCHAR(100)               |
| TIMESTAMP      | ✓         | TIMESTAMP                  |
| TINYINT        | ✓         | DECIMAL(3.0)               |
| VARBINARY      | ×         |                            |
| VARCHAR        | ✓         | VARCHAR ASCII              |

## Known Issues

### Unparameterized Column Type `DECIMAL`

In Hana you are allowed to create columns of type `DECIMAL` without parameterizing them. I.e. you can skip the part in the brackets.

What the Virtual Schemas get from the Hana JDBC driver as column metadata is a column of precision 34 and scale 0. So in theory this column's values should behave like a 34-digit integer number. Tests that we conducted with a SQL editor though show that the values can have fractional digits. In fact values of this column type behave like floating point numbers.

Unfortunately we can't tell the metadata of columns defined with `DECIMAL` and `DECIMAL(34,0)` apart even though they behave differently.

To fix this, don't define any columns that you plan to use via a Virtual Schema with unparameterized type `DECIMAL`.

### Column Type `SMALLDECIMAL`

The type `SMALLDECIMAL` exhibits the same behavior as the [unparameterized Column Type `DECIMAL`](#unparameterized-column-type-decimal).

Also here the only solution is to not use it in conjunction with a Virtual Schema.

### Column Type `TIME`

The type `TIME` always comes to Virtual Schema as a `TIMESTAMP` data type therefore it has not only time, but also date.
For now, it is always a current date. Example: 10:30:25 will be 27.06.2019 10:30:25.0 where date is a current date. 

## Testing information

| Virtual Schema Version | Hana Version                       | Driver Name and Version |
|------------------------|------------------------------------|-------------------------|
| 1.0.1                  | hanaexpress:2.00.045.00.20200121.1 | ngdbc-2.4.56.jar        |

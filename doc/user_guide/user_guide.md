# Hana SQL Dialect User Guide

The Hana SQL dialect allows you to access [Hana](https://www.sap.com/products/hana.html) databases via Virtual Schemas.

## Telemetry

This virtual schema uses `telemetry-java` to send anonymous feature-usage events.

For details on what is collected and how to disable telemetry, see the [documentation](https://github.com/exasol/telemetry-java/blob/main/doc/app-user-guide.md).

## Uploading the JDBC Driver to Exasol BucketFS

1. Download the [SAP HANA JDBC driver](https://central.sonatype.com/artifact/com.sap.cloud.db.jdbc/ngdbc).
2. Upload the driver to BucketFS, see [BucketFS documentation](https://docs.exasol.com/db/latest/administration/on-premise/bucketfs/accessfiles.htm).

    Hint: Put the driver into folder `default/drivers/jdbc/` to register it for [ExaLoader](#registering-the-jdbc-driver-for-exaloader), too.

## Registering the JDBC driver for ExaLoader

In order to enable the ExaLoader to fetch data from the external database you must register the driver for ExaLoader as described in the [Installation procedure for JDBC drivers](https://github.com/exasol/docker-db/#installing-custom-jdbc-drivers).
1. ExaLoader expects the driver in BucketFS folder `default/drivers/jdbc`.

   If you uploaded the driver for UDF to a different folder, then you need to [upload](#uploading-the-jdbc-driver-to-exasol-bucketfs) the driver again.
2. Additionally  you need to create file `settings.cfg` and [upload](#uploading-the-jdbc-driver-to-exasol-bucketfs) it to the same folder in BucketFS:

   ```properties
   DRIVERNAME=HANA
   JAR=ngdbc.jar
   DRIVERMAIN=com.sap.db.jdbc.Driver
   PREFIX=jdbc:sap:
   NOSECURITY=YES
   FETCHSIZE=100000
   INSERTSIZE=-1
   
   ```
   Ensure that the file ends with a trailing newline.

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
     %jar /buckets/<BFS service>/<bucket>/virtual-schema-dist-14.0.2-hana-4.0.1.jar;
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

## Testing Information

| Virtual Schema Version | Hana Version                       | Driver Name and Version |
|------------------------|------------------------------------|-------------------------|
| 1.0.1                  | hanaexpress:2.00.045.00.20200121.1 | ngdbc-2.4.56.jar        |
| 3.0.1                  | hanaexpress:2.00.082.00.20250528.1 | ngdbc-2.25.9.jar        |
| 4.0.0                  | hanaexpress:2.00.088.00.20251110.1 | ngdbc-2.28.7.jar        |

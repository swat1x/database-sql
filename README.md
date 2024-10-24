# Database connection library
##### [Русскоязычная версия](/README-ru.md)
## How to import?

Maven
``` xml
<dependency>
    <groupId>ru.swat1x</groupId>
    <artifactId>database-sql</artifactId>
    <version>1.13</version>
</dependency>
```

Gradle - Groovy
``` groovy
implementation 'ru.swat1x:database-sql:1.13' 
```

Gradle - Kotlin
``` kotlin
implementation("ru.swat1x:database-sql:1.13")
```

> Its also using HikariCP dependency

## Sample usage

### Connect to database

``` java
SQLDatabase database = new DatabaseBuilder()
    .poolConfig(PoolConfig.builder() // HikariCP config builder
        .poolName("my-example-pool")
        .driver(Drivers.MARIADB)
        .build())
    .host("localhost:3306")
    .username("exampleUsername")
    .password("examplePassword")
    .database("exampleDatabase")
    .build();
```

You also can connect with no password and not select exact database

``` java
SQLDatabase database = new DatabaseBuilder()
    .poolConfig(PoolConfig.defaultConfig()) // For defaults it's already there
    .host("database.example.com:3306")
    .dirver(Drivers.MARIADB)
    .username("exampleUsername")
    .build()
```

### Execute queries

``` java
// Async get and return data
CompletableFuture<Integer> someFutureValue = 
    database.query()
        .async()
        .execute("select * from `my_database` where `id`=?, `name`=?",
                 result -> { // processor with return value
                   return rs.next() ? rs.getInt("some_column") : null
                 }, 
                 "id_to_find_data", // values to fill `?`
                 "name_to_find_data"
               );
someFutureValue.thenAccept(asyncValue -> {
    // ...processMyAsyncValue(asyncValue);
});

// Sync get and processing
database.query()
    .sync()
    .execute("select * from `my_database` where `id`=?",
             result -> { // void processor 
               String value = rs.next() ? rs.getString("some_column") : null;
               // ...processMySyncValue(value)                
             },
             "id_to_find_data" // values to fill `?`
           );
```

### Drivers
Library have default drivers
`Drivers.MARIADB` & `Drivers.MYSQL` but you can implement `SQLDriver` interface\
Java library with each driver you need to setup by yourself like [MariaDB](https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client) or [MySQL](https://mvnrepository.com/artifact/com.mysql/mysql-connector-j)

### Logger _Important!_
If you are using a third-party logger - setup it to `SQLDatabase.setLogger(...)`
``` java
// If you using SLF4J logger
DatabaseLogger slf4jDatabaseLogger = new Slf4jDatabaseLogger(slf4jLogger);
// If you using Log4j logger
DatabaseLogger log4jDatabaseLogger = new Log4jDatabaseLogger(log4jLogger);
```
\
For self-made build use `./gradlew shadowJar`


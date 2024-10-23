# Библиотека для соединения с базой данных

## Как импортировать?

Maven
``` xml
<dependency>
    <groupId>ru.swat1x</groupId>
    <artifactId>database-sql</artifactId>
    <version>1.12</version>
</dependency>
```

Gradle - Groovy
``` groovy
implementation 'ru.swat1x:database-sql:1.12' 
```

Gradle - Kotlin
``` groovy
implementation("ru.swat1x:database-sql:1.12")
```

> Так же используется HikariCP зависимость

## Пример использования

### Подключение к базе данных

``` java
SQLDatabase database = new SQLDatabase(
    Drivers.MARIADB,
    SQLDatabase.Host.of("database.example.com", 3306),
    "myDatabaseName",
    SQLDatabase.Credentials.of("myDatabaseUsername", "myDatabasePassword"),
    PoolConfig.builder() // HikariCP config builder
        .maximumPoolSize(25)
        .minimumIdle(25)
        .parameter("useSsl", "true")
        .build()
    );
```

Так же можете подключаться не к конкретной базе и без пароля

``` java
SQLDatabase database = new DatabaseBuilder()
    .host("database.example.com:3306")
    .dirver(Drivers.MARIADB)
    .username("exampleUsername")
```

### Выполнения запросов

``` java
// Асинхронно получить данные, обработать и вернуть объект
CompletableFuture<Integer> someFutureValue = 
    database.query()
        .async()
        .execute("select * from `my_database` where `id`=?, `name`=?",
                 result -> { // обработчик возвращающий объект
                   return rs.next() ? rs.getInt("some_column") : null
                 }, 
                 "id_to_find_data", // значения для заполнения `?`
                 "name_to_find_data"
               );
someFutureValue.thenAccept(asyncValue -> {
    // ...processMyAsyncValue(asyncValue);
});

// Синхронное получение данных и обработка
database.query()
    .sync()
    .execute("select * from `my_database` where `id`=?",
             result -> { // void обработчик
               String value = rs.next() ? rs.getString("some_column") : null;
               // ...processMySyncValue(value)                
             },
             "id_to_find_data" // значения для заполнения `?`
           );
```

### Драйверы
Библиотека имеет стандартные драйверы, такие как
`Drivers.MARIADB` & `Drivers.MYSQL`, но Вы можете реализовать свой драйвер, используя `SQLDriver` интерфейс\
Сами библиотеки драйверов для Java импортируйте отдельно, такие как [MariaDB](https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client) или [MySQL](https://mvnrepository.com/artifact/com.mysql/mysql-connector-j)

### Логгер _Важно!_
Если Вы используете сторонний логер - установите его с помощью `SQLDatabase.setLogger(...)`
``` java
// Если используете логгер SLF4J
DatabaseLogger slf4jDatabaseLogger = new Slf4jDatabaseLogger(slf4jLogger);
// Если используете логгер Log4j
DatabaseLogger log4jDatabaseLogger = new Log4jDatabaseLogger(log4jLogger);
```
\
Для сборки собственного артефакта, используйте `./gradlew shadowJar`


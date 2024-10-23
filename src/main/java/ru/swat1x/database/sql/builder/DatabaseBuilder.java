package ru.swat1x.database.sql.builder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.swat1x.database.sql.PoolConfig;
import ru.swat1x.database.sql.SQLDatabase;
import ru.swat1x.database.sql.SQLDriver;
import ru.swat1x.database.sql.driver.Drivers;

@Setter
@Getter
@Accessors(fluent = true, chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatabaseBuilder {

  @NotNull
  String host;
  @Nullable
  String database;

  @NotNull
  String username;
  @Nullable
  String password;

  @NotNull
  SQLDriver driver = Drivers.MYSQL;

  @NotNull
  PoolConfig poolConfig = PoolConfig.defaultConfig();


  public DatabaseBuilder host(String hostName, int port) {
    return host(String.format("%s:%d", hostName, port));
  }

  public SQLDatabase build() {
    return new SQLDatabase(
            driver,
            SQLDatabase.Host.of(host),
            database,
            SQLDatabase.Credentials.of(
                    username,
                    password
            ),
            poolConfig
    );
  }


}

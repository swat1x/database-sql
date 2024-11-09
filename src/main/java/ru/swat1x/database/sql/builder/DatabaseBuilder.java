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
import ru.swat1x.database.sql.path.DatabasePath;
import ru.swat1x.database.sql.path.DedicatedDatabaseServerPath;
import ru.swat1x.database.sql.path.LocalFileDatabasePath;

@Getter
@Accessors(fluent = true, chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatabaseBuilder {

  @NotNull
  DatabasePath path;

  @NotNull
  @Setter
  String username;
  @Nullable
  @Setter
  String password;

  @NotNull
  @Setter
  PoolConfig poolConfig = PoolConfig.defaultConfig();

  public DatabaseBuilder path(@NotNull String host, @Nullable String database) {
    this.path = new DedicatedDatabaseServerPath(host, database);
    return this;
  }

  public DatabaseBuilder path(@NotNull String hostName) {
    return path(hostName, null);
  }

  public DatabaseBuilder path(@NotNull String hostName, int port) {
    return path(String.format("%s:%d", hostName, port));
  }

  public DatabaseBuilder filePath(@NotNull String filePath) {
    this.path = new LocalFileDatabasePath(filePath);
    return this;
  }

  public SQLDatabase build() {
    return new SQLDatabase(
            path,
            SQLDatabase.Credentials.of(
                    username,
                    password
            ),
            poolConfig
    );
  }


}

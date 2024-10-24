package ru.swat1x.database.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.swat1x.database.sql.executor.ExecutorSelector;
import ru.swat1x.database.sql.executor.query.AsyncQueryExecutor;
import ru.swat1x.database.sql.executor.query.SyncQueryExecutor;
import ru.swat1x.database.sql.executor.update.AsyncUpdateExecutor;
import ru.swat1x.database.sql.executor.update.SyncUpdateExecutor;
import ru.swat1x.database.sql.logger.DatabaseLogger;
import ru.swat1x.database.sql.logger.SoutDatabaseLogger;
import ru.swat1x.database.sql.moved.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SQLDatabase {

  @Setter
  private static DatabaseLogger logger = new SoutDatabaseLogger();

  HikariDataSource dataSource;
  ExecutorService asyncExecutor;

  /**
   * @param host        Host of database server
   * @param credentials Credentials for connection
   * @see ru.swat1x.database.sql.driver.Drivers Default drivers
   * @see Host Create host data object
   * @see Credentials#of(String, String) Create a credential object
   * @see Credentials#withNoPassword(String) Create credentials with no password
   */
  public SQLDatabase(
          @NotNull @NonNull Host host,
          @Nullable Credentials credentials
  ) {
    this(host, null, credentials, PoolConfig.defaultConfig());
  }

  /**
   * @param host        Host of database server
   * @param database    Database name for connection
   * @param credentials Credentials for connection
   * @see SQLDatabase#SQLDatabase(Host, Credentials)
   * @see ru.swat1x.database.sql.driver.Drivers Default drivers
   * @see Host          Create host data object
   * @see Credentials   Create a credential object
   * @see Credentials#withNoPassword(String) Create credentials with no password
   */
  public SQLDatabase(
          @NotNull @NonNull Host host,
          @Nullable String database,
          @Nullable Credentials credentials,
          @NotNull PoolConfig poolConfig
  ) {
    HikariConfig config = new HikariConfig();

    // Apply config
    poolConfig.applyConfig(
            host,
            database,
            credentials,
            config
    );
    // Apply optional driver parameters
    poolConfig.driver().setupToConfig(config);

    this.dataSource = new HikariDataSource(config);
    this.asyncExecutor = Executors.newFixedThreadPool(
            5,
            new ThreadFactoryBuilder()
                    .setUncaughtExceptionHandler((thread, throwable) -> {
                      logger.error("Exception: {}");
                    })
                    .setNameFormat("Database-Thread-%d")
                    .build()
    );
  }

  public ExecutorSelector<SyncQueryExecutor, AsyncQueryExecutor> query() {
    return new ExecutorSelector<>(
            () -> new SyncQueryExecutor(dataSource),
            () -> new AsyncQueryExecutor(asyncExecutor, new SyncQueryExecutor(dataSource))
    );
  }

  public ExecutorSelector<SyncUpdateExecutor, AsyncUpdateExecutor> update() {
    return new ExecutorSelector<>(
            () -> new SyncUpdateExecutor(dataSource),
            () -> new AsyncUpdateExecutor(asyncExecutor, new SyncUpdateExecutor(dataSource))
    );
  }

  public void disconnect() {
    dataSource.close();
  }

  @Getter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  public static class Credentials {

    String username;
    String password;

    public static Credentials of(String username, String password) {
      return new Credentials(username, password);
    }

    public static Credentials withNoPassword(String username) {
      return of(username, null);
    }

  }

  @Getter
  @Accessors(fluent = true)
  @Value(staticConstructor = "of")
  public static class Host {

    String hostname;

    public static Host of(String host, int port) {
      return new Host(host + ":" + port);
    }

  }


}

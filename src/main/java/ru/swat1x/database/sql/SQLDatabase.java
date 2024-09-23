package ru.swat1x.database.sql;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.swat1x.database.sql.executor.ExecutorSelector;
import ru.swat1x.database.sql.executor.query.AsyncQueryExecutor;
import ru.swat1x.database.sql.executor.query.SyncQueryExecutor;
import ru.swat1x.database.sql.executor.update.AsyncUpdateExecutor;
import ru.swat1x.database.sql.executor.update.SyncUpdateExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j(topic = "database")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SQLDatabase {

  HikariDataSource dataSource;
  ExecutorService asyncExecutor;

  /**
   * @param driver      Driver for database connection
   * @param host        Host of database server
   * @param credentials Credentials for connection
   * @see ru.swat1x.database.sql.driver.Drivers Default drivers
   * @see Host Create host data object
   * @see Credentials#of (String, String) Create credentials object
   * @see Credentials#withNoPassword(String) Create credentials with no password
   */
  public SQLDatabase(
          @NotNull @NonNull SQLDriver driver,
          @NotNull @NonNull Host host,
          @Nullable Credentials credentials
  ) {
    this(driver, host, null, credentials);
  }

  /**
   * @param driver      Driver for database connection
   * @param host        Host of database server
   * @param database    Database name for connection
   * @see SQLDatabase#SQLDatabase(SQLDriver, Host, Credentials)
   * @param credentials Credentials for connection
   * @see ru.swat1x.database.sql.driver.Drivers Default drivers
   * @see Host Create host data object
   * @see Credentials#of (String, String) Create a credential object
   * @see Credentials#withNoPassword(String) Create credentials with no password
   */
  public SQLDatabase(
          @NotNull @NonNull SQLDriver driver,
          @NotNull @NonNull Host host,
          @Nullable String database,
          @Nullable Credentials credentials
  ) {
    HikariConfig config = new HikariConfig();
    driver.setupToConfig(config);

    // Credentials
    if (credentials != null) {
      config.setUsername(credentials.getUsername());
      if (credentials.getPassword() != null) config.setPassword(credentials.getPassword());
    }

    // Connection settings
    config.setMaximumPoolSize(100);
    config.setConnectionTimeout(30000L);
    config.setIdleTimeout(600000L);
    config.setMaxLifetime(1800000L);
    config.addDataSourceProperty("cachePrepStmts", true);
    config.addDataSourceProperty("prepStmtCacheSize", 250);
    config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

    // Settings
    config.setJdbcUrl(driver.getUrl(host, database, credentials));

    this.dataSource = new HikariDataSource(config);
    this.asyncExecutor = Executors.newFixedThreadPool(
            5,
            new ThreadFactoryBuilder()
                    .setUncaughtExceptionHandler((thread, throwable) -> {
                      log.error("Exception:", throwable);
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

  @Value(staticConstructor = "of")
  public static class Credentials {

    String username;
    String password;

    public static Credentials withNoPassword(String username) {
      return of(username, null);
    }

  }

  @Value(staticConstructor = "of")
  public static class Host {

    String hostname;

    public static Host of(String host, int port) {
      return new Host(host + ":" + port);
    }

  }


}

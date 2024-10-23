package ru.swat1x.database.sql;

import com.zaxxer.hikari.HikariConfig;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

@Setter
@Builder
@Accessors(fluent = true, chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PoolConfig {

  public static PoolConfig defaultConfig() {
    return PoolConfig.builder()
            .parameter("useUnicode", "yes")
            .parameter("useSSL", "false")
            .parameter("characterEncoding", "UTF-8")
            .parameter("allowMultiQueries", "true")
            .parameter("autoReconnect", "true")
            .parameter("jdbcCompliantTruncation", "false")
            .build();
  }

  @Builder.Default
  String poolName = SQLDatabase.class.getName() + "-pool";

  SQLDriver driver;

  @Builder.Default
  Integer maximumPoolSize = 10;
  @Builder.Default
  Integer minimumIdle = 10;
  @Builder.Default
  Integer maximumLifetime = 1800000;
  @Builder.Default
  Integer keepaliveTime = 0;
  @Builder.Default
  Integer connectionTimeout = 5000;
  @Builder.Default
  Boolean autoCommit = true;

  @Singular("parameter")
  Map<String, String> urlParameters;

  @Singular("property")
  Map<String, String> datasourceProperty;

  public void applyConfig(@NotNull SQLDatabase.Host host,
                          @Nullable String database,
                          @Nullable SQLDatabase.Credentials credentials,
                          @NotNull HikariConfig config) {
    config.setPoolName(poolName);
    config.setDriverClassName(driver.driverClassName());

    if (credentials != null) {
      config.setUsername(credentials.username());
      if (credentials.password() != null) config.setPassword(credentials.password());
    }

    config.setMaximumPoolSize(maximumPoolSize);
    config.setMaxLifetime(minimumIdle);
    config.setMinimumIdle(maximumLifetime);
    config.setKeepaliveTime(keepaliveTime);
    config.setConnectionTimeout(connectionTimeout);
    config.setAutoCommit(autoCommit);

    datasourceProperty.forEach(config::addDataSourceProperty);

    config.setJdbcUrl(buildUrl(host, database));
  }

  private String buildUrl(@NotNull SQLDatabase.Host host,
                          @Nullable String database) {
    StringBuilder builder = new StringBuilder();
    builder.append(String.format(
            "jdbc:%s://%s/%s",
            driver.name(), host.hostname(),
            database == null ? "" : database
    ));

    if (!urlParameters.isEmpty()) {
      builder.append("?");
      builder.append(
              urlParameters.entrySet().stream()
                      .map(entry -> entry.getKey() + "=" + entry.getValue())
                      .collect(Collectors.joining("&"))
      );
    }

    return builder.toString();
  }

}

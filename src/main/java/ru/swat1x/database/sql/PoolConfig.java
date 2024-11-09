package ru.swat1x.database.sql;

import com.zaxxer.hikari.HikariConfig;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.swat1x.database.sql.driver.Drivers;
import ru.swat1x.database.sql.path.DatabasePath;

import java.util.Map;
import java.util.stream.Collectors;

@Setter
@Getter
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

  @Getter
  @Builder.Default
  SQLDriver driver = Drivers.MYSQL;

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

  public void applyConfig(@NotNull DatabasePath path,
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

    config.setJdbcUrl(buildUrl(path));
  }

  private String buildUrl(@NotNull DatabasePath path) {
    StringBuilder builder = new StringBuilder();
    builder.append(String.format(
            "jdbc:%s:%s",
            driver.name(), path.getPath()
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

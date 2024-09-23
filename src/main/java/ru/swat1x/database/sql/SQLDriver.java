package ru.swat1x.database.sql;

import com.zaxxer.hikari.HikariConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SQLDriver {

  String name();

  String driverClassName();

  default void setupToConfig(HikariConfig config) {
    config.setPoolName(name() + "-pool");
    config.setDriverClassName(driverClassName());
  }

  default String getUrl(@NotNull SQLDatabase.Host host, @Nullable String database, @Nullable SQLDatabase.Credentials credentials) {
    return String.format(
            "jdbc:%s://%s/%s" +
                    "?useUnicode=yes" +
                    "&useSSL=false" +
                    "&characterEncoding=UTF-8" +
                    "&allowMultiQueries=true" +
                    "&autoReconnect=true" +
                    "&jdbcCompliantTruncation=false",
            name(), host.getHostname(), database == null ? "" : database);
  }

}

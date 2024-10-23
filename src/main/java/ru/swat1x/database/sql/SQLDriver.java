package ru.swat1x.database.sql;

import com.zaxxer.hikari.HikariConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SQLDriver {

  String name();

  String driverClassName();

  default void setupToConfig(HikariConfig config) {
    // Ignore if dont need rewrite config
  }

}

package ru.swat1x.database.sql.executor;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;

public interface QueryResult {

  @NotNull ResultSet source();

  default void close() {
    try {
      source().close();
    } catch (SQLException e) {
      throw new RuntimeException("Can't close ResultSet", e);
    }
  }

  @SneakyThrows
  default boolean next() {
    return source().next();
  }

  default void forEach(Consumer<QueryResult> consumer) {
    while (next()) {
      consumer.accept(this);
    }
  }

  @SneakyThrows
  default Object getObject(String column) {
    return source().getObject(column);
  }

  @SneakyThrows
  default String getString(String column) {
    return source().getString(column);
  }

  @SneakyThrows
  default int getInt(String column) {
    return source().getInt(column);
  }

  @SneakyThrows
  default double getDouble(String column) {
    return source().getDouble(column);
  }

  @SneakyThrows
  default long getLong(String column) {
    return source().getLong(column);
  }

  @SneakyThrows
  default UUID getUUID(String column) {
    return UUID.fromString(getString(column));
  }

  @SneakyThrows
  default Date getTimestamp(String column) {
    return source().getTimestamp(column);
  }

}

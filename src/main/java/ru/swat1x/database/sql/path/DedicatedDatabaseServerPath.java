package ru.swat1x.database.sql.path;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DedicatedDatabaseServerPath implements DatabasePath {

  @NotNull
  String host;
  @Nullable
  String database;

  @Override
  public String getPath() {
    return String.format("//%s/%s", host, database == null ? "" : database);
  }

}

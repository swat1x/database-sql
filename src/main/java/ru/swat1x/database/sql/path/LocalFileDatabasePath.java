package ru.swat1x.database.sql.path;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LocalFileDatabasePath implements DatabasePath {

  @NotNull
  String filename;

  @Override
  public String getPath() {
    return String.format("./%s/", filename);
  }

}

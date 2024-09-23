package ru.swat1x.database.sql.driver;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.swat1x.database.sql.SQLDriver;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BaseSQLDriver implements SQLDriver {

  String name;
  String driverClassName;

}

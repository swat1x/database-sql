package ru.swat1x.database.sql.logger;

public interface DatabaseLogger {

  void info(String text, Object... args);

  void warn(String text, Object... args);

  void error(String text, Object... args);

}

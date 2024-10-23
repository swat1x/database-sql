package ru.swat1x.database.sql.logger;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.Logger;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class Log4jDatabaseLogger implements DatabaseLogger {

  Logger logger;

  @Override
  public void info(String text, Object... args) {
    logger.info(text, args);
  }

  @Override
  public void warn(String text, Object... args) {
    logger.warn(text, args);
  }

  @Override
  public void error(String text, Object... args) {
    logger.error(text, args);
  }

  @Override
  public void error(String text, Throwable throwable) {
    logger.error(text, throwable);
  }
}

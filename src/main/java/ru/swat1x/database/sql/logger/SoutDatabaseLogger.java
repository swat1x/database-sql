package ru.swat1x.database.sql.logger;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SoutDatabaseLogger implements DatabaseLogger {

  @Override
  public void info(String text, Object... args) {
    System.out.print("[INFO] ");
    System.out.println(formatString(text, args));
  }

  @Override
  public void warn(String text, Object... args) {
    System.out.print("[WARN] ");
    System.out.println(formatString(text, args));
  }

  @Override
  public void error(String text, Object... args) {
    System.out.print("[ERROR] ");
    System.out.println(formatString(text, args));
  }

    @Override
    public void error(String text, Throwable throwable) {
        error(text);
        throwable.printStackTrace(System.out);
    }

    private static String formatString(String template, Object... objects) {
    if (template == null || objects == null) {
      throw new IllegalArgumentException("Template or objects list cannot be null");
    } else if (template.isEmpty()) return template;


    List<String> parts = new ArrayList<>();
    int start = 0, index = 0, end;
    while ((end = template.indexOf("{}", start)) != -1) {
      parts.add(template.substring(start, end));
      if (index < objects.length) {
        Object object = objects[index];
        parts.add(object.toString());
        index++;
      } else break;
      start = end + 2;
    }
    parts.add(template.substring(start));

    return String.join("", parts);
  }

}

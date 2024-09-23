package ru.swat1x.database.sql.executor.update;

import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.swat1x.database.sql.exception.SQLDatabaseException;
import ru.swat1x.database.sql.executor.RequestExecutor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SyncUpdateExecutor implements RequestExecutor<Integer> {

  HikariDataSource dataSource;

  @Override
  public @NotNull Integer execute(@NotNull String query) {
    return execute(query, new Object[]{});
  }

  @Override
  public @NotNull Integer execute(@NotNull String query, @NotNull Object... args) {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {
      for (int i = 0; i < args.length; i++) {
        statement.setObject(i + 1, args[i]);
      }
      return statement.executeUpdate();
    } catch (SQLException e) {
      throw new SQLDatabaseException(String.format("Can't execute request '%s'", query), e);
    }
  }

}

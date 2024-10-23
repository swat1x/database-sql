package ru.swat1x.database.sql.executor.query;

import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import ru.swat1x.database.sql.exception.SQLDatabaseException;
import ru.swat1x.database.sql.executor.QueryResult;
import ru.swat1x.database.sql.executor.processor.ValueQueryProcessor;
import ru.swat1x.database.sql.executor.processor.VoidQueryProcessor;
import ru.swat1x.database.sql.executor.result.BaseQueryResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j(topic = "SyncExecutor")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SyncQueryExecutor implements QueryExecutor<QueryResult> {

  HikariDataSource dataSource;

  @Override
  public @NotNull QueryResult execute(@Language("sql") @NotNull String query) {
    return execute(query, new Object[]{});
  }

  @Override
  public @NotNull QueryResult execute(@Language("sql") @NotNull String query,
                                      @NotNull Object... args) {
    try(Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      for (int i = 0; i < args.length; i++) {
        statement.setObject(i + 1, args[i]);
      }
      ResultSet resultSet = statement.executeQuery();
      return new BaseQueryResult(resultSet);
    } catch (SQLException e) {
      throw new SQLDatabaseException(String.format("Can't return queried result. Args %s",
              Arrays.stream(args).map(Object::toString).collect(Collectors.joining(", "))
      ), e);
    }
  }

  @Override
  public void execute(@Language("sql") @NotNull String query,
                      @NotNull VoidQueryProcessor processor) {
    QueryResult result = execute(query);
    processor.process(result);
    result.close();
  }

  public <V> @NotNull V execute(@Language("sql") @NotNull String query,
                                @NotNull ValueQueryProcessor<V> processor) {
    QueryResult result = execute(query);
    V value = processor.process(result);
    result.close();
    return value;
  }

  @Override
  public void execute(@Language("sql") @NotNull String query,
                      @NotNull VoidQueryProcessor processor,
                      @NotNull Object... args) {
    QueryResult result = execute(query, args);
    processor.process(result);
    result.close();
  }

  public <V> @NotNull V execute(@Language("sql") @NotNull String query,
                                @NotNull ValueQueryProcessor<V> processor,
                                @NotNull Object... args) {
    QueryResult result = execute(query, args);
    V value = processor.process(result);
    result.close();
    return value;
  }

}

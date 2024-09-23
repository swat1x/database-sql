package ru.swat1x.database.sql.executor.query;

import ru.swat1x.database.sql.executor.RequestExecutor;
import ru.swat1x.database.sql.executor.processor.VoidQueryProcessor;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

public interface QueryExecutor<T> extends RequestExecutor<T> {

  void execute(@Language("sql") @NotNull String query,
               @NotNull VoidQueryProcessor processor);

  void execute(@Language("sql") @NotNull String query,
               @NotNull VoidQueryProcessor processor,
               @NotNull Object... args);

}

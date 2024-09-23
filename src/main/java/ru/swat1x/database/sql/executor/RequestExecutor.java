package ru.swat1x.database.sql.executor;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

public interface RequestExecutor<T> {

  @NotNull T execute(@Language("sql") @NotNull String query);

  @NotNull T execute(@Language("sql") @NotNull String query, @NotNull Object... args);

}

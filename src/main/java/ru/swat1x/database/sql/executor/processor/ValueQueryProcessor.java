package ru.swat1x.database.sql.executor.processor;

import ru.swat1x.database.sql.executor.QueryResult;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ValueQueryProcessor<T> {

  @NotNull T process(QueryResult result);

}

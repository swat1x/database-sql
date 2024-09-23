package ru.swat1x.database.sql.executor.processor;

import ru.swat1x.database.sql.executor.QueryResult;

@FunctionalInterface
public interface VoidQueryProcessor {

  void process(QueryResult result);

}

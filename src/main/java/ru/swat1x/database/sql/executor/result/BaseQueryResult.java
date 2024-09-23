package ru.swat1x.database.sql.executor.result;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.swat1x.database.sql.executor.QueryResult;

import java.sql.ResultSet;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BaseQueryResult implements QueryResult {

  ResultSet source;

}

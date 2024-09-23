package ru.swat1x.database.sql.executor.query;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.swat1x.database.sql.executor.QueryResult;
import ru.swat1x.database.sql.executor.processor.ValueQueryProcessor;
import ru.swat1x.database.sql.executor.processor.VoidQueryProcessor;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AsyncQueryExecutor implements QueryExecutor<CompletableFuture<QueryResult>> {

  ExecutorService asyncExecutor;
  SyncQueryExecutor syncExecutor;

  @Override
  public @NotNull CompletableFuture<QueryResult> execute(@Language("sql") @NotNull String query) {
    return completeFuture(() -> syncExecutor.execute(query));
  }

  @Override
  public @NotNull CompletableFuture<QueryResult> execute(@Language("sql") @NotNull String query, @NotNull Object... args) {
    return completeFuture(() -> syncExecutor.execute(query, args));
  }

  @Override
  public void execute(@Language("sql") @NotNull String query, @NotNull VoidQueryProcessor processor) {
    completeFuture(() -> syncExecutor.execute(query)).thenAccept(processor::process);
  }

  public <V> @NotNull CompletableFuture<V> execute(@Language("sql") @NotNull String query, @NotNull ValueQueryProcessor<V> processor) {
    return completeFuture(() -> syncExecutor.execute(query)).thenApply(processor::process);
  }

  @Override
  public void execute(@Language("sql") @NotNull String query, @NotNull VoidQueryProcessor processor, @NotNull Object... args) {
    completeFuture(() -> syncExecutor.execute(query, args)).thenAccept(processor::process);
  }

  public <V> @NotNull CompletableFuture<V> execute(@Language("sql") @NotNull String query, @NotNull ValueQueryProcessor<V> processor, @NotNull Object... args) {
    return completeFuture(() -> syncExecutor.execute(query, args)).thenApply(processor::process);
  }

  private CompletableFuture<QueryResult> completeFuture(Supplier<QueryResult> resultSupplier) {
    CompletableFuture<QueryResult> future = new CompletableFuture<>();
    asyncExecutor.execute(() -> future.complete(resultSupplier.get()));
    return future;
  }

}

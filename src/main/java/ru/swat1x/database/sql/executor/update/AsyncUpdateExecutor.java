package ru.swat1x.database.sql.executor.update;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.swat1x.database.sql.executor.RequestExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AsyncUpdateExecutor implements RequestExecutor<CompletableFuture<Integer>> {

  ExecutorService asyncExecutor;
  SyncUpdateExecutor syncExecutor;

  @Override
  public @NotNull CompletableFuture<Integer> execute(@NotNull String query) {
    return completeFuture(() -> syncExecutor.execute(query));
  }

  @Override
  public @NotNull CompletableFuture<Integer> execute(@NotNull String query, @NotNull Object... args) {
    return completeFuture(() -> syncExecutor.execute(query, args));
  }

  private CompletableFuture<Integer> completeFuture(Supplier<Integer> resultSupplier) {
    CompletableFuture<Integer> future = new CompletableFuture<>();
    asyncExecutor.execute(() -> future.complete(resultSupplier.get()));
    return future;
  }

}

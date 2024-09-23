package ru.swat1x.database.sql.executor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ExecutorSelector<S, A> {

  Supplier<S> syncSupplier;
  Supplier<A> asyncSupplier;

  public S sync() {
    return syncSupplier.get();
  }

  public A async() {
    return asyncSupplier.get();
  }


}

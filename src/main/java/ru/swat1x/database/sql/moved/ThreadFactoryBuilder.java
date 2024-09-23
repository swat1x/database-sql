package ru.swat1x.database.sql.moved;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.WARNING;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import javax.annotation.CheckForNull;

/**
 * A ThreadFactory builder, providing any combination of these features:
 *
 * <ul>
 *   <li>whether threads should be marked as {@linkplain Thread#setDaemon daemon} threads
 *   <li>a {@linkplain ThreadFactoryBuilder#setNameFormat naming format}
 *   <li>a {@linkplain Thread#setPriority thread priority}
 *   <li>an {@linkplain Thread#setUncaughtExceptionHandler uncaught exception handler}
 *   <li>a {@linkplain ThreadFactory#newThread backing thread factory}
 * </ul>
 *
 * <p>If no backing thread factory is provided, a default backing thread factory is used as if by
 * calling {@code setThreadFactory(}{@link Executors#defaultThreadFactory()}{@code )}.
 *
 * @author Kurt Alfred Kluever
 * @since 4.0
 */
@J2ktIncompatible
@GwtIncompatible
public final class ThreadFactoryBuilder {

  // moved start!

  public static void checkArgument(
          boolean expression, String errorMessageTemplate, int p1, int p2) {
    if (!expression) {
      throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
    }
  }

  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference) {
    if (reference == null) {
      throw new NullPointerException();
    }
    return reference;
  }

  public static String lenientFormat(
          @CheckForNull String template, @CheckForNull @Nullable Object... args) {
    template = String.valueOf(template); // null -> "null"

    if (args == null) {
      args = new Object[] {"(Object[])null"};
    } else {
      for (int i = 0; i < args.length; i++) {
        args[i] = lenientToString(args[i]);
      }
    }

    // start substituting the arguments into the '%s' placeholders
    StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
    int templateStart = 0;
    int i = 0;
    while (i < args.length) {
      int placeholderStart = template.indexOf("%s", templateStart);
      if (placeholderStart == -1) {
        break;
      }
      builder.append(template, templateStart, placeholderStart);
      builder.append(args[i++]);
      templateStart = placeholderStart + 2;
    }
    builder.append(template, templateStart, template.length());

    // if we run out of placeholders, append the extra args in square braces
    if (i < args.length) {
      builder.append(" [");
      builder.append(args[i++]);
      while (i < args.length) {
        builder.append(", ");
        builder.append(args[i++]);
      }
      builder.append(']');
    }

    return builder.toString();
  }

  private static String lenientToString(@CheckForNull Object o) {
    if (o == null) {
      return "null";
    }
    try {
      return o.toString();
    } catch (Exception e) {
      // Default toString() behavior - see Object.toString()
      String objectToString =
              o.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(o));
      // Logger is created inline with fixed name to avoid forcing Proguard to create another class.
      Logger.getLogger("com.google.common.base.Strings")
              .log(WARNING, "Exception during lenientFormat for " + objectToString, e);
      return "<" + objectToString + " threw " + e.getClass().getName() + ">";
    }
  }


  // moved end!




  @CheckForNull private String nameFormat = null;
  @CheckForNull private Boolean daemon = null;
  @CheckForNull private Integer priority = null;
  @CheckForNull private UncaughtExceptionHandler uncaughtExceptionHandler = null;
  @CheckForNull private ThreadFactory backingThreadFactory = null;

  /** Creates a new {@link ThreadFactory} builder. */
  public ThreadFactoryBuilder() {}

  /**
   * Sets the naming format to use when naming threads ({@link Thread#setName}) which are created
   * with this ThreadFactory.
   *
   * @param nameFormat a {@link String#format(String, Object...)}-compatible format String, to which
   *     a unique integer (0, 1, etc.) will be supplied as the single parameter. This integer will
   *     be unique to the built instance of the ThreadFactory and will be assigned sequentially. For
   *     example, {@code "rpc-pool-%d"} will generate thread names like {@code "rpc-pool-0"}, {@code
   *     "rpc-pool-1"}, {@code "rpc-pool-2"}, etc.
   * @return this for the builder pattern
   */
  @CanIgnoreReturnValue
  public ThreadFactoryBuilder setNameFormat(String nameFormat) {
    String unused = format(nameFormat, 0); // fail fast if the format is bad or null
    this.nameFormat = nameFormat;
    return this;
  }

  /**
   * Sets daemon or not for new threads created with this ThreadFactory.
   *
   * @param daemon whether or not new Threads created with this ThreadFactory will be daemon threads
   * @return this for the builder pattern
   */
  @CanIgnoreReturnValue
  public ThreadFactoryBuilder setDaemon(boolean daemon) {
    this.daemon = daemon;
    return this;
  }

  /**
   * Sets the priority for new threads created with this ThreadFactory.
   *
   * <p><b>Warning:</b> relying on the thread scheduler is <a
   * href="http://errorprone.info/bugpattern/ThreadPriorityCheck">discouraged</a>.
   *
   * @param priority the priority for new Threads created with this ThreadFactory
   * @return this for the builder pattern
   */
  @CanIgnoreReturnValue
  public ThreadFactoryBuilder setPriority(int priority) {
    // Thread#setPriority() already checks for validity. These error messages
    // are nicer though and will fail-fast.
    checkArgument(
        priority >= Thread.MIN_PRIORITY,
        "Thread priority (%s) must be >= %s",
        priority,
        Thread.MIN_PRIORITY);
    checkArgument(
        priority <= Thread.MAX_PRIORITY,
        "Thread priority (%s) must be <= %s",
        priority,
        Thread.MAX_PRIORITY);
    this.priority = priority;
    return this;
  }

  /**
   * Sets the {@link UncaughtExceptionHandler} for new threads created with this ThreadFactory.
   *
   * @param uncaughtExceptionHandler the uncaught exception handler for new Threads created with
   *     this ThreadFactory
   * @return this for the builder pattern
   */
  @CanIgnoreReturnValue
  public ThreadFactoryBuilder setUncaughtExceptionHandler(
      UncaughtExceptionHandler uncaughtExceptionHandler) {
    this.uncaughtExceptionHandler = checkNotNull(uncaughtExceptionHandler);
    return this;
  }

  /**
   * Sets the backing {@link ThreadFactory} for new threads created with this ThreadFactory. Threads
   * will be created by invoking #newThread(Runnable) on this backing {@link ThreadFactory}.
   *
   * @param backingThreadFactory the backing {@link ThreadFactory} which will be delegated to during
   *     thread creation.
   * @return this for the builder pattern
   */
  @CanIgnoreReturnValue
  public ThreadFactoryBuilder setThreadFactory(ThreadFactory backingThreadFactory) {
    this.backingThreadFactory = checkNotNull(backingThreadFactory);
    return this;
  }

  /**
   * Returns a new thread factory using the options supplied during the building process. After
   * building, it is still possible to change the options used to build the ThreadFactory and/or
   * build again. State is not shared amongst built instances.
   *
   * @return the fully constructed {@link ThreadFactory}
   */
  public ThreadFactory build() {
    return doBuild(this);
  }

  // Split out so that the anonymous ThreadFactory can't contain a reference back to the builder.
  // At least, I assume that's why. TODO(cpovirk): Check, and maybe add a test for this.
  private static ThreadFactory doBuild(ThreadFactoryBuilder builder) {
    String nameFormat = builder.nameFormat;
    Boolean daemon = builder.daemon;
    Integer priority = builder.priority;
    UncaughtExceptionHandler uncaughtExceptionHandler = builder.uncaughtExceptionHandler;
    ThreadFactory backingThreadFactory =
        (builder.backingThreadFactory != null)
            ? builder.backingThreadFactory
            : Executors.defaultThreadFactory();
    AtomicLong count = (nameFormat != null) ? new AtomicLong(0) : null;
    return new ThreadFactory() {
      @Override
      public Thread newThread(Runnable runnable) {
        Thread thread = backingThreadFactory.newThread(runnable);
        // TODO(b/139735208): Figure out what to do when the factory returns null.
        requireNonNull(thread);
        if (nameFormat != null) {
          // requireNonNull is safe because we create `count` if (and only if) we have a nameFormat.
          thread.setName(format(nameFormat, requireNonNull(count).getAndIncrement()));
        }
        if (daemon != null) {
          thread.setDaemon(daemon);
        }
        if (priority != null) {
          thread.setPriority(priority);
        }
        if (uncaughtExceptionHandler != null) {
          thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        }
        return thread;
      }
    };
  }

  private static String format(String format, Object... args) {
    return String.format(Locale.ROOT, format, args);
  }
}

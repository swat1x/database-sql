package ru.swat1x.database.sql.moved;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.WARNING;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public final class ThreadFactoryBuilder {

  // moved start!

  public static void checkArgument(boolean expression, String errorMessageTemplate, int p1, int p2) {
    if (!expression) {
      throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1, p2));
    }
  }


  public static <T> T checkNotNull(T reference) {
    if (reference == null) {
      throw new NullPointerException();
    }
    return reference;
  }

  public static String lenientFormat(String template, Object... args) {
    template = String.valueOf(template); // null -> "null"

    if (args == null) {
      args = new Object[]{"(Object[])null"};
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

  private static String lenientToString(Object o) {
    if (o == null) {
      return "null";
    }
    try {
      return o.toString();
    } catch (Exception e) {
      // Default toString() behavior - see Object.toString()
      String objectToString = o.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(o));
      // Logger is created inline with fixed name to avoid forcing Proguard to create another class.
//      Logger.getLogger("com.google.common.base.Strings").log(WARNING, "Exception during lenientFormat for " + objectToString, e);
      return "<" + objectToString + " threw " + e.getClass().getName() + ">";
    }
  }


  // moved end!


  private String nameFormat = null;
  private Boolean daemon = null;
  private Integer priority = null;
  private UncaughtExceptionHandler uncaughtExceptionHandler = null;
  private ThreadFactory backingThreadFactory = null;

  public ThreadFactoryBuilder() {
  }

  public ThreadFactoryBuilder setNameFormat(String nameFormat) {
    String unused = format(nameFormat, 0); // fail fast if the format is bad or null
    this.nameFormat = nameFormat;
    return this;
  }

  public ThreadFactoryBuilder setDaemon(boolean daemon) {
    this.daemon = daemon;
    return this;
  }

  public ThreadFactoryBuilder setPriority(int priority) {
    // Thread#setPriority() already checks for validity. These error messages
    // are nicer though and will fail-fast.
    checkArgument(priority >= Thread.MIN_PRIORITY, "Thread priority (%s) must be >= %s", priority, Thread.MIN_PRIORITY);
    checkArgument(priority <= Thread.MAX_PRIORITY, "Thread priority (%s) must be <= %s", priority, Thread.MAX_PRIORITY);
    this.priority = priority;
    return this;
  }

  public ThreadFactoryBuilder setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler) {
    this.uncaughtExceptionHandler = checkNotNull(uncaughtExceptionHandler);
    return this;
  }

  public ThreadFactoryBuilder setThreadFactory(ThreadFactory backingThreadFactory) {
    this.backingThreadFactory = checkNotNull(backingThreadFactory);
    return this;
  }

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
    ThreadFactory backingThreadFactory = (builder.backingThreadFactory != null) ? builder.backingThreadFactory : Executors.defaultThreadFactory();
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

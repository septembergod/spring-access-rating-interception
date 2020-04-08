package org.zspace.common.security.rating.config.util;


import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.Uninterruptibles;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * 访问频率限制
 */
public abstract class RateLimiter {

  public static RateLimiter create(double permitsPerSecond) {
    return create(SleepingStopwatch.createFromSystemTimer(), permitsPerSecond);
  }

  static RateLimiter create(SleepingStopwatch stopwatch, double permitsPerSecond) {
      RateLimiter rateLimiter = new SmoothRateLimiter.SmoothBursty(stopwatch, 1.0 /* maxBurstSeconds */, permitsPerSecond);
    rateLimiter.setRate(permitsPerSecond);
    return rateLimiter;
  }

  private final SleepingStopwatch stopwatch;

  private volatile Object mutexDoNotUseDirectly;

  private Object mutex() {
    Object mutex = mutexDoNotUseDirectly;
    if (mutex == null) {
      synchronized (this) {
        mutex = mutexDoNotUseDirectly;
        if (mutex == null) {
          mutexDoNotUseDirectly = mutex = new Object();
        }
      }
    }
    return mutex;
  }

  RateLimiter(SleepingStopwatch stopwatch) {
    this.stopwatch = checkNotNull(stopwatch);
  }

  public final void setRate(double permitsPerSecond) {
    checkArgument(
        permitsPerSecond > 0.0 && !Double.isNaN(permitsPerSecond), "rate must be positive");
    synchronized (mutex()) {
      doSetRate(permitsPerSecond, stopwatch.readMicros());
    }
  }

  abstract void doSetRate(double permitsPerSecond, long nowMicros);

  public final double getRate() {
    synchronized (mutex()) {
      return doGetRate();
    }
  }

  abstract double doGetRate();

  public double acquire() {
    return acquire(1);
  }

  public double acquire(int permits) {
    long microsToWait = reserve(permits);
    return 1.0 * microsToWait / SECONDS.toMicros(1L);
  }

  final long reserve(int permits) {
    checkPermits(permits);
    synchronized (mutex()) {
      return reserveAndGetWaitLength(permits, stopwatch.readMicros());
    }
  }

  /** 已修改需要的等待的时间 此处不需要在进行比较，直接返回 */
  final long reserveAndGetWaitLength(int permits, long nowMicros) {
    //long momentAvailable = reserveEarliestAvailable(permits, nowMicros);
    //return max(momentAvailable - nowMicros, 0);
    return reserveEarliestAvailable(permits, nowMicros);
  }

  abstract long queryEarliestAvailable(long nowMicros);

  abstract long reserveEarliestAvailable(int permits, long nowMicros);

  @Override
  public String toString() {
    return String.format("RateLimiter[stableRate=%3.1fqps]", getRate());
  }

  abstract static class SleepingStopwatch {

    abstract long readMicros();

    abstract void sleepMicrosUninterruptibly(long micros);

    static final SleepingStopwatch createFromSystemTimer() {
      return new SleepingStopwatch() {
        final Stopwatch stopwatch = Stopwatch.createStarted();

        @Override
        long readMicros() {
          return stopwatch.elapsed(MICROSECONDS);
        }

        @Override
        void sleepMicrosUninterruptibly(long micros) {
          if (micros > 0) {
            Uninterruptibles.sleepUninterruptibly(micros, MICROSECONDS);
          }
        }
      };
    }
  }

  private static int checkPermits(int permits) {
    checkArgument(permits > 0, "Requested permits (%s) must be positive", permits);
    return permits;
  }
}


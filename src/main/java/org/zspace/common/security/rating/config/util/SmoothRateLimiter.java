package org.zspace.common.security.rating.config.util;


import static java.lang.Math.min;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 对于guava RateLimiter的改版
 */
abstract class SmoothRateLimiter extends RateLimiter {

  /**
   * “突发”速率限制器
   */
  static final class SmoothBursty extends SmoothRateLimiter {

    final double maxBurstSeconds;

    SmoothBursty(SleepingStopwatch stopwatch, double maxBurstSeconds) {
      super(stopwatch);
      this.maxBurstSeconds = maxBurstSeconds;
    }

    SmoothBursty(SleepingStopwatch stopwatch, double maxBurstSeconds, double storedPermits) {
        super(stopwatch, storedPermits);
        //对计数进行初始化，防止第一次过快，而导致频繁访问的问题
        this.maxBurstSeconds = maxBurstSeconds;
        this.maxPermits = this.maxBurstSeconds * storedPermits;
      }
    
    @Override
    void doSetRate(double permitsPerSecond, double stableIntervalMicros) {
      double oldMaxPermits = this.maxPermits;
      maxPermits = maxBurstSeconds * permitsPerSecond;
      if (oldMaxPermits == Double.POSITIVE_INFINITY) {
        // if we don't special-case this, we would get storedPermits == NaN, below
        storedPermits = maxPermits;
      } else {
        storedPermits = (oldMaxPermits == 0.0)
            ? 0.0 // initial state
            : storedPermits * maxPermits / oldMaxPermits;
      }
    }
  
    @Override
    long storedPermitsToWaitTime(double storedPermits, double permitsToTake) {
      return 0L;
    }
  }

  /**
   * 当前存储的令牌
   */
  double storedPermits;

  /**
   * 允许的最大令牌数
   */
  double maxPermits;

  /**
   * 令牌生产间隔 例如：5qps 则为200ms
   */
  double stableIntervalMicros;

  /**
   * 下一次允许获取令牌的时间： 无论获取多少个令牌
   */
  private long nextFreeTicketMicros = 0L; // could be either in the past or future

  private SmoothRateLimiter(SleepingStopwatch stopwatch) {
    super(stopwatch);
  }
  
  private SmoothRateLimiter(SleepingStopwatch stopwatch, double storedPermits) {
      super(stopwatch);
      this.storedPermits = storedPermits;
    }

  @Override
  final void doSetRate(double permitsPerSecond, long nowMicros) {
    resync(nowMicros);
    double stableIntervalMicros = SECONDS.toMicros(1L) / permitsPerSecond;
    this.stableIntervalMicros = stableIntervalMicros;
    doSetRate(permitsPerSecond, stableIntervalMicros);
  }

  abstract void doSetRate(double permitsPerSecond, double stableIntervalMicros);

  @Override
  final double doGetRate() {
    return SECONDS.toMicros(1L) / stableIntervalMicros;
  }

  @Override
  final long queryEarliestAvailable(long nowMicros) {
    return nextFreeTicketMicros;
  }

  /**
   * 代码修改：修改返回 要获取的令牌是否需要等待时间
   * 返回：需要等待的时间  ：不需要等待返回0,负责返回需要的等待的时间
   * 不支持预消费的情况
   */
  @Override
  final long reserveEarliestAvailable(int requiredPermits, long nowMicros) {
    resync(nowMicros);
    double storedPermitsToSpend = min(requiredPermits, this.storedPermits);
    double freshPermits = requiredPermits - storedPermitsToSpend;
    long waitMicros = storedPermitsToWaitTime(this.storedPermits, storedPermitsToSpend)
        + (long) (freshPermits * stableIntervalMicros);
    
    if(waitMicros == 0l) {
        this.storedPermits -= requiredPermits;
    }
    
    return waitMicros;
  }

  /**
   * 计算需要wait 的时间
   */
  abstract long storedPermitsToWaitTime(double storedPermits, double permitsToTake);

  private void resync(long nowMicros) {
    // if nextFreeTicket is in the past, resync to now
    if (nowMicros > nextFreeTicketMicros) {
      /**
       * 可能存在问题 ：有可能每次都是小于最小间隔时间，这样就可能刷新nextFreeTicketMicros 而 storedPermits不变，这样是可以的，就是为了防刷
       */
      storedPermits = min(maxPermits,
          storedPermits + (nowMicros - nextFreeTicketMicros) / stableIntervalMicros);
      nextFreeTicketMicros = nowMicros;
    }
  }
}


package io.inoa.test;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.function.Function;
import org.slf4j.Logger;

/**
 * Utility for waits.
 *
 * <p>Alternatives:
 *
 * <ul>
 *   <li><a>https://github.com/rnorth/duct-tape</a> - archived project
 *   <li><a>https://github.com/awaitility/awaitility</a> - has junit dependency and is unmaintained
 *       since 3 years
 * </ul>
 *
 * @author stephan.schnabel@grayc.de
 */
public class Await {

  public static Await await(Logger log, String text) {
    return new Await(log, text);
  }

  private final Logger log;
  private final String text;
  private Duration timeout;
  private Duration interval;

  private Await(Logger log, String text) {
    this.log = log;
    this.text = text;
    this.timeout = Duration.ofSeconds(1);
    this.interval = Duration.ofMillis(10);
  }

  public Await interval(Duration newInterval) {
    this.interval = newInterval;
    return this;
  }

  public Await timeout(Duration newTimeout) {
    this.timeout = newTimeout;
    return this;
  }

  public void until(Callable<Boolean> check) {
    until(check, Function.identity());
  }

  public <V> V until(Callable<V> supplier, Function<V, Boolean> check) {

    Throwable lastThrowable = null;
    var started = Instant.now().plus(timeout);
    while (Instant.now().isBefore(started)) {
      try {
        var value = supplier.call();
        if (check.apply(value)) {
          log.debug(text);
          return value;
        }
        lastThrowable = null;
      } catch (Throwable e) {
        lastThrowable = e;
        log.debug("Await " + text + " failed with exception " + e.getMessage());
      }
      wait(interval);
    }

    if (lastThrowable != null) {
      log.error("Await " + text + " had exception while waiting", lastThrowable);
    }

    fail(text + " did not complete in " + timeout.toSeconds() + " seconds");
    return null;
  }

  private void wait(Duration duration) {
    try {
      Thread.sleep(duration.toMillis());
    } catch (InterruptedException e) {
      fail(text + " interrupted");
    }
  }
}

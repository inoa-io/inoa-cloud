package io.inoa;

import io.micronaut.context.annotation.Factory;
import io.micronaut.runtime.Micronaut;
import jakarta.inject.Singleton;
import java.time.Clock;

@Factory
public class Application {

  public static void main(String[] args) {
    Micronaut.build(args)
        .banner(false)
        .mainClass(Application.class)
        .environments("exporter", "translator", "app", "mqtt")
        .start();
  }

  @Singleton
  Clock clock() {
    return Clock.systemUTC();
  }
}

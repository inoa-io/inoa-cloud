package io.inoa.measurement.translator;

import io.micronaut.context.annotation.Context;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for telemetry translator.
 *
 * @author stephan.schnabel@grayc.de
 */
@Context
public class TranslatorFactory {

  private static final Logger log = LoggerFactory.getLogger(TranslatorFactory.class);

  @PostConstruct
  void log() {
    log.info("Starting controller: {}", getClass().getPackage().getName());
  }
}

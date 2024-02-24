package io.inoa.controller.translator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.annotation.Context;
import jakarta.annotation.PostConstruct;

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

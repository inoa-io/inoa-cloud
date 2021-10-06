package io.inoa.fleet.registry.test;

import io.inoa.fleet.registry.infrastructure.PrometheusClient;
import io.micronaut.http.client.annotation.Client;

/**
 * Prometheus clients for translator.
 *
 * @author Stephan Schnabel
 */
@Client("inoa-translator-management")
public interface InoaTranslatorPrometheusClient extends PrometheusClient {

	default long scrapMessagesSuccess() {
		return scrapCounter("translate_messages_success");
	}
}

package io.inoa.fleet.registry.test.prometheus;

import io.micronaut.http.client.annotation.Client;

/**
 * Prometheus clients for translator.
 *
 * @author Stephan Schnabel
 */
@Client("inoa-translator-management")
public interface TranslatorPrometheusClient extends PrometheusClient {

	default long scrapMessagesSuccess() {
		return scrapCounter("translate_messages_success");
	}
}

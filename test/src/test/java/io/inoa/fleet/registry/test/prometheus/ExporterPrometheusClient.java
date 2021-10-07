package io.inoa.fleet.registry.test.prometheus;

import io.micronaut.http.client.annotation.Client;

/**
 * Prometheus clients for exporter.
 *
 * @author Stephan Schnabel
 */
@Client("inoa-exporter-management")
public interface ExporterPrometheusClient extends PrometheusClient {

	default long scrapKafkaRecords() {
		return scrapCounter("kafka_consumer_records_consumed_total_records");
	}
}

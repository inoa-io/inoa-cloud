package io.inoa.fleet.registry.test;

import io.inoa.fleet.registry.infrastructure.PrometheusClient;
import io.micronaut.http.client.annotation.Client;

/**
 * Prometheus clients for kafka backup.
 *
 * @author Stephan Schnabel
 */
@Client("kafka-backup-management")
public interface KafkaBackupPrometheusClient extends PrometheusClient {

	default long scrapMessages() {
		return scrapCounter("backup_messages");
	}

	default long scrapMessages(String topic) {
		return scrapCounter("backup_messages", "topic", topic);
	}
}

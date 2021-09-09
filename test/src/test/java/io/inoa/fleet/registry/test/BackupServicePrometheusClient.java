package io.inoa.fleet.registry.test;

import io.inoa.fleet.registry.infrastructure.PrometheusClient;
import io.micronaut.http.client.annotation.Client;

/**
 * Prometheus clients for backup service.
 *
 * @author Stephan Schnabel
 */
@Client("backup-service-management")
public interface BackupServicePrometheusClient extends PrometheusClient {

	default long scrapMessages() {
		return scrapCounter("backup_messages");
	}

	default long scrapMessages(String topic) {
		return scrapCounter("backup_messages", "topic", topic);
	}
}

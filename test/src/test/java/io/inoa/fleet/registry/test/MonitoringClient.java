package io.inoa.fleet.registry.test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Singleton;

import org.awaitility.Awaitility;

import io.inoa.fleet.registry.test.prometheus.BackupPrometheusClient;
import io.inoa.fleet.registry.test.prometheus.ExporterPrometheusClient;
import io.inoa.fleet.registry.test.prometheus.TranslatorPrometheusClient;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class MonitoringClient {

	private final Map<String, Long> before = new HashMap<>();
	private final BackupPrometheusClient backup;
	private final TranslatorPrometheusClient translator;
	private final ExporterPrometheusClient exporter;

	public void reset() {
		before.clear();
		before.put("backup.scrapMessages", backup.scrapMessages());
		before.put("translator.scrapMessagesSuccess", translator.scrapMessagesSuccess());
		before.put("exporter.scrapKafkaRecords", exporter.scrapKafkaRecords());
	}

	// backup

	public long backupMessagesBefore() {
		return before.get("backup.scrapMessages");
	}

	public long backupMessagesNow() {
		return backup.scrapMessages();
	}

	public void awaitbackupMessages(String message, int amount) {
		await(message, () -> backupMessagesNow() == backupMessagesBefore() + amount);
	}

	// translator

	public long translatorSuccessBefore() {
		return before.get("translator.scrapMessagesSuccess");
	}

	public long translatorSuccessNow() {
		return translator.scrapMessagesSuccess();
	}

	public void awaitTranslatorSuccess(String message, int amount) {
		await(message, () -> translatorSuccessNow() == translatorSuccessBefore() + amount);
	}

	// translator

	public long exporterKafkaRecordsBefore() {
		return before.get("exporter.scrapKafkaRecords");
	}

	public long exporterKafkaRecordsNow() {
		return exporter.scrapKafkaRecords();
	}

	public void awaitExporterKafkaRecords(String message, int amount) {
		await(message, () -> exporterKafkaRecordsNow() == exporterKafkaRecordsBefore() + amount);
	}

	// internal

	private void await(String message, Callable<Boolean> conditionEvaluator) {
		Awaitility.await(message)
				.pollInterval(Duration.ofMillis(200))
				.timeout(Duration.ofSeconds(30))
				.until(conditionEvaluator);
	}
}

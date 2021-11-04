package io.inoa.cloud;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.FluxTable;

import io.inoa.cloud.messages.InoaTelemetryMessageVO;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

/**
 * Test for {@link InoaMessageListener}.
 *
 * @author Stephan Schnabel
 */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InoaMessageListenerTest implements TestPropertyProvider {

	@Inject
	@KafkaClient("inoa-producer")
	Producer<String, InoaTelemetryMessageVO> producer;
	@Inject
	InfluxDBClient influxdb;

	@DisplayName("write to influx")
	@Test
	void success() {

		var message = new InoaTelemetryMessageVO()
				.setTenantId("inoa")
				.setGatewayId(UUID.randomUUID())
				.setUrn("urn")
				.setDeviceId(UUID.randomUUID().toString())
				.setDeviceType(UUID.randomUUID().toString())
				.setSensor(UUID.randomUUID().toString())
				.setExt(Map.of("sensor", "should be overidden by origing", "foo", "bar"))
				.setTimestamp(Instant.now().minusSeconds(7200).truncatedTo(ChronoUnit.SECONDS))
				.setValue(123.456D);

		// send message

		var future = producer.send(new ProducerRecord<>("inoa.telemetry.foo", "bar", message));
		Awaitility.await("kafka")
				.pollInterval(Duration.ofMillis(50))
				.timeout(Duration.ofSeconds(5))
				.until(() -> future.isDone());

		// check influx

		Supplier<List<FluxTable>> query = () -> influxdb.getQueryApi()
				.query("from(bucket:\"test-bucket\") |> range(start: -10h)");
		Awaitility.await("influx")
				.pollInterval(Duration.ofMillis(100))
				.timeout(Duration.ofSeconds(5))
				.until(() -> query.get().size() == 1);

		var table = query.get().iterator().next();
		assertEquals(1, table.getRecords().size(), "records");
		var record = table.getRecords().get(0);
		assertAll("record",
				() -> assertEquals("inoa", record.getMeasurement(), "measurement"),
				() -> assertEquals(message.getTenantId().toString(), record.getValueByKey("tenant_id"), "tenant_id"),
				() -> assertEquals(message.getGatewayId().toString(), record.getValueByKey("gateway_id"), "gateway_id"),
				() -> assertEquals(message.getUrn(), record.getValueByKey("urn"), "urn"),
				() -> assertEquals(message.getDeviceId(), record.getValueByKey("device_id"), "device_id"),
				() -> assertEquals(message.getDeviceType(), record.getValueByKey("type"), "type"),
				() -> assertEquals(message.getSensor(), record.getValueByKey("sensor"), "sensor"),
				() -> assertEquals("bar", record.getValueByKey("foo"), "ext.foo"),
				() -> assertEquals(message.getTimestamp(), record.getTime(), "timestamp"),
				() -> assertEquals(message.getValue(), record.getValue(), "value"));
	}

	@Override
	public Map<String, String> getProperties() {

		// kafka

		var kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.1.2"));

		// influxdb

		var username = "username";
		var password = "password";
		var org = "test-org";
		var bucket = "test-bucket";
		@SuppressWarnings("resource")
		var influx = new GenericContainer<>(DockerImageName.parse("influxdb:2.0-alpine"))
				.withEnv("DOCKER_INFLUXDB_INIT_MODE", "setup")
				.withEnv("DOCKER_INFLUXDB_INIT_USERNAME", username)
				.withEnv("DOCKER_INFLUXDB_INIT_PASSWORD", password)
				.withEnv("DOCKER_INFLUXDB_INIT_ORG", org)
				.withEnv("DOCKER_INFLUXDB_INIT_BUCKET", bucket)
				.withExposedPorts(8086)
				.waitingFor(Wait.forListeningPort());

		// start container

		Stream.of(kafka, influx).parallel().forEach(GenericContainer::start);

		return Map.of(
				"kafka.bootstrap.servers", kafka.getBootstrapServers(),
				"kafka.consumers.default.metadata.max.age.ms", "200",
				"influxdb.username", username,
				"influxdb.password", password,
				"influxdb.org", org,
				"influxdb.bucket", bucket,
				"influxdb.url", "http://" + influx.getHost() + ":" + influx.getMappedPort(8086));
	}
}

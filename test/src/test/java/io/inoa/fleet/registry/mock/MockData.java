package io.inoa.fleet.registry.mock;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.Map;

import org.testcontainers.shaded.org.apache.commons.lang.math.RandomUtils;

import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.DecoderException;
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Hex;

import io.inoa.fleet.registry.infrastructure.ComposeTest;
import io.inoa.fleet.registry.test.GatewayClient.GatewayMqttClient;
import io.inoa.fleet.registry.test.GatewayRegistryClient;
import io.micronaut.runtime.Micronaut;
import io.micronaut.scheduling.TaskScheduler;

public class MockData {

	private final static String GATEWAY_1_SERIAL = "mock-001";
	private final static String GATEWAY_1_SECRET = "mock-001-secret-mock-001-secret-mock-001-secret-mock-001-secret";
	private final static String GATEWAY_2_SERIAL = "mock-002";
	private final static String GATEWAY_2_SECRET = "mock-002-secret-mock-002-secret-mock-002-secret-mock-002-secret";

	public static void main(String[] args) {

		var context = Micronaut.build(args)
				.properties(Map.of("influxdb.enabled", "false"))
				.environments("test")
				.banner(false).build().start();

		var registry = context.getBean(GatewayRegistryClient.class).withUser(ComposeTest.USER_TENANT_A);
		var gateway1 = registry
				.toClient(GATEWAY_1_SERIAL, GATEWAY_1_SECRET)
				.orElseGet(() -> registry.createGateway(GATEWAY_1_SERIAL, GATEWAY_1_SECRET))
				.mqtt().connect();

		var gateway2 = registry
				.toClient(GATEWAY_2_SERIAL, GATEWAY_2_SECRET)
				.orElseGet(() -> registry.createGateway(GATEWAY_2_SERIAL, GATEWAY_2_SECRET))
				.mqtt().connect();

		var scheduler = context.getBean(TaskScheduler.class);
		scheduler.scheduleAtFixedRate(Duration.ZERO, Duration.ofSeconds(1), () -> dvh4013(gateway1, "001", 0, 0));
		scheduler.scheduleAtFixedRate(Duration.ZERO, Duration.ofSeconds(1), () -> dvh4013(gateway1, "002", 0, 500));
		scheduler.scheduleAtFixedRate(Duration.ZERO, Duration.ofSeconds(1), () -> dvh4013(gateway1, "003", 0, 1000));
		scheduler.scheduleAtFixedRate(Duration.ZERO, Duration.ofSeconds(1), () -> dvh4013(gateway2, "004", 0, 0));
	}

	private static void dvh4013(GatewayMqttClient gateway, String device, int shiftSecond, int shiftValue) {

		var timestamp = Instant.now();
		var second = timestamp.atOffset(ZoneOffset.UTC).get(ChronoField.SECOND_OF_MINUTE);
		var sinus = Math.sin(Math.toRadians((second + shiftSecond) * 6)) * 1000 + shiftValue;
		var work = timestamp.toEpochMilli()
				- OffsetDateTime.now().with(ChronoField.SECOND_OF_DAY, 0).toInstant().toEpochMilli()
				+ RandomUtils.nextInt(100);

		try {
			gateway.sendTelemetry("urn:dvh4013:" + device + ":0x0000", timestamp, modbus(Math.max(0, sinus)));
			gateway.sendTelemetry("urn:dvh4013:" + device + ":0x0002", timestamp, modbus(Math.min(0, sinus) * -1));
			gateway.sendTelemetry("urn:dvh4013:" + device + ":0x4000", timestamp, modbus(work));
			gateway.sendTelemetry("urn:dvh4013:" + device + ":0x8102", timestamp, modbus(work / 2));
		} catch (Exception e) {
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
	}

	private static byte[] modbus(double value) throws DecoderException {
		var hex = Integer.toHexString((int) value).toUpperCase();
		var hexLength = "0" + (hex.length() % 2 == 1 ? hex.length() + 1 : hex.length()) / 2;
		var message = "0103" + hexLength + (hex.length() % 2 == 1 ? "0" + hex : hex);
		return Hex.decodeHex(message);
	}
}

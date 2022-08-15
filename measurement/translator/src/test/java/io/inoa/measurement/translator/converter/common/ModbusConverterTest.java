package io.inoa.measurement.translator.converter.common;

import static io.inoa.measurement.translator.converter.LogSink.assertMessage;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Hex;

import io.inoa.fleet.telemetry.TelemetryRawVO;
import io.inoa.measurement.translator.converter.AbstractConverterTest;
import io.inoa.measurement.translator.converter.LogSink;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

public class ModbusConverterTest extends AbstractConverterTest {

	@Inject
	ModbusConverter converter;

	@BeforeEach
	void setup() {
		LogSink.setUp(ModbusConverter.class);
	}

	@DisplayName("success: without modifier")
	@Test
	void successWithoutModifier() {
		var telemetry = telemetry("08030240C85413");
		assertSingleValue(16584D, converter, telemetry, "example", "modbus");
		assertMessage(ModbusConverter.class, "Modbus with slaveId 08, functionCode 03 has value: 16584");
	}

	@DisplayName("success: with modifier")
	@Test
	void successWithModifier() {
		var telemetry = telemetry("08030240C85413").urn("urn:example:0815:modbus-with-modifier");
		assertSingleValue(165840D, converter, telemetry, "example", "modbus-with-modifier");
		assertMessage(ModbusConverter.class, "Modbus with slaveId 08, functionCode 03 has value: 16584");
	}

	@DisplayName("fail: payload with invalid crc16")
	@Test
	void failPayloadCrcInvalid() {
		var hex = "010301105044";
		assertEmpty(converter, telemetry(hex), "example", "modbus");
		assertMessage(ModbusConverter.class, "Retrieved invalid modbus message (crc16): " + hex);
	}

	@DisplayName("fail: payload too short")
	@Test
	void failPayloadToShort() {
		var hex = "FC";
		assertEmpty(converter, telemetry(hex), "example", "modbus");
		assertMessage(ModbusConverter.class, "Retrieved invalid modbus message (too short): " + hex);
	}

	@DisplayName("fail: function code invalid")
	@Test
	void failFunctionCodeInvalid() {
		var hex = "010101105044";
		assertEmpty(converter, telemetry(hex), "example", "modbus");
		assertMessage(ModbusConverter.class, "Retrieved invalid modbus message (functionCode 01): " + hex);
	}

	@DisplayName("fail: exception code")
	@Test
	void failExceptionCode() {
		var hex = "0A8102B053";
		assertEmpty(converter, telemetry(hex), "example", "modbus");
		assertMessage(ModbusConverter.class, "Retrieved modbus error message (functionCode 81) with error 2");
	}

	@DisplayName("fail: byte count invalid")
	@Test
	void failByteCountInvalid() {
		var hex = "01030210F174";
		assertEmpty(converter, telemetry(hex), "example", "modbus");
		assertMessage(ModbusConverter.class, "Retrieved invalid modbus message (data.length 1 < byteCount 2): " + hex);
	}

	@DisplayName("fail: byte count empty")
	@Test
	void failByteCountEmpty() {
		var hex = "01030020F0";
		assertEmpty(converter, telemetry(hex), "example", "modbus");
		assertMessage(ModbusConverter.class, "Retrieved invalid modbus message (byteCount == 0): " + hex);
	}

	@SneakyThrows
	private TelemetryRawVO telemetry(String hex) {
		return new TelemetryRawVO()
				.urn("urn:example:0815:modbus")
				.timestamp(Instant.now().toEpochMilli())
				.value(Hex.decodeHex(hex));
	}
}

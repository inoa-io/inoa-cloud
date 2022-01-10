package io.inoa.measurement.translator.converter.common;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Hex;

import io.inoa.fleet.telemetry.TelemetryRawVO;
import io.inoa.measurement.translator.converter.AbstractConverterTest;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

public class ModbusConverterTest extends AbstractConverterTest {

	@Inject
	ModbusConverter converter;

	@DisplayName("success: without modifier")
	@Test
	void successWithoutModifier() {
		var raw = new TelemetryRawVO()
				.setUrn("urn:example:0815:modbus")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue(toModbus("03", "02", "40C8"));
		assertSingleValue(16584D, converter, raw, "example", "modbus");
	}

	@DisplayName("success: with modifier")
	@Test
	void successWithModifier() {
		var raw = new TelemetryRawVO()
				.setUrn("urn:example:0815:modbus-with-modifier")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue(toModbus("03", "02", "40C8"));
		assertSingleValue(165840D, converter, raw, "example", "modbus-with-modifier");
	}

	@DisplayName("fail: payload too short")
	@Test
	void failPayloadToShort() {
		var raw = new TelemetryRawVO()
				.setUrn("urn:example:0815:modbus")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue(new byte[3]);
		assertEmpty(converter, raw, "example", "modbus");
	}

	@DisplayName("fail: function code invalid")
	@Test
	void failFunctionCodeInvalid() {
		var raw = new TelemetryRawVO()
				.setUrn("urn:example:0815:modbus")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue(toModbus("01", "01", "10"));
		assertEmpty(converter, raw, "example", "modbus");
	}

	@DisplayName("fail: exception code")
	@Test
	void failExceptionCode() {
		var raw = new TelemetryRawVO()
				.setUrn("urn:example:0815:modbus")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue(toModbus("81", "02", "0815"));
		assertEmpty(converter, raw, "example", "modbus");
	}

	@DisplayName("fail: byte count invalid")
	@Test
	void failByteCountInvalid() {
		var raw = new TelemetryRawVO()
				.setUrn("urn:example:0815:modbus")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue(toModbus("03", "02", "10"));
		assertEmpty(converter, raw, "example", "modbus");
	}

	@DisplayName("fail: byte count empty")
	@Test
	void failByteCountEmpty() {
		var raw = new TelemetryRawVO()
				.setUrn("urn:example:0815:modbus")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue(toModbus("03", "00", "00"));
		assertEmpty(converter, raw, "example", "modbus");
	}

	@SneakyThrows
	private byte[] toModbus(String functionCode, String length, String value) {
		return Hex.decodeHex("01" + functionCode + length + value);
	}
}

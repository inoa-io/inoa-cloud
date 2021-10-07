package io.inoa.cloud.converter.common;

import java.time.Instant;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Hex;

import io.inoa.cloud.converter.AbstractConverterTest;
import io.inoa.hono.messages.HonoTelemetryMessageVO;
import lombok.SneakyThrows;

/**
 * Test for {@link ModbusConverter}.
 *
 * @author Stephan Schnabel
 */
public class ModbusConverterTest extends AbstractConverterTest {

	@Inject
	ModbusConverter converter;

	@DisplayName("success: without modifier")
	@Test
	void successWithoutModifier() {
		var hono = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:modbus")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue(toModbus("03", "02", "40C8"));
		assertSingleValue(16584D, converter, hono, "example", "modbus");
	}

	@DisplayName("success: with modifier")
	@Test
	void successWithModifier() {
		var hono = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:modbus-with-modifier")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue(toModbus("03", "02", "40C8"));
		assertSingleValue(165840D, converter, hono, "example", "modbus-with-modifier");
	}

	@DisplayName("fail: payload too short")
	@Test
	void failPayloadToShort() {
		var hono = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:modbus")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue(new byte[3]);
		assertEmpty(converter, hono, "example", "modbus");
	}

	@DisplayName("fail: function code invalid")
	@Test
	void failFunctionCodeInvalid() {
		var hono = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:modbus")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue(toModbus("01", "01", "10"));
		assertEmpty(converter, hono, "example", "modbus");
	}

	@DisplayName("fail: byte count invalid")
	@Test
	void failByteCountInvalid() {
		var hono = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:modbus")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue(toModbus("03", "02", "10"));
		assertEmpty(converter, hono, "example", "modbus");
	}

	@DisplayName("fail: byte count empty")
	@Test
	void failByteCountEmpty() {
		var hono = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:modbus")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue(toModbus("03", "00", "00"));
		assertEmpty(converter, hono, "example", "modbus");
	}

	@SneakyThrows
	private byte[] toModbus(String functionCode, String length, String value) {
		return Hex.decodeHex("01" + functionCode + length + value);
	}
}

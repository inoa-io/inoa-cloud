package io.inoa.cloud.converter.common;

import java.util.stream.Stream;

import javax.inject.Singleton;

import io.inoa.cloud.ApplicationProperties;
import io.inoa.cloud.messages.InoaTelemetryMessageVO;
import io.inoa.hono.messages.HonoTelemetryMessageVO;

/**
 * Value converter for Modbus.
 *
 * @author Stephan Schnabel
 * @see "https://npulse.net/en/online-modbus"
 */
@Singleton
public class ModbusConverter extends CommonConverter {

	ModbusConverter(ApplicationProperties properties) {
		super(properties, "modbus");
	}

	@Override
	public Stream<InoaTelemetryMessageVO> convert(HonoTelemetryMessageVO hono, String type, String sensor) {

		// parse to hex

		var hexString = toHexString(hono.getValue());
		if (hexString.length() < 7) {
			log.debug("Retrieved invalid modbus message (too short): {}", hexString);
			return Stream.empty();
		}

		// validate function code

		var slaveId = Integer.parseInt(hexString.substring(0, 2), 16);
		var functionCode = Integer.parseInt(hexString.substring(2, 4), 16);
		if (functionCode != 3) {
			log.debug("Retrieved invalid modbus message (code {}): {}", functionCode, hexString);
			return Stream.empty();
		}

		// read value

		var byteCount = Integer.parseInt(hexString.substring(4, 6), 16);
		if (byteCount == 0) {
			log.debug("Retrieved invalid modbus message (byteCount == 0): {}", hexString);
			return Stream.empty();
		}
		var dataEndIndex = 2 * byteCount + 6;
		if (hexString.length() < dataEndIndex) {
			log.debug("Retrieved invalid modbus message (data.length {} < byteCount {}): {}",
					hexString.length() - 6, dataEndIndex - 6, hexString);
			return Stream.empty();
		}
		var value = Long.parseLong(hexString.substring(6, dataEndIndex), 16);

		log.trace("Modbus with slaveId {}, functionCode {} and value {}.", slaveId, functionCode, value);

		return Stream.of(convert(type, sensor, (double) value));
	}
}

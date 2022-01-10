package io.inoa.measurement.translator.converter.common;

import java.util.Set;
import java.util.stream.Stream;

import io.inoa.fleet.telemetry.TelemetryRawVO;
import io.inoa.measurement.telemetry.TelemetryVO;
import io.inoa.measurement.translator.ApplicationProperties;
import jakarta.inject.Singleton;

/**
 * Value converter for Modbus.
 *
 * @see "https://npulse.net/en/online-modbus"
 */
@Singleton
public class ModbusConverter extends CommonConverter {

	/**
	 * Exception codes for modbus.
	 *
	 * @see "https://www.simplymodbus.ca/exceptions.htm#table1"
	 */
	private static final Set<Integer> FUNCTION_CODE_EXCEPTION = Set.of(129, 130, 131, 132, 133, 134, 143, 144);

	ModbusConverter(ApplicationProperties properties) {
		super(properties, "modbus");
	}

	@Override
	public Stream<TelemetryVO> convert(TelemetryRawVO raw, String type, String sensor) {

		// parse to hex

		var hexString = toHexString(raw.getValue());
		if (hexString.length() < 7) {
			log.info("Retrieved invalid modbus message (too short): {}", hexString);
			return Stream.empty();
		}

		// validate function code

		var slaveId = Integer.parseInt(hexString.substring(0, 2), 16);
		var functionCode = Integer.parseInt(hexString.substring(2, 4), 16);
		if (functionCode != 3) {
			if (FUNCTION_CODE_EXCEPTION.contains(functionCode)) {
				var error = Integer.parseInt(hexString.substring(4, 6), 16);
				log.info("Retrieved modbus error message (functionCode {}) with error {}", functionCode, error);
			} else {
				log.info("Retrieved invalid modbus message (functionCode {}): {}", functionCode, hexString);
			}
			return Stream.empty();
		}

		// read value

		var byteCount = Integer.parseInt(hexString.substring(4, 6), 16);
		if (byteCount == 0) {
			log.info("Retrieved invalid modbus message (byteCount == 0): {}", hexString);
			return Stream.empty();
		}
		var dataEndIndex = 2 * byteCount + 6;
		if (hexString.length() < dataEndIndex) {
			log.info("Retrieved invalid modbus message (data.length {} < byteCount {}): {}",
					hexString.length() - 6, dataEndIndex - 6, hexString);
			return Stream.empty();
		}
		var value = Long.parseLong(hexString.substring(6, dataEndIndex), 16);

		log.trace("Modbus with slaveId {}, functionCode {} and value {}.", slaveId, functionCode, value);

		return Stream.of(convert(type, sensor, (double) value));
	}
}

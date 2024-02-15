package io.inoa.measurement.translator.converter.common;

import java.util.HexFormat;
import java.util.Set;
import java.util.stream.Stream;

import io.inoa.measurement.ApplicationProperties;
import io.inoa.measurement.translator.modbus.CRC16;
import io.inoa.rest.TelemetryRawVO;
import io.inoa.rest.TelemetryVO;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Singleton;

/**
 * Value converter for Modbus.
 *
 * @see "https://npulse.net/en/online-modbus"
 */
@Singleton
public class ModbusConverter extends CommonConverter {

	public static final String COUNTER_FAIL_SHORT = "translator_modbus_fail_short";
	public static final String COUNTER_FAIL_CRC = "translator_modbus_fail_crc";
	public static final String COUNTER_FAIL_BYTE_COUNT = "translator_modbus_fail_byte_count";
	public static final String COUNTER_INGORE_EMPTY = "translator_modbus_ignore_empty";
	public static final String COUNTER_INGORE_ERROR_CODE = "translator_modbus_ignore_error_code";
	public static final String COUNTER_INGORE_FUNCTION_CODE = "translator_modbus_ignore_function_code";
	public static final String COUNTER_SUCCESS = "translator_modbus_success";

	/**
	 * Exception codes for modbus.
	 *
	 * @see "https://www.simplymodbus.ca/exceptions.htm#table1"
	 */
	private static final Set<Integer> FUNCTION_CODE_EXCEPTION = Set.of(129, 130, 131, 132, 133, 134, 143, 144);

	/**
	 * Minimum length: 1 byte functionCode + 1 byte slaveId + 1 byte byteCount + 2 byte crc
	 */
	private static final Integer MESSAGE_MIN_LENGTH = 5;

	ModbusConverter(ApplicationProperties properties, MeterRegistry meterRegistry) {
		super(properties, meterRegistry, "modbus");
	}

	@Override
	public Stream<TelemetryVO> convert(TelemetryRawVO raw, String type, String sensor) {

		// parse to hex

		var hexString = HexFormat.of().formatHex(raw.getValue());
		if (raw.getValue().length < MESSAGE_MIN_LENGTH) {
			log.warn("Retrieved invalid modbus message (too short): {}", hexString);
			increment(type, COUNTER_FAIL_SHORT);
			return Stream.empty();
		}

		// check crc

		if (!CRC16.isValid(raw.getValue())) {
			log.info("Retrieved invalid modbus message (crc16): {}", hexString);
			increment(type, COUNTER_FAIL_CRC);
			return Stream.empty();
		}

		// validate function code

		var slaveIdHex = hexString.substring(0, 2);
		var functionCodeHex = hexString.substring(2, 4);
		var functionCode = Integer.parseInt(functionCodeHex, 16);
		if (functionCode != 3) {
			if (FUNCTION_CODE_EXCEPTION.contains(functionCode)) {
				var error = Integer.parseInt(hexString.substring(4, 6), 16);
				log.info("Retrieved modbus error message (functionCode {}) with error {}", functionCodeHex, error);
				increment(type, COUNTER_INGORE_ERROR_CODE);
			} else {
				log.info("Retrieved invalid modbus message (functionCode {}): {}", functionCodeHex, hexString);
				increment(type, COUNTER_INGORE_FUNCTION_CODE);
			}
			return Stream.empty();
		}

		// read value

		var byteCount = Integer.parseInt(hexString.substring(4, 6), 16);
		if (byteCount == 0) {
			log.info("Retrieved invalid modbus message (byteCount == 0): {}", hexString);
			increment(type, COUNTER_INGORE_EMPTY);
			return Stream.empty();
		}
		var dataEndIndex = 2 * byteCount + 6;
		int hexLength = hexString.length() - 4;
		if (hexLength < dataEndIndex) {
			log.info("Retrieved invalid modbus message (data.length {} < byteCount {}): {}", (hexLength - 6) / 2,
					(dataEndIndex - 6) / 2, hexString);
			increment(type, COUNTER_FAIL_BYTE_COUNT);
			return Stream.empty();
		}
		var value = Long.parseLong(hexString.substring(6, dataEndIndex), 16);

		log.trace("Modbus with slaveId {}, functionCode {} has value: {}", slaveIdHex, functionCodeHex, value);
		increment(type, COUNTER_SUCCESS);

		return Stream.of(convert(type, sensor, (double) value));
	}
}

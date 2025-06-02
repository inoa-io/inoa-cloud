package io.inoa.measurement.things.builder.modbus;

import static io.inoa.measurement.things.domain.ObisId.OBIS_1_7_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_1_8_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_2_8_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_METER_SERIAL;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.inoa.measurement.things.builder.DatapointBuilderException;
import io.inoa.measurement.things.domain.Thing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Builder for <a
 * href=
 * "https://www.device.de/index.php/produkte/kommunikationsadapter-zur-zaehlerauslesung-stationaer/dvmodbusir">DvModbusIR
 * devices (Device GmbH)</a>
 *
 * @author fabian.schlegel@grayc.de
 */
@SuppressWarnings("unchecked")
@Slf4j
@RequiredArgsConstructor
public class DvModbusIRBuilder extends ModbusBuilderBase {

	private static final short FUNCTION_CODE_SERIAL_NUMBER = 0x000B;
	private static final short FUNCTION_CODE_OBIS_1_8_0 = 0x000D;
	private static final short FUNCTION_CODE_OBIS_2_8_0 = 0x001C;
	private static final short FUNCTION_CODE_OBIS_1_7_0 = 0x002B;

	private static final Map<String, RegisterSetting> MAPPINGS;

	static {
		MAPPINGS = new HashMap<>();
		MAPPINGS.put(
				OBIS_METER_SERIAL.getObisId(),
				new RegisterSetting((byte) 3, FUNCTION_CODE_SERIAL_NUMBER, (short) 2));
		MAPPINGS.put(
				OBIS_1_8_0.getObisId(), new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_1_8_0, (short) 4));
		MAPPINGS.put(
				OBIS_2_8_0.getObisId(), new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_2_8_0, (short) 4));
		MAPPINGS.put(
				OBIS_1_7_0.getObisId(), new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_1_7_0, (short) 2));
	}

	private final ObjectMapper objectMapper;

	@Override
	public ArrayNode build(Thing thing) throws DatapointBuilderException {
		var serial = getConfigAsNumber(thing, CONFIG_KEY_SERIAL).intValue();
		int slaveId = serial % 1000;
		if (slaveId > 255) {
			log.warn("slaveId greater then 255 id: {} -- serial: {}", slaveId, serial);
		}
		return toDatapoints(thing, slaveId, MAPPINGS);
	}

	@Override
	public ArrayNode buildRPC(Thing thing) {
		return null;
	}

	@Override
	protected ObjectMapper getObjectMapper() {
		return objectMapper;
	}
}

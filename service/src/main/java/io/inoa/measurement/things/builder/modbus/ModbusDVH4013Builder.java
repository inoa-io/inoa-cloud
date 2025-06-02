package io.inoa.measurement.things.builder.modbus;

import static io.inoa.measurement.things.domain.ObisId.OBIS_1_7_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_1_8_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_2_7_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_2_8_0;
import static java.lang.Math.abs;

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
 * "https://www.dzg.de/fileadmin/dzg/content/downloads/produkte-zaehler/dvh/DZG_DWH4113_Handbuch_231114.pdf">DVH4013
 * devices (DZG GmbH)</a>
 *
 * @author fabian.schlegel@grayc.de
 */
@SuppressWarnings("unchecked")
@Slf4j
@RequiredArgsConstructor
public class ModbusDVH4013Builder extends ModbusBuilderBase {

	private static final short FUNCTION_CODE_POWER_IN = 0x0000;
	private static final short FUNCTION_CODE_POWER_OUT = 0x0002;
	private static final short FUNCTION_CODE_OBIS_1_8_0 = 0x4001;
	private static final short FUNCTION_CODE_OBIS_2_8_0 = 0x4101;

	private static final Map<String, RegisterSetting> MAPPINGS;

	static {
		MAPPINGS = new HashMap<>();
		MAPPINGS.put(
				OBIS_1_7_0.getObisId(), new RegisterSetting((byte) 3, FUNCTION_CODE_POWER_IN, (short) 2));
		MAPPINGS.put(
				OBIS_2_7_0.getObisId(), new RegisterSetting((byte) 3, FUNCTION_CODE_POWER_OUT, (short) 2));
		MAPPINGS.put(
				OBIS_1_8_0.getObisId(), new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_1_8_0, (short) 2));
		MAPPINGS.put(
				OBIS_2_8_0.getObisId(), new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_2_8_0, (short) 2));
	}

	private final ObjectMapper objectMapper;

	@Override
	public ArrayNode build(Thing thing) throws DatapointBuilderException {
		var serial = getConfigAsNumber(thing, CONFIG_KEY_SERIAL).intValue();
		String hex = String.format("%02d", abs(serial + 1) % 100);
		int slaveId = Integer.parseInt(hex, 16);
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

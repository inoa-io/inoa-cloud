package io.inoa.measurement.things.builder.modbus;

import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.inoa.measurement.things.builder.DatapointBuilder;
import io.inoa.measurement.things.builder.DatapointBuilderException;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.rest.DatapointVO;

/**
 * Abstract class for datapoint builders with ModBus measurement (e.g. DZG devices)
 *
 * @author fabian.schlegel
 */
public abstract class ModbusBuilderBase extends DatapointBuilder {

	protected abstract ObjectMapper getObjectMapper();

	private static final int DEFAULT_TIMEOUT = 1000;
	private static final int DEFAULT_INTERVAL = 60000;

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static final String CONFIG_KEY_SERIAL = "serial";
	public static final String CONFIG_KEY_MODBUS_INTERFACE = "modbus_interface";

	protected ObjectNode createModbusJsonNode(
			Integer serial,
			String thingTypeReference,
			RegisterSetting registerSetting,
			Thing thing,
			Integer modbusInterface,
			byte slaveId,
			Long interval,
			Long timeout,
			Boolean enabled) {
		return new ObjectMapper()
				.valueToTree(
						new DatapointVO()
								.id(
										Utils.buildUrn(
												serial.toString(),
												thingTypeReference,
												String.format("0x%04X", registerSetting.getRegisterOffset())))
								.name(thing.getName())
								.enabled(enabled != null ? enabled : false)
								.type(DatapointVO.Type.RS485)
								.interval(interval != null ? interval.intValue() : DEFAULT_INTERVAL)
								._interface(modbusInterface)
								.frame(
										HexFormat.of()
												.withUpperCase()
												.formatHex(
														Utils.buildFrame(
																slaveId,
																registerSetting.getFunctionCode(),
																registerSetting.getRegisterOffset(),
																registerSetting.getRegisterLength())))
								.timeout(timeout != null ? timeout.intValue() : DEFAULT_TIMEOUT));
	}

	protected Optional<ObjectNode> obisCodeToDatapointJSON(
			Thing thing,
			String obisCodeKey,
			RegisterSetting registerSetting,
			Integer serial,
			int slaveId,
			Integer modbusInterface) {
		var measurand = thing.getMeasurands().stream()
				.filter(m -> obisCodeKey.equals(m.getMeasurandType().getObisId()))
				.findFirst();
		if (measurand.isPresent()) {
			ObjectNode node = createModbusJsonNode(
					serial,
					thing.getThingType().getIdentifier(),
					registerSetting,
					thing,
					modbusInterface,
					(byte) slaveId,
					measurand.get().getInterval(),
					measurand.get().getTimeout(),
					measurand.get().getEnabled());
			return Optional.of(node);
		}
		return Optional.empty();
	}

	protected ArrayNode toDatapoints(Thing thing, int slaveId, Map<String, RegisterSetting> mappings)
			throws DatapointBuilderException {
		ArrayNode datapoints = OBJECT_MAPPER.createArrayNode();
		var serial = getConfigAsNumber(thing, CONFIG_KEY_SERIAL).intValue();
		var modbusInterface = getConfigAsNumber(thing, CONFIG_KEY_MODBUS_INTERFACE).intValue();

		for (var mapping : mappings.entrySet()) {
			Optional<ObjectNode> optionalValue = obisCodeToDatapointJSON(
					thing, mapping.getKey(), mapping.getValue(), serial, slaveId, modbusInterface);
			optionalValue.ifPresent(datapoints::add);
		}
		return datapoints;
	}
}

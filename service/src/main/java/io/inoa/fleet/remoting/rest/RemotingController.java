package io.inoa.fleet.remoting.rest;

import static io.inoa.fleet.remoting.RemotingService.COMMAND_DATAPOINTS_ADD_ADC;
import static io.inoa.fleet.remoting.RemotingService.COMMAND_DATAPOINTS_ADD_HTTPGET;
import static io.inoa.fleet.remoting.RemotingService.COMMAND_DATAPOINTS_ADD_MBUS;
import static io.inoa.fleet.remoting.RemotingService.COMMAND_DATAPOINTS_ADD_RMS;
import static io.inoa.fleet.remoting.RemotingService.COMMAND_DATAPOINTS_ADD_RS485;
import static io.inoa.fleet.remoting.RemotingService.COMMAND_DATAPOINTS_ADD_S0;
import static io.inoa.fleet.remoting.RemotingService.COMMAND_DATAPOINTS_ADD_TCP;
import static io.inoa.fleet.remoting.RemotingService.COMMAND_DATAPOINTS_ADD_WMBUS;
import static io.inoa.fleet.remoting.RemotingService.COMMAND_SYSTEM_REBOOT;
import static io.inoa.fleet.remoting.RemotingService.COMMAND_SYSTEM_WINK;
import static io.inoa.measurement.things.domain.MeasurandProtocol.ADC;
import static io.inoa.measurement.things.domain.MeasurandProtocol.JSON_REST_HTTP;
import static io.inoa.measurement.things.domain.MeasurandProtocol.MBUS;
import static io.inoa.measurement.things.domain.MeasurandProtocol.MODBUS_RS458;
import static io.inoa.measurement.things.domain.MeasurandProtocol.MODBUS_TCP;
import static io.inoa.measurement.things.domain.MeasurandProtocol.RMS;
import static io.inoa.measurement.things.domain.MeasurandProtocol.S0;
import static io.inoa.measurement.things.domain.MeasurandProtocol.WMBUS;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.inoa.fleet.remoting.RemotingService;
import io.inoa.measurement.things.builder.DatapointBuilderException;
import io.inoa.measurement.things.builder.DatapointService;
import io.inoa.measurement.things.domain.MeasurandProtocol;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingRepository;
import io.inoa.rest.RemoteApi;
import io.inoa.shared.Security;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RemotingController implements RemoteApi {

	private final GatewayRepository gatewayRepository;
	private final ThingRepository thingRepository;
	private final DatapointService datapointService;
	private final RemotingService remotingService;

	private final Security security;

	@Override
	public HttpResponse<Object> getConfigFromGateway(String gatewayId) {
		return null;
	}

	@Override
	public HttpResponse<Object> getThingsFromGateway(String gatewayId) {
		return null;
	}

	@Override
	public HttpResponse<Object> reboot(String gatewayId) {
		return doRpcAsRestCall("inoa", checkGateway(gatewayId).getGatewayId(), COMMAND_SYSTEM_REBOOT, Optional.empty());
	}

	@Override
	public HttpResponse<Object> syncConfigToGateway(String gatewayId) {
		return null;
	}

	@Override
	public HttpResponse<Object> syncThingsToGateway(String gatewayId) {
		var gateway = checkGateway(gatewayId);
		var things = thingRepository.findByGateway(gateway);
		try {
			syncThingsByProtocol(things, gatewayId, JSON_REST_HTTP);
			syncThingsByProtocol(things, gatewayId, MODBUS_RS458);
			syncThingsByProtocol(things, gatewayId, MODBUS_TCP);
			syncThingsByProtocol(things, gatewayId, S0);
			syncThingsByProtocol(things, gatewayId, MBUS);
			syncThingsByProtocol(things, gatewayId, WMBUS);
			syncThingsByProtocol(things, gatewayId, ADC);
			syncThingsByProtocol(things, gatewayId, RMS);
			var datapointsJson = datapointService.getDatapointsJson(things);
			return HttpResponse.ok(datapointsJson);
		} catch (DatapointBuilderException e) {
			return HttpResponse.status(500, e.toString());
		} catch (ExecutionException | InterruptedException e) {
			return HttpResponse.status(500, e.getLocalizedMessage());
		} catch (JsonProcessingException e) {
			return HttpResponse.status(500, "Unprocessable Json: " + e.getMessage());
		} catch (TimeoutException e) {
			return HttpResponse.status(504, "No response from device within timeout.");
		}
	}

	@Override
	public HttpResponse<Object> wink(String gatewayId) {
		return doRpcAsRestCall("inoa", checkGateway(gatewayId).getGatewayId(), COMMAND_SYSTEM_WINK, Optional.empty());
	}

	private List<Thing> getThingsByProtocol(List<Thing> things, MeasurandProtocol protocol) {
		return things.stream().filter(thing -> protocol.equals(thing.getThingType().getProtocol()))
				.collect(Collectors.toList());
	}

	private String getRpcCommandByProtocol(MeasurandProtocol protocol) {
		return switch (protocol) {
			case JSON_REST_HTTP -> COMMAND_DATAPOINTS_ADD_HTTPGET;
			case MODBUS_RS458 -> COMMAND_DATAPOINTS_ADD_RS485;
			case MODBUS_TCP -> COMMAND_DATAPOINTS_ADD_TCP;
			case S0 -> COMMAND_DATAPOINTS_ADD_S0;
			case MBUS -> COMMAND_DATAPOINTS_ADD_MBUS;
			case WMBUS -> COMMAND_DATAPOINTS_ADD_WMBUS;
			case ADC -> COMMAND_DATAPOINTS_ADD_ADC;
			case RMS -> COMMAND_DATAPOINTS_ADD_RMS;
		};
	}

	private void syncThingsByProtocol(List<Thing> things, String gatewayId, MeasurandProtocol protocol)
			throws DatapointBuilderException, ExecutionException, JsonProcessingException, InterruptedException,
			TimeoutException {
		var thingsForProtocol = things.stream().filter(thing -> protocol.equals(thing.getThingType().getProtocol()))
				.toList();
		for (var thing : thingsForProtocol) {
			for (JsonNode datapoint : datapointService.getDatapointsJson(List.of(thing))) {
				remotingService.sendRpcCommand("inoa", gatewayId, getRpcCommandByProtocol(protocol),
						Optional.of(datapoint));
			}
		}
	}

	private Gateway checkGateway(String gatewayId) {
		var gateway = gatewayRepository.findByGatewayId(gatewayId);
		if (gateway.isEmpty()
				|| !Objects.equals(security.getTenantId(), gateway.get().getTenant().getTenantId())) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found: " + gatewayId);
		}
		return gateway.get();
	}

	private MutableHttpResponse<Object> doRpcAsRestCall(String tenant, String gatewayId, String command,
			Optional<Object> parameters) {
		try {
			var result = remotingService.sendRpcCommand(tenant, gatewayId, command, parameters);
			if (result.getError() != null) {
				throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, result.getError().toString());
			}
			return HttpResponse.ok(result.getResult());
		} catch (ExecutionException | InterruptedException e) {
			throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		} catch (JsonProcessingException e) {
			throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unprocessable Json: " + e.getMessage());
		} catch (TimeoutException e) {
			throw new HttpStatusException(HttpStatus.GATEWAY_TIMEOUT, "No response from device within timeout.");
		}
	}
}

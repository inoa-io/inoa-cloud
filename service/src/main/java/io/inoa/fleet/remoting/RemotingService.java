package io.inoa.fleet.remoting;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.inject.Singleton;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.inoa.fleet.mqtt.MqttBroker;
import io.inoa.rest.RpcCommandVO;
import io.inoa.rest.RpcResponseVO;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Service for sending RPC commands to gateways. */
@Slf4j
@Singleton
@RequiredArgsConstructor
public class RemotingService {

	/** Read device status */
	public static final String COMMAND_STATUS_READ = "status.read";
	/** Read OS version */
	public static final String COMMAND_SYSTEM_VERSION = "sys.version";
	/** Get free RAM on device */
	public static final String COMMAND_SYSTEM_FREE = "sys.free";
	/** Get free Heap on device */
	public static final String COMMAND_SYSTEM_HEAP = "sys.heap";
	/** Let the status LED blink */
	public static final String COMMAND_SYSTEM_WINK = "sys.wink";
	/** Reboot system */
	public static final String COMMAND_SYSTEM_REBOOT = "sys.reboot";
	/** Start VPN */
	public static final String COMMAND_SYSTEM_VPN_START = "sys.vpn.start";
	/** Stop VPN */
	public static final String COMMAND_SYSTEM_VPN_STOP = "sys.vpn.stop";
	/** Read system configuration */
	public static final String COMMAND_CONFIG_READ = "config.read";
	/** Write system configuration */
	public static final String COMMAND_CONFIG_WRITE = "config.write";
	/** List datapoints */
	public static final String COMMAND_DATAPOINTS_LIST = "dp.list";
	/** Get a specific datapoint */
	public static final String COMMAND_DATAPOINTS_GET = "dp.get";
	/** Delete specific datapoint */
	public static final String COMMAND_DATAPOINTS_DELETE = "dp.delete";
	/** Delete all datapoints */
	public static final String COMMAND_DATAPOINTS_CLEAR = "dp.clear";
	/** Add RS485 datapoint */
	public static final String COMMAND_DATAPOINTS_ADD_RS485 = "dp.add.rs485";
	/** Add HTTP GET datapoint */
	public static final String COMMAND_DATAPOINTS_ADD_HTTPGET = "dp.add.http.get";
	/** Add S0 datapoint */
	public static final String COMMAND_DATAPOINTS_ADD_S0 = "dp.add.s0";
	/** Add ModBus TCP datapoint */
	public static final String COMMAND_DATAPOINTS_ADD_TCP = "dp.add.tcp";
	/** Add MBus datapoint */
	public static final String COMMAND_DATAPOINTS_ADD_MBUS = "dp.add.mbus";
	/** Add WMBus datapoint */
	public static final String COMMAND_DATAPOINTS_ADD_WMBUS = "dp.add.wmbus";
	/** Add ADC datapoint */
	public static final String COMMAND_DATAPOINTS_ADD_ADC = "dp.add.adc";
	/** Add RMS datapoint */
	public static final String COMMAND_DATAPOINTS_ADD_RMS = "dp.add.rms";

	/** Send a RS485 frame and return answer */
	public static final String COMMAND_METERING_RS485_FRAME = "metering.rs485.frame";
	/** Send console command and return answer */
	public static final String COMMAND_CONSOLE = "console.command";

	public static final String COMMAND_TOPIC_FORMAT_STRING = MqttBroker.COMMAND_TOPIC_LONG_NAME
			+ "/%s/%s/req/%s/cloudEventRpc";

	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private final RemotingHandler remotingHandler;
	private final ObjectMapper mapper;
	private final MqttBroker mqttBroker;

	public RpcResponseVO sendRpcCommandSync(String tenantId, String gatewayId, RpcCommandVO command, long timeout)
			throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
		sendRpcCommandInternal(tenantId, gatewayId, command);
		// Wait for RPC response with 20s timeout
		Future<RpcResponseVO> rpcResponseFuture = waitForRpcResponse(command.getId());
		return rpcResponseFuture.get(timeout, TimeUnit.MILLISECONDS);
	}

	public void sendRpcCommandAsync(String tenantId, String gatewayId, RpcCommandVO command)
			throws JsonProcessingException {
		sendRpcCommandInternal(tenantId, gatewayId, command);
	}

	private void sendRpcCommandInternal(String tenantId, String gatewayId, RpcCommandVO command)
			throws JsonProcessingException {

		log.trace(
				"Sending command on tenant '{}' for gateway '{}' with message: {}",
				tenantId,
				gatewayId,
				command);

		// Create RPC message
		var commandJson = mapper.writeValueAsString(command);
		var topic = COMMAND_TOPIC_FORMAT_STRING.formatted(tenantId, gatewayId, command.getId());
		var message = MqttMessageBuilders.publish()
				.topicName(topic)
				.retained(true)
				.qos(MqttQoS.AT_LEAST_ONCE)
				.payload(Unpooled.copiedBuffer(commandJson.getBytes(UTF_8)))
				.build();

		// Send internal message
		var routingResults = mqttBroker.internalPublish(message, RemotingService.class.toString());
		if (routingResults.isAllFailed()) {
			log.error("Error sending command: {}", routingResults);
			throw new IllegalStateException("All MQTT routes failed.");
		}
	}

	private Future<RpcResponseVO> waitForRpcResponse(String commandId) {
		return executor.submit(
				() -> {
					while (remotingHandler.getCommandResponse(commandId).isEmpty()) {
						Thread.sleep(100);
					}
					return remotingHandler.getCommandResponse(commandId).get();
				});
	}
}

package io.inoa.fleet.command;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.hono.application.client.ApplicationClient;
import org.eclipse.hono.application.client.MessageContext;
import org.eclipse.hono.client.ServiceInvocationException;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.vertx.core.buffer.Buffer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/command")
@RequiredArgsConstructor
public class CommandController {

	private final ApplicationClient<? extends MessageContext> honoClient;
	private final ObjectMapper objectMapper;

	/**
	 * use sendRpcCommand
	 */
	@Deprecated
	@PostMapping("/{tenantId}/{deviceId}")
	public DeferredResult<ResponseEntity<?>> sendCommand(@PathVariable String tenantId, @PathVariable String deviceId,
			@RequestBody Object data) throws JsonProcessingException {
		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
		final Buffer commandBuffer = buildCommandPayload(objectMapper.writeValueAsBytes(data));
		final String command = "cloudEvent";
		honoClient.sendCommand(tenantId, deviceId, command, "application/json", commandBuffer, new HashMap<>())
				.map(result -> {
					log.info("Successfully sent command payload: [{}].", commandBuffer.toString());
					log.info("And received response: [{}].",
							Optional.ofNullable(result.getPayload()).orElseGet(Buffer::buffer).toString());
					output.setResult(ResponseEntity
							.ok(Optional.ofNullable(result.getPayload()).orElseGet(Buffer::buffer).toString()));
					return result;
				}).otherwise(t -> {
					if (t instanceof ServiceInvocationException) {
						final int errorCode = ((ServiceInvocationException) t).getErrorCode();
						log.debug("Command was replied with error code [{}].", errorCode);
						output.setResult(ResponseEntity.status(errorCode).build());
					} else {
						log.debug("Could not send command : {}.", t.getMessage());
						output.setResult(ResponseEntity.status(503).build());
					}
					return null;
				});
		return output;
	}

	@Data
	private static class RpcCommand {

		private String id;
		private String method;
		private JsonNode params;
	}

	@PostMapping("/{tenantId}/{deviceId}/rpc")
	public DeferredResult<ResponseEntity<?>> sendRpcCommand(@PathVariable String tenantId,
			@PathVariable String deviceId, @RequestBody RpcCommand data) throws JsonProcessingException {
		MDC.put("tenantId", tenantId);
		MDC.put("gatewayId", deviceId);
		Objects.requireNonNull(data);
		Objects.requireNonNull(data.getId());
		Objects.requireNonNull(data.getMethod());

		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
		var now = OffsetDateTime.now();
		var id = "rpc@" + now;
		CloudEvent cloudEvent = CloudEventBuilder.v1()
				.withSource(URI.create(String.format("/command/%s/%s/rpc", tenantId, deviceId))).withId(id)
				.withSubject("rpc").withType("io.inoa.fleet.rpc").withTime(OffsetDateTime.now())
				.withDataContentType(MediaType.APPLICATION_JSON_VALUE)
				.withData(PojoCloudEventData.wrap(data, objectMapper::writeValueAsBytes)).build();
		final Buffer commandBuffer = buildCommandPayload(objectMapper.writeValueAsBytes(cloudEvent));
		final String command = "cloudEventRpc";
		log.info("start sending command with id {} to gateway {} for tenant {}", id, deviceId, tenantId);
		honoClient.sendCommand(tenantId, deviceId, command, "application/json", commandBuffer, new HashMap<>())
				.map(result -> {
					log.info("Successfully sent command payload: [{}].", commandBuffer);
					log.info("And received response: [{}].",
							Optional.ofNullable(result.getPayload()).orElseGet(Buffer::buffer));
					try {
						var resultCloudEvent = objectMapper.readValue(result.getPayload().getBytes(), CloudEvent.class);
						resultCloudEvent.getData().toBytes();
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					}
					output.setResult(ResponseEntity
							.ok(Optional.ofNullable(result.getPayload()).orElseGet(Buffer::buffer).toString()));
					return result;
				}).otherwise(t -> {
					if (t instanceof ServiceInvocationException) {
						final int errorCode = ((ServiceInvocationException) t).getErrorCode();
						log.debug("Command was replied with error code [{}].", errorCode);
						output.setResult(ResponseEntity.status(errorCode).build());
					} else {
						log.debug("Could not send command : {}.", t.getMessage());
						output.setResult(ResponseEntity.status(503).build());
					}
					return null;
				}).onComplete(f -> {
					MDC.remove("tenantId");
					MDC.remove("gatewayId");
				});
		return output;
	}

	private Buffer buildCommandPayload(byte[] data) {
		return Buffer.buffer(data);
	}
}

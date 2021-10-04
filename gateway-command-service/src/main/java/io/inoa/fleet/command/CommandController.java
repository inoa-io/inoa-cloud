package io.inoa.fleet.command;

import java.util.HashMap;
import java.util.Optional;

import org.eclipse.hono.application.client.ApplicationClient;
import org.eclipse.hono.application.client.MessageContext;
import org.eclipse.hono.client.ServiceInvocationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.buffer.Buffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/command")
@RequiredArgsConstructor
public class CommandController {
	private final ApplicationClient<? extends MessageContext> honoClient;
	private final ObjectMapper objectMapper;

	@PostMapping("/{tenantId}/{deviceId}")
	public DeferredResult<ResponseEntity<?>> los(@PathVariable String tenantId, @PathVariable String deviceId,
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

	private Buffer buildCommandPayload(byte[] data) {
		return Buffer.buffer(data);
	}
}

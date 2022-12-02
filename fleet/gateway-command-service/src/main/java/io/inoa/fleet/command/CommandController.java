package io.inoa.fleet.command;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.hono.application.client.ApplicationClient;
import org.eclipse.hono.application.client.MessageContext;
import org.eclipse.hono.client.ServiceInvocationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.vertx.core.buffer.Buffer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommandController {

	private final ApplicationClient<? extends MessageContext> honoClient;
	private final ObjectMapper objectMapper;

	@Post("/{tenantId}/{deviceId}/rpc")
	public HttpResponse<?> sendRpcCommand(@PathVariable String tenantId, @PathVariable String deviceId,
			@Body String body) throws JsonProcessingException, InterruptedException {
		RpcCommand data = objectMapper.readValue(body, RpcCommand.class);
		Objects.requireNonNull(data);
		Objects.requireNonNull(data.getId());
		Objects.requireNonNull(data.getMethod());

		final Buffer commandBuffer = buildCommandPayload(body.getBytes());
		final String command = "cloudEventRpc";
		final String[] res = {null};
		CountDownLatch cdl = new CountDownLatch(1);
		honoClient.sendCommand(tenantId, deviceId, command, commandBuffer, "application/json").map(result -> {
			log.info("Successfully sent command payload: [{}].", commandBuffer.toString());
			log.info("And received response: [{}].",
					Optional.ofNullable(result.getPayload()).orElseGet(Buffer::buffer).toString());
			res[0] = Optional.ofNullable(result.getPayload()).orElseGet(Buffer::buffer).toString();
			cdl.countDown();
			return result;
		}).otherwise(t -> {
			if (t instanceof ServiceInvocationException sie) {
				final int errorCode = sie.getErrorCode();
				log.debug("Command was replied with error code [{}].", errorCode);
			} else {
				log.debug("Could not send command : {}.", t.getMessage());
			}
			cdl.countDown();
			return null;
		});
		cdl.await(10, TimeUnit.SECONDS);
		if (res[0] == null) {
			return HttpResponse.status(HttpStatus.SERVICE_UNAVAILABLE);
		}
		return HttpResponse.ok(res[0]);
	}

	private Buffer buildCommandPayload(byte[] data) {
		return Buffer.buffer(data);
	}

	@Data
	private static class RpcCommand {

		private String id;
		private String method;
		private JsonNode params;
	}
}

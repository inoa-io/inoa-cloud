package io.inoa.fleet.remoting;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.inoa.rest.RemoteApi;
import io.inoa.rest.RpcCommandVO;
import io.inoa.rest.RpcResponseVO;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.AllArgsConstructor;

/**
 * Handles RPC commands to be distributed to gateways. This procedure is single threaded by
 * intention for now. Goal is to prevent sending multiple RPCs to one gateway.
 */
@Controller
@AllArgsConstructor
public class RemoteController implements RemoteApi {

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final GatewayRepository gatewayRepository;
	private final RemotingService remotingService;
	private final RemotingHandler remotingHandler;

	@Override
	public HttpResponse<RpcResponseVO> sendRpcCommand(
			@NonNull String gatewayId, @NonNull RpcCommandVO rpcCommandVO) {

		if (gatewayRepository.findByGatewayId(gatewayId).isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "GatewayId does not exist.");
		}

		// If no ID is provided, create a distinct one
		if (rpcCommandVO.getId() == null || rpcCommandVO.getId().isEmpty()) {
			var commandId = UUID.randomUUID().toString();
			rpcCommandVO.setId(commandId);
		}

		try {
			// Tracing
			MDC.put("tenantId", "inoa");
			MDC.put("gatewayId", gatewayId);
			// Send RPC command
			return HttpResponse.ok(remotingService.sendRpcCommandSync("inoa", gatewayId, rpcCommandVO, 5000));
		} catch (TimeoutException e) {
			return HttpResponse.status(HttpStatus.REQUEST_TIMEOUT);
		} catch (JsonProcessingException e) {
			return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, "Json processing error: " + e.getMessage());
		} catch (Exception e) {
			return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		} finally {
			MDC.remove("tenantId");
			MDC.remove("gatewayId");
		}
	}
}

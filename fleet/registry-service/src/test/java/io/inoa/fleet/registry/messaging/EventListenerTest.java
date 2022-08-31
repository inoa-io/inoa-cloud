package io.inoa.fleet.registry.messaging;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayRepository;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("kafka: Connection Event")
public class EventListenerTest extends AbstractKafkaTest {

	@Inject
	public TestProducer topicClient;
	@Inject
	public GatewayRepository gatewayRepository;
	@Inject
	public ObjectMapper objectMapper;

	@DisplayName("connection status change to true")
	@Test
	void shouldChangeConnectionStatus() throws InterruptedException, JsonProcessingException {

		Gateway gateway = data.gateway();
		var payload = Map.of("cause", "connected", "remote-id", gateway.getGatewayId(), "source", "inoa-mqtt");
		String body = objectMapper.writeValueAsString(payload);
		topicClient.sendTestMessage(gateway.getGatewayId(), "test", "application/vnd.eclipse-hono-dc-notification+json",
				"-1", body);
		Awaitility.await("wait for gateway " + gateway.getGatewayId()).pollInterval(Duration.ofMillis(1000))
				.timeout(Duration.ofSeconds(30))
				.until(() -> gatewayRepository.findByGatewayId(gateway.getGatewayId()), gw -> {
					return gw.isPresent() && gw.get().getConnectionStatus().isConnected();
				});
		Optional<Gateway> optionalGateway = gatewayRepository.findByGatewayId(gateway.getGatewayId());
		assertTrue(optionalGateway.isPresent());
		assertTrue(optionalGateway.get().getConnectionStatus().isConnected());
		assertNotNull(optionalGateway.get().getConnectionStatus().getTimestamp());

	}
}

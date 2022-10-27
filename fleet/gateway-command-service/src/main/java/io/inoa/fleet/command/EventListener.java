package io.inoa.fleet.command;

import org.eclipse.hono.application.client.ApplicationClient;
import org.eclipse.hono.application.client.MessageContext;

import io.micronaut.context.annotation.Factory;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Factory
@RequiredArgsConstructor
public class EventListener {

	private final ApplicationClient<? extends MessageContext> honoClient;
	private final InoaConfig inoaConfig;

	@io.micronaut.runtime.event.annotation.EventListener
	void onStartup(ServerStartupEvent event) {
		for (String tenant : inoaConfig.getTenantIds()) {
			honoClient.createEventConsumer(tenant, msg -> {
				// handle command readiness notification
				if (msg.getTimeUntilDisconnectNotification().isPresent()) {
					log.info("ttl: {}", msg.getTimeUntilDisconnectNotification().get());
				}
			}, cause -> log.error("event consumer closed by remote", cause));
		}
	}
}

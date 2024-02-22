package io.inoa.fleet.broker.handler;

import io.inoa.fleet.broker.MqttGatewayIdentifier;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class SubscriptionHandler extends AbstractInterceptHandler {

	@Override
	public String getID() {
		return "subscription";
	}

	@Override
	public Class<?>[] getInterceptedMessageTypes() {
		return new Class<?>[] { InterceptSubscribeMessage.class, InterceptUnsubscribeMessage.class };
	}

	@Override
	public void onSubscribe(InterceptSubscribeMessage message) {
		MqttGatewayIdentifier.of(message.getUsername()).mdc(gateway -> log.debug("Gateway subscribed to {} with qos {}",
				message.getTopicFilter(),
				message.getRequestedQos()));
	}

	@Override
	public void onUnsubscribe(InterceptUnsubscribeMessage message) {
		MqttGatewayIdentifier.of(message.getUsername()).mdc(gateway -> log.debug("Gateway unsubscribed from {}",
				message.getTopicFilter()));
	}

	@Override
	public void onSessionLoopError(Throwable error) {
		log.warn("Got session loop error", error);
	}
}

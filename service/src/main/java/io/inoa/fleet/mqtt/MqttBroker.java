package io.inoa.fleet.mqtt;

import java.io.IOException;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import io.micronaut.context.annotation.Context;
import io.moquette.broker.ClientDescriptor;
import io.moquette.broker.ISslContextCreator;
import io.moquette.broker.RoutingResults;
import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.security.IAuthenticator;
import io.moquette.broker.security.IAuthorizatorPolicy;
import io.moquette.broker.subscriptions.Topic;
import io.moquette.interception.InterceptHandler;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import lombok.RequiredArgsConstructor;

@Context
@RequiredArgsConstructor
public class MqttBroker {

	public static final String COMMAND_TOPIC_SHORT_NAME = "c";
	public static final String COMMAND_TOPIC_LONG_NAME = "command";
	public static final String TELEMETRY_TOPIC_SHORT_NAME = "t";
	public static final String TELEMETRY_TOPIC_LONG_NAME = "telemetry";
	public static final String EVENT_TOPIC_SHORT_NAME = "e";
	public static final String EVENT_TOPIC_LONG_NAME = "event";
	public static final String COMMAND_RESPONSE_TOPIC_LONG_MATCHER = "command/+/+/res/+/+";
	public static final String COMMAND_RESPONSE_TOPIC_SHORT_MATCHER = "c/+/+/s/+/+";
	public static final String COMMAND_REQUEST_TOPIC_LONG_MATCHER = "command/+/+/req/+/cloudEventRpc";
	public static final String COMMAND_REQUEST_TOPIC_SHORT_MATCHER = "c/+/+/q/+/cloudEventRpc";

	private final IConfig config;
	private final IAuthenticator authenticator;
	private final IAuthorizatorPolicy authorizator;
	private final ISslContextCreator tls;
	private final List<InterceptHandler> handler;
	private final Server server = new Server();

	@PostConstruct
	public void start() throws IOException {
		server.startServer(config, handler, tls, authenticator, authorizator);
	}

	@PreDestroy
	public void close() {
		server.stopServer();
	}

	public RoutingResults internalPublish(MqttPublishMessage message, String clientId) {
		return server.internalPublish(message, clientId);
	}

	public static boolean matchesTopic(String topicName, String topicExpression) {
		return new Topic(topicName).match(new Topic(topicExpression));
	}

	public List<ClientDescriptor> getConnectedClients() {
		return server.listConnectedClients().stream().toList();
	}
}

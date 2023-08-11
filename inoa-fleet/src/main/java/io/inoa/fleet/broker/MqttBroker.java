package io.inoa.fleet.broker;

import java.util.List;

import io.micronaut.context.annotation.Context;
import io.moquette.broker.ISslContextCreator;
import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.security.IAuthenticator;
import io.moquette.broker.security.IAuthorizatorPolicy;
import io.moquette.interception.InterceptHandler;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

@Context
@RequiredArgsConstructor
public class MqttBroker {

	private final IConfig config;
	private final IAuthenticator authenticator;
	private final IAuthorizatorPolicy authorizator;
	private final ISslContextCreator tls;
	private final List<InterceptHandler> handler;
	private final Server server = new Server();

	@PostConstruct
	public void start() {
		server.startServer(config, handler, tls, authenticator, authorizator);
	}

	@PreDestroy
	public void close() {
		server.stopServer();
	}

	public void internalPublish(MqttPublishMessage message, String clientId) {
		server.internalPublish(message, clientId);
	}
}

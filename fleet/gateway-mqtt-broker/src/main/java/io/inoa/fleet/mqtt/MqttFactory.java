package io.inoa.fleet.mqtt;

import java.util.Properties;

import io.micronaut.context.annotation.Factory;
import io.moquette.BrokerConstants;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import jakarta.inject.Singleton;

@Factory
public class MqttFactory {

	@Singleton
	IConfig config(MqttProperties properties) {
		var config = new MemoryConfig(new Properties());
		config.setProperty(BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME, String.valueOf(false));
		config.setProperty(BrokerConstants.HOST_PROPERTY_NAME, properties.getHost());
		config.setProperty(BrokerConstants.PORT_PROPERTY_NAME, String.valueOf(properties.getPort()));
		return config;
	}
}

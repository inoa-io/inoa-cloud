package io.inoa.fleet.mqtt;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import io.moquette.BrokerConstants;
import lombok.Getter;
import lombok.Setter;

@Context
@ConfigurationProperties("inoa.mqtt")
@Getter
@Setter
public class MqttProperties {

	private String host = BrokerConstants.HOST;
	private int port = BrokerConstants.PORT;
}

package io.inoa.fleet.mqtt;

import java.security.cert.CertificateException;
import java.util.Properties;

import javax.net.ssl.SSLException;

import io.micronaut.context.annotation.Factory;
import io.moquette.BrokerConstants;
import io.moquette.broker.ISslContextCreator;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProtocols;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import jakarta.inject.Singleton;

@Factory
public class MqttFactory {

	@Singleton
	IConfig config(MqttProperties properties) {
		var config = new MemoryConfig(new Properties());
		config.setProperty(BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME, String.valueOf(false));
		config.setProperty(BrokerConstants.HOST_PROPERTY_NAME, properties.getHost());
		config.setProperty(BrokerConstants.PORT_PROPERTY_NAME, String.valueOf(properties.getPort()));
		config.setProperty(BrokerConstants.SSL_PORT_PROPERTY_NAME, String.valueOf(properties.getTls().getPort()));
		return config;
	}

	@Singleton
	SslContext sslContext(MqttProperties properties) throws SSLException, CertificateException {
		SslContextBuilder builder;
		var tls = properties.getTls();
		if (tls.isGenerateKey()) {
			var cert = new SelfSignedCertificate();
			builder = SslContextBuilder.forServer(cert.key(), cert.cert());
		} else {
			builder = SslContextBuilder.forServer(tls.getCert().toFile(), tls.getKey().toFile());
		}
		return builder.protocols(SslProtocols.TLS_v1_3).build();
	}

	@Singleton
	ISslContextCreator sslContextCreator(SslContext sslContext) {
		return () -> sslContext;
	}
}

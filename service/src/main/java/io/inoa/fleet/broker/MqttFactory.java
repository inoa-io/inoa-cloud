package io.inoa.fleet.broker;

import java.security.Security;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.net.ssl.SSLException;

import io.micronaut.context.annotation.Factory;
import io.moquette.broker.ISslContextCreator;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProtocols;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Factory
public class MqttFactory {

	@PostConstruct
	void bouncycastle() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	@Singleton
	IConfig config(MqttProperties properties) {
		log.info("Configuration: {}", properties);
		var config = new MemoryConfig(new Properties());
		config.setProperty(IConfig.ALLOW_ANONYMOUS_PROPERTY_NAME, String.valueOf(false));
		config.setProperty(IConfig.HOST_PROPERTY_NAME, properties.getHost());
		config.setProperty(IConfig.PORT_PROPERTY_NAME, String.valueOf(properties.getPort()));
		config.setProperty(IConfig.SSL_PORT_PROPERTY_NAME, String.valueOf(properties.getTls().getPort()));
		config.setProperty(IConfig.PERSISTENCE_ENABLED_PROPERTY_NAME, String.valueOf(false));
		// Immediate flush to avoid timing issues in tests or dependant clients
		config.setProperty(IConfig.BUFFER_FLUSH_MS_PROPERTY_NAME, String.valueOf(0));
		config.setProperty(IConfig.ENABLE_TELEMETRY_NAME, "false");
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
		return builder.protocols(SslProtocols.TLS_v1_2).build();
	}

	// TODO Dokumentieren warum das gebraucht wird.
	@Singleton
	ISslContextCreator sslContextCreator(SslContext sslContext) {
		return () -> sslContext;
	}
}

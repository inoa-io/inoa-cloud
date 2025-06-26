package io.inoa.fleet.mqtt;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

import javax.net.ssl.SSLException;
import javax.security.auth.x500.X500Principal;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.annotation.Factory;
import io.moquette.broker.ISslContextCreator;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProtocols;

/**
 * Factory for mqtt broker.
 *
 * @author stephan.schnabel@grayc.de
 */
@Factory
public class MqttFactory {

	private static final Logger log = LoggerFactory.getLogger(MqttFactory.class);

	@PostConstruct
	void log() {
		log.info("Starting controller: {}", getClass().getPackage().getName());
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
	SslContext sslContext(MqttProperties properties) throws SSLException, GeneralSecurityException, OperatorException {
		SslContextBuilder builder;
		var tls = properties.getTls();
		if (tls.isGenerateKey()) {
			var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(1024);
			var keyPair = keyPairGenerator.generateKeyPair();
			var subject = new X500Principal("CN=nope");
			var cert = new JcaX509CertificateConverter().getCertificate(new JcaX509v3CertificateBuilder(subject,
					BigInteger.ONE, new Date(0), new Date(Integer.MAX_VALUE), subject, keyPair.getPublic())
							.build(new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate())));
			builder = SslContextBuilder.forServer(keyPair.getPrivate(), cert);
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

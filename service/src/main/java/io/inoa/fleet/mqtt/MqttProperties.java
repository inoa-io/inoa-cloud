package io.inoa.fleet.mqtt;

import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.moquette.BrokerConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@ConfigurationProperties("inoa.mqtt")
@Slf4j
@Getter
@Setter
public class MqttProperties {

	private Tls tls = new Tls();

	@NotNull
	private String host = BrokerConstants.HOST;

	@Min(0)
	@Max(65535)
	private int port = BrokerConstants.PORT;

	private String dataPath = BrokerConstants.DEFAULT_PERSISTENT_PATH;

	@ConfigurationProperties("tls")
	@Getter
	@Setter
	public static class Tls {

		@Min(0)
		@Max(65535)
		private int port = 8883;

		/** Generate key if no key was defined. */
		private boolean generateKey = false;

		private Path key;
		private Path cert;
	}

	@AssertTrue(message = "configure tls cert or generate self signed cert for testing")
	public boolean getTlsValid() {
		if (tls.isGenerateKey()) {
			log.warn("Generate self signed tls cert. Do not use in Production!");
			return true;
		}
		if (tls.key == null || tls.cert == null) {
			log.error(
					"No tls cert/key provided, please set 'inoa.mqtt.tls.cert' and 'inoa.mqtt.tls.key'");
			return false;
		}
		if (!Files.isRegularFile(tls.key) || !Files.isReadable(tls.key)) {
			log.error("TLS key not found: {}", tls.key.toAbsolutePath());
			return false;
		}
		if (!Files.isRegularFile(tls.cert) || !Files.isReadable(tls.cert)) {
			log.error("TLS cert not found: {}", tls.cert.toAbsolutePath());
			return false;
		}
		return true;
	}

	@AssertTrue(message = "port for tcp and tls must differ")
	public boolean getPortsValid() {
		return tls.port != port;
	}
}

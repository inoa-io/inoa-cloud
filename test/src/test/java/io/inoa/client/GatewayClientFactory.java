package io.inoa.client;

import static io.inoa.junit.HttpAssertions.assert200;
import static io.inoa.junit.HttpAssertions.assertStatus;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;

import io.inoa.fleet.mqtt.HonoMqttClient;
import io.inoa.fleet.registry.gateway.AuthApiClient;
import io.inoa.fleet.registry.gateway.GatewayApiClient;
import io.inoa.fleet.registry.gateway.PropertiesApiClient;
import io.inoa.fleet.registry.gateway.TokenResponseVO;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class GatewayClientFactory {

	private final AuthApiClient authClient;
	private final GatewayApiClient gatewayClient;
	private final PropertiesApiClient propertiesClient;

	public GatewayClient get(String gatewayId, byte[] preSharedKey) {
		return new GatewayClient(gatewayId, preSharedKey);
	}

	@RequiredArgsConstructor
	public class GatewayClient {

		@Getter
		private final String tenantId = "inoa";
		@Getter
		private final String gatewayId;
		private final byte[] preSharedKey;

		private String token;
		private HonoMqttClient mqtt;

		// token

		public HttpResponse<TokenResponseVO> getTokenResponse() {

			// create jwt from gateway secret as hmac

			var now = Instant.now();
			var claims = new JWTClaimsSet.Builder()
					.audience("gateway-registry")
					.jwtID(UUID.randomUUID().toString())
					.issuer(gatewayId)
					.issueTime(Date.from(now))
					.notBeforeTime(Date.from(now))
					.expirationTime(Date.from(now.plusSeconds(10)));
			var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims.build());
			assertDoesNotThrow(() -> jwt.sign(new MACSigner(preSharedKey)), "failed to sign gateway jwt");

			// call registry

			return authClient.getToken("urn:ietf:params:oauth:grant-type:jwt-bearer", jwt.serialize());
		}

		public GatewayClient fetchToken() {
			var response = assertStatus(HttpStatus.OK, () -> getTokenResponse(), "failed to get token");
			token = response.body().getAccessToken();
			return this;
		}

		public SignedJWT getSignedJWT() {
			if (token == null) {
				fetchToken();
			}
			return (SignedJWT) assertDoesNotThrow(() -> JWTParser.parse(token), "failed to parse token");
		}

		private String bearer() {
			if (token == null) {
				fetchToken();
			}
			return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + token;
		}

		// registry

		public Map<String, String> getProperties() {
			return assert200(() -> propertiesClient.getProperties(bearer()), "failed to get properties");
		}

		public Map<String, String> setProperties(Map<String, String> properties) {
			return assert200(() -> propertiesClient.setProperties(bearer(), properties), "failed to get properties");
		}

		public Map<String, Object> getConfiguration() {
			return assert200(() -> gatewayClient.getConfiguration(bearer()), "failed to get configuration");
		}

		// mqtt

		public HonoMqttClient mqtt() {
			if (mqtt == null) {
				var mqttUrl = getConfiguration().get("mqtt.url");
				assertNotNull(mqttUrl, "mqtt.url is null");
				mqtt = assertDoesNotThrow(
						() -> new HonoMqttClient(mqttUrl.toString(), "inoa", gatewayId, preSharedKey),
						"failed to create hono mqtt client");
			}
			return mqtt;
		}
	}
}

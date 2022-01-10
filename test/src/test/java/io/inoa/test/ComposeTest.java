package io.inoa.test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.influxdb.client.InfluxDBClient;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.inoa.cnpm.tenant.management.TenantsApiClient;
import io.inoa.measurement.provisioner.grafana.GrafanaClient;
import io.inoa.test.infrastructure.AbstractTest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import lombok.SneakyThrows;

/**
 * Test docker-compose setup.
 */
@DisplayName("compose setup")
public class ComposeTest extends AbstractTest {

	@DisplayName("tenant: default issuer")
	@Test
	void tenantDefaultIssuer() {

		var admin = keycloak.realmInoa().token(INOA_UI, INOA_ADMIN);
		var tenant = tenantManagment.createTenant(admin);

		assertEquals(1, tenant.getIssuers().size(), "no default issuer found");
		var issuer = tenant.getIssuers().iterator().next();
		assertAll("issuer invalid",
				() -> assertEquals("compose", issuer.getName(), "name"),
				() -> assertEquals(new URL("http://keycloak:8080/realms/inoa"), issuer.getUrl(), "url"),
				() -> assertEquals(Set.of(INOA_BACKEND, "foo"), issuer.getServices(), "services"),
				() -> assertEquals(Duration.ofDays(7), issuer.getCacheDuration(), "cache"));
	}

	@DisplayName("tenant: secured")
	@Test
	void tenantSecured(TenantsApiClient client) {
		assertions.assert401(() -> client.findTenants());
	}

	@DisplayName("auth: jwk set")
	@Test
	void authJwkSet() {
		assertNotNull(auth.jwk());
	}

	@DisplayName("echo: secured")
	@Test
	void echoSecured(@Client("echo") HttpClient client) {
		assertions.assert403(() -> client.toBlocking().exchange("/"));
	}

	@DisplayName("grafana: available")
	@Test
	void grafana(GrafanaClient client) {
		assertFalse(client.findOrganizations().isEmpty(), "grafana master organization");
	}

	@DisplayName("influx: available")
	@Test
	void influx(InfluxDBClient client) {
		assertNotNull(client.getOrganizationsApi().findOrganizations(), "influx organizations");
	}

	@DisplayName("keycloak: client inoa-ui with user")
	@Test
	void keycloakClientInoaUiWithUser() {
		var claims = getClaims(keycloak.realmInoa().token(INOA_UI, INOA_ADMIN));
		assertAll("inoa-ui client misconfigured",
				() -> assertEquals(INOA_UI, claims.getClaim("azp"), "azp"),
				() -> assertEquals(List.of(), claims.getAudience(), "aud"),
				() -> assertNotNull(claims.getClaim("email"), "email"));
	}

	@DisplayName("keycloak: client inoa-ui without user")
	@Test
	void keycloakClientInoaUiWithoutUser() {
		var claims = getClaims(keycloak.realmInoa().token(INOA_UI));
		assertAll("inoa-ui client misconfigured",
				() -> assertEquals(INOA_UI, claims.getClaim("azp"), "azp"),
				() -> assertEquals(List.of(), claims.getAudience(), "aud"),
				() -> assertNull(claims.getClaim("email"), "email"));
	}

	@DisplayName("keycloak: client inoa-backend with user")
	@Test
	void keycloakClientInoaBackendWithUser() {
		var claims = getClaims(keycloak.realmInoa().token(INOA_BACKEND, INOA_ADMIN));
		assertAll("inoa-ui client misconfigured",
				() -> assertEquals(INOA_BACKEND, claims.getClaim("azp"), "azp"),
				() -> assertEquals(Set.of(INOA_BACKEND, "bar"), Set.copyOf(claims.getAudience()), "aud"),
				() -> assertNotNull(claims.getClaim("email"), "email"));
	}

	@DisplayName("keycloak: client inoa-backend without user")
	@Test
	void keycloakClientInoaBackendWithoutUser() {
		var claims = getClaims(keycloak.realmInoa().token(INOA_BACKEND));
		assertAll("inoa-ui client misconfigured",
				() -> assertEquals(INOA_BACKEND, claims.getClaim("azp"), "azp"),
				() -> assertEquals(Set.of(INOA_BACKEND, "bar"), Set.copyOf(claims.getAudience()), "aud"),
				() -> assertNull(claims.getClaim("email"), "email"));
	}

	@SneakyThrows
	private JWTClaimsSet getClaims(String token) {
		return SignedJWT.parse(token.split(" ")[1]).getJWTClaimsSet();
	}
}

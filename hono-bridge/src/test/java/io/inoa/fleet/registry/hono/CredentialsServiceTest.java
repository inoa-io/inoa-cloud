package io.inoa.fleet.registry.hono;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.hono.service.credentials.CredentialsService;
import org.eclipse.hono.util.CredentialsResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.inoa.fleet.registry.hono.config.InoaProperties;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Testcontainers
@ExtendWith({SpringExtension.class, VertxExtension.class})
@SpringBootTest
@Timeout(value = 5, timeUnit = TimeUnit.SECONDS)
@Execution(ExecutionMode.SAME_THREAD)
public class CredentialsServiceTest {

	WireMockServer wireMockServer;

	@Container
	private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
			DockerImageName.parse("confluentinc/cp-kafka:6.1.2"));

	@Autowired
	CredentialsService credentialsService;

	@Autowired
	InoaProperties inoaProperties;

	@DynamicPropertySource
	static void dataSourceProperties(DynamicPropertyRegistry registry) {
		registry.add("hono.kafka.commonClientConfig.bootstrap.servers", KAFKA_CONTAINER::getBootstrapServers);
	}

	@BeforeEach
	public void setup() {
		wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
		// wireMockServer.stubFor()

		wireMockServer.stubFor(get(urlEqualTo("/tenants/a910b71e-492e-4776-8a62-792d5a1f1780")).willReturn(
				aResponse().withHeader("Content-Type", "application/json").withBodyFile("json/tenant.json")));

		wireMockServer.stubFor(get(urlEqualTo("/gateways/b1f16184-04cf-11ec-9955-b780b05b0a68"))
				.withHeader("x-inoa-tenant", containing("a910b71e-492e-4776-8a62-792d5a1f1780")).willReturn(
						aResponse().withHeader("Content-Type", "application/json").withBodyFile("json/gateway.json")));

		wireMockServer.stubFor(get(urlEqualTo("/gateways/b1f16184-04cf-11ec-9955-b780b05b0a68/credentials"))
				.withHeader("x-inoa-tenant", containing("a910b71e-492e-4776-8a62-792d5a1f1780")).willReturn(aResponse()
						.withHeader("Content-Type", "application/json").withBodyFile("json/credentials.json")));

		wireMockServer.stubFor(post(urlEqualTo("/auth/realms/inoa/protocol/openid-connect/token"))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withBody("{\"access_token\": "
						+ "\"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJfUjZJWXNHdGdad2dkMDBMdHFRVnRBYlRmOVJTLWxGNUtJUVNBalBGcjVnIn0.eyJqdGkiOiI2N2QyMmQ0My0zMjQ2LTRhYWMtOWEwNi1lZDNlZWVkYmNkYmUiLCJleHAiOjE1Mjg3OTczNzgsIm5iZiI6MCwiaWF0IjoxNTI4Nzk3MDc4LCJpc3MiOiJodHRwczovL3Rlc3Qtbmc6ODg0My9hdXRoL3JlYWxtcy9kY200Y2hlIiwiYXVkIjoiY3VybCIsInN1YiI6ImVmNjRlYjRlLTBhNmQtNGYxMy1iZjQ0LWI2YjQxZWI5YTQ1ZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImN1cmwiLCJhdXRoX3RpbWUiOjAsInNlc3Npb25fc3RhdGUiOiIyMzNkNDAyYy05YmIwLTRjM2YtOGNmMy1mYjAwNTFjMjNhNGYiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInVtYV9hdXRob3JpemF0aW9uIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sImNsaWVudEhvc3QiOiIxOTIuMTY4LjIuMTc4IiwiY2xpZW50SWQiOiJjdXJsIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWN1cmwiLCJjbGllbnRBZGRyZXNzIjoiMTkyLjE2OC4yLjE3OCIsImVtYWlsIjoic2VydmljZS1hY2NvdW50LWN1cmxAcGxhY2Vob2xkZXIub3JnIn0.BdtmOKJNi-wzFBy-FUZu6_zRlukU81-yoXGl4YomEXMTLkK4AaUIsBO2Y3LjWt5vDbrki6RXZXNFbTEkDJMsMKXzur_xxAq5PzNE6q0QyEaTttsfrVETuzZMsU9r5Z0dfVSMIdAnpG7qgWMzETj2E9tOuZN1Mn7X8JRl6qQC0RLvl_TZcuRLElHoZbpvs2HiVRYkIhiG9Gn89cc6LT02wXdeGMccNx4jEyCY_YKhKsT6QNfzKmAKtiYdSF_arhlF6rlIf_HcCDjUIkgSQ_bY0LF5tA6FvEM2stCjO2YPjeVU2WrmQOYJyQ1FyvswiGBx2tutE-yLYdEmYwJknF2JuQ\", \"expires_in\": 120, \"token_type\": \"bearer\"}")));
		wireMockServer.start();
		inoaProperties.setGatewayRegistryHost("localhost");
		inoaProperties.setGatewayRegistryPort(wireMockServer.port());
		inoaProperties.setKeycloakUrl(String
				.format("http://localhost:%d/auth/realms/inoa/protocol/openid-connect/token", wireMockServer.port()));

	}

	@AfterEach
	public void teardown() {
		wireMockServer.stop();
	}

	@Test
	public void testGetCredentialsForGateway() throws InterruptedException {
		Future<CredentialsResult<JsonObject>> credentialsResultFuture = credentialsService.get(
				"a910b71e-492e-4776-8a62-792d5a1f1780", "password",
				"b1f16184-04cf-11ec-9955-b780b05b0a68");
		CountDownLatch l = new CountDownLatch(1);
		credentialsResultFuture.setHandler(event -> {
			l.countDown();
		});
		l.await(5, TimeUnit.SECONDS);
		assertTrue(credentialsResultFuture.succeeded());
		CredentialsResult<JsonObject> result = credentialsResultFuture.result();
		log.info("payload: {}", result.getPayload());
	}
}

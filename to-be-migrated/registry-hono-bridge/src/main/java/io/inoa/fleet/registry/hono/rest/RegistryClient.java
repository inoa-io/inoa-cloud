package io.inoa.fleet.registry.hono.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.inoa.fleet.registry.rest.CredentialTypeVO;
import io.inoa.fleet.registry.rest.CredentialVO;
import io.inoa.fleet.registry.rest.GatewayDetailVO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistryClient {

	private final RestTemplate restTemplate = new RestTemplate();
	private final RegistryProperties properties;

	public Optional<GatewayDetailVO> findGateway(String tenantId, String gatewayId) {
		return find(tenantId, "/gateways/" + gatewayId, GatewayDetailVO.class, null);
	}

	public Optional<CredentialVO> findPassword(String tenantId, String gatewayId) {

		var uri = "/gateways/" + gatewayId + "/credentials";
		var credential = find(tenantId, uri, null, new ParameterizedTypeReference<List<CredentialVO>>() {
		}).stream().flatMap(List::stream).filter(c -> c.getType() == CredentialTypeVO.PSK).findFirst();
		log.info("return {}", credential.get().getValue());
		log.info("return {}", new String(credential.get().getValue()));
		return credential;
	}

	private <T> Optional<T> find(String tenantId, String uri, Class<T> type, ParameterizedTypeReference<T> ref) {

		var url = "http://" + properties.getGatewayRegistryHost() + ":" + properties.getGatewayRegistryPort() + uri;
		var headers = new HttpHeaders();
		headers.setBearerAuth(getAccessToken());
		headers.add("x-tenant-id", tenantId);
		var request = new HttpEntity<>(null, headers);

		try {
			return Optional.of(type == null
					? restTemplate.exchange(url, HttpMethod.GET, request, ref).getBody()
					: restTemplate.exchange(url, HttpMethod.GET, request, type).getBody());
		} catch (HttpStatusCodeException e) {
			log.warn("Status {} for uri {} with tenant {}", e.getStatusCode(), uri, tenantId);
			return Optional.empty();
		} catch (RestClientException e) {
			log.error("Unable to get {}.", url, e);
			return Optional.empty();
		}
	}

	private String getAccessToken() {

		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		var payload = new LinkedMultiValueMap<String, String>();
		payload.add("client_id", properties.getClientId());
		payload.add("client_secret", properties.getClientSecret());
		payload.add("grant_type", "client_credentials");

		try {
			var result = restTemplate.exchange(properties.getKeycloakUrl(), HttpMethod.POST,
					new HttpEntity<>(payload, headers), AccessToken.class);
			if (result.getStatusCode() == HttpStatus.OK) {
				return result.getBody().getAccessToken();
			}
			log.warn("Failed to obtain token with status {} from keycloak.", result.getStatusCode());
		} catch (Exception e) {
			log.warn("Failed to obtain token from keycloak.", e);
		}
		return null;
	}

	@Data
	public static class AccessToken {
		@JsonProperty("access_token")
		private String accessToken;
	}
}

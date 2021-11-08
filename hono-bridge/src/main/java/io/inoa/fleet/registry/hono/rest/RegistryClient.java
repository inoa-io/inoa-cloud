package io.inoa.fleet.registry.hono.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.inoa.fleet.registry.rest.CredentialTypeVO;
import io.inoa.fleet.registry.rest.CredentialVO;
import io.inoa.fleet.registry.rest.GatewayDetailVO;
import io.inoa.fleet.registry.rest.SecretDetailPasswordVO;
import io.inoa.fleet.registry.rest.TenantVO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistryClient {

	private final RestTemplate restTemplate = new RestTemplate();
	private final RegistryProperties properties;

	public Optional<TenantVO> findTenant(String tenantId) {

		var headers = new HttpHeaders();
		headers.setBearerAuth(getAccessToken());
		var request = new HttpEntity<>(null, headers);
		var url = String.format("http://%s:%d/tenants/%s",
				properties.getGatewayRegistryHost(),
				properties.getGatewayRegistryPort(),
				tenantId);

		try {
			return Optional.of(restTemplate.exchange(url, HttpMethod.GET, request, TenantVO.class).getBody());
		} catch (NotFound e) {
			return Optional.empty();
		} catch (RestClientException e) {
			log.error("Unable to get tenant.", e);
			return Optional.empty();
		}
	}

	public Optional<GatewayDetailVO> findGateway(String tenantId, String gatewayId) {

		var headers = new HttpHeaders();
		headers.setBearerAuth(getAccessToken());
		headers.add("x-inoa-tenant", tenantId);
		var request = new HttpEntity<>(null, headers);
		var url = String.format("http://%s:%d/gateways/%s",
				properties.getGatewayRegistryHost(),
				properties.getGatewayRegistryPort(),
				gatewayId);

		try {
			return Optional.of(restTemplate.exchange(url, HttpMethod.GET, request, GatewayDetailVO.class).getBody());
		} catch (NotFound e) {
			return Optional.empty();
		} catch (RestClientException e) {
			log.error("Unable to get gateway.", e);
			return Optional.empty();
		}
	}

	public Optional<SecretDetailPasswordVO> findPassword(String tenantId, String gatewayId) {

		var headers = new HttpHeaders();
		headers.setBearerAuth(getAccessToken());
		headers.add("x-inoa-tenant", tenantId);
		var request = new HttpEntity<>(null, headers);
		var url = String.format("http://%s:%d/gateways/%s/credentials",
				properties.getGatewayRegistryHost(),
				properties.getGatewayRegistryPort(),
				gatewayId);

		List<CredentialVO> credentials = null;
		try {
			credentials = restTemplate
					.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<List<CredentialVO>>() {})
					.getBody();
		} catch (NotFound e) {
			return Optional.empty();
		} catch (RestClientException e) {
			log.error("Unable to get credentials.", e);
			return Optional.empty();
		}

		var credential = credentials.stream().filter(c -> c.getType() == CredentialTypeVO.PASSWORD).findFirst();
		if (credential.isEmpty() || credential.get().getSecrets().isEmpty()) {
			log.info("No password secret not found.");
			return Optional.empty();
		}
		var secret = credential.get().getSecrets().get(0);

		url = String.format("http://%s:%d/gateways/%s/credentials/%s/secrets/%s",
				properties.getGatewayRegistryHost(),
				properties.getGatewayRegistryPort(), gatewayId,
				credential.get().getCredentialId(), secret.getSecretId());
		try {
			return Optional
					.of(restTemplate.exchange(url, HttpMethod.GET, request, SecretDetailPasswordVO.class).getBody());
		} catch (NotFound e) {
			return Optional.empty();
		} catch (RestClientException e) {
			log.error("Unable to get credentials.", e);
			return Optional.empty();
		}
	}

	public String getAccessToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		LinkedMultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
		tokenRequest.add("client_id", properties.getClientId());
		tokenRequest.add("client_secret", properties.getClientSecret());
		tokenRequest.add("grant_type", "client_credentials");
		HttpEntity<?> request = new HttpEntity<>(tokenRequest, headers);
		try {
			ResponseEntity<AccessToken> result = restTemplate.exchange(properties.getKeycloakUrl(), HttpMethod.POST,
					request, AccessToken.class);
			if (result.getBody() == null) {
				return "";
			}
			return result.getBody().getAccessToken();
		} catch (Exception e) {
			log.error(e.getMessage());
			return "";
		}
	}

	@Data
	public static class AccessToken {
		@JsonProperty("access_token")
		private String accessToken;
	}

}

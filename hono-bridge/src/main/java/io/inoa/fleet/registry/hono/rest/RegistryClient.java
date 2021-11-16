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
		return find(tenantId, "/tenants/" + tenantId, TenantVO.class, null);
	}

	public Optional<GatewayDetailVO> findGateway(String tenantId, String gatewayId) {
		return find(tenantId, "/gateways/" + gatewayId, GatewayDetailVO.class, null);
	}

	public Optional<SecretDetailPasswordVO> findPassword(String tenantId, String gatewayId) {

		var uri = "/gateways/" + gatewayId + "/credentials";
		var credential = find(tenantId, uri, null, new ParameterizedTypeReference<List<CredentialVO>>() {})
				.stream().flatMap(List::stream)
				.filter(c -> c.getType() == CredentialTypeVO.PASSWORD)
				.findFirst();
		var secret = credential.stream().flatMap(c -> c.getSecrets().stream()).findFirst();
		if (secret.isEmpty()) {
			log.info("No password secret not found.");
			return Optional.empty();
		}

		uri += "/" + credential.get().getCredentialId() + "/secrets/" + secret.get().getSecretId();
		return find(tenantId, uri, SecretDetailPasswordVO.class, null);
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
		} catch (NotFound e) {
			log.warn("Not found {} for tenant {}", uri, tenantId);
			return Optional.empty();
		} catch (RestClientException e) {
			log.error("Unable to get {}.", url, e);
			return Optional.empty();
		}
	}

	private String getAccessToken() {
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

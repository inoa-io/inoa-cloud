package io.kokuwa.fleet.registry.hono;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import io.kokuwa.fleet.registry.hono.config.InoaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

	private final RestTemplate restTemplate;

	private final InoaProperties inoaProperties;

	public String getAccessToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		LinkedMultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
		tokenRequest.add("client_id", inoaProperties.getClientId());
		tokenRequest.add("client_secret", inoaProperties.getClientSecret());
		tokenRequest.add("grant_type", "client_credentials");
		HttpEntity<?> request = new HttpEntity<>(tokenRequest, headers);
		try {
			ResponseEntity<AccessToken> result = restTemplate.exchange(inoaProperties.getKeycloakUrl(), HttpMethod.POST,
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

}

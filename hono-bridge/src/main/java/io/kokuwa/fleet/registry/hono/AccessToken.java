package io.kokuwa.fleet.registry.hono;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AccessToken {
	@JsonProperty("access_token")
	private String accessToken;
}

package io.inoa.tenant.auth;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenidConfigurationVO {

	@JsonProperty("issuer")
	private String issuer;
	@JsonProperty("authorization_endpoint")
	private String authorizationEndpoint;
	@JsonProperty("token_endpoint")
	private String tokenEndpoint;
	@JsonProperty("token_introspection_endpoint")
	private String tokenIntrospectionEndpoint;
	@JsonProperty("userinfo_endpoint")
	private String userinfoEndpoint;

	@JsonProperty("end_session_endpoint")
	private String endSessionEndpoint;
	@JsonProperty("jwks_uri")
	private String jwksUri;
	@JsonProperty("check_session_iframe")
	private String checkSessionIframe;

	@Getter
	@JsonProperty("grant_types_supported")
	private List<String> grantTypesSupported = Arrays.asList("authorization_code", "implicit", "refresh_token",
			"password", "client_credentials");

	@Getter
	@JsonProperty("response_types_supported")
	private List<String> responseTypesSupported = Arrays.asList("code", "none", "id_token", "token", "id_token token",
			"code id_token", "code token", "code id_token token");
}

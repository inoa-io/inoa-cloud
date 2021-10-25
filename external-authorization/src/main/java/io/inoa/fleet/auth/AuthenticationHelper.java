package io.inoa.fleet.auth;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticationHelper {

	private final JwtDecoder jwtDecoder;
	public Jwt authenticate(String token) {
		return jwtDecoder.decode(token);
	}
}

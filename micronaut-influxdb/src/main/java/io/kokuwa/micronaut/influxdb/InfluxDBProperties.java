package io.kokuwa.micronaut.influxdb;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import com.influxdb.LogLevel;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Getter;
import lombok.Setter;

@Context
@ConfigurationProperties("influxdb")
@Getter
@Setter
public class InfluxDBProperties {

	@NotNull
	private String url;
	@NotNull
	private String bucket = "default";
	@NotNull
	private String org;
	@NotNull
	private LogLevel logLevel = LogLevel.BASIC;

	private char[] token;
	private String username;
	private char[] password;

	@AssertTrue
	boolean isAuthenticationSet() {
		return token != null || username != null && password != null;
	}
}

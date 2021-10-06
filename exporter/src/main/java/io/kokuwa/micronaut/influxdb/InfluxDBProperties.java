package io.kokuwa.micronaut.influxdb;

import com.influxdb.LogLevel;
import com.influxdb.client.InfluxDBClientOptions;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("influxdb")
@Getter
@Setter
public class InfluxDBProperties {

	private String url;
	private String username;
	private char[] password;
	private String bucket;
	private String org;
	private LogLevel logLevel;

	InfluxDBClientOptions getClientOptions() {
		var builder = InfluxDBClientOptions.builder();
		if (url != null) {
			builder.url(url);
		}
		if (username != null && password != null) {
			builder.authenticate(username, password);
		}
		if (org != null) {
			builder.org(org);
		}
		if (bucket != null) {
			builder.bucket(bucket);
		}
		if (logLevel != null) {
			builder.logLevel(logLevel);
		}
		return builder.build();
	}
}

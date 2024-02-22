package io.inoa.measurement.exporter.influx;

import com.influxdb.LogLevel;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

/**
 * Factory for InfluxDB client.
 *
 * @author stephan.schnabel@grayc.de
 */
@Factory
public class InfluxDBFactory {

	@Singleton
	InfluxDBClientOptions options(
			@Value("${influxdb.url:`http://influxdb:8086`}") String url,
			@Value("${influxdb.token:changeMe}") char[] token,
			@Value("${influxdb.organisation:default}") String organisation,
			@Value("${influxdb.bucket:default}") String bucket,
			@Value("${influxdb.log-level:NONE}") LogLevel logLevel) {
		return InfluxDBClientOptions
				.builder()
				.url(url).authenticateToken(token)
				.org(organisation)
				.bucket(bucket)
				.logLevel(logLevel).build();
	}

	@Context
	@Bean(preDestroy = "close")
	InfluxDBClient client(InfluxDBClientOptions options) {
		return InfluxDBClientFactory.create(options);
	}
}

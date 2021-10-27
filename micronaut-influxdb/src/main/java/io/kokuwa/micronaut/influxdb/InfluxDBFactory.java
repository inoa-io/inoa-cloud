package io.kokuwa.micronaut.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.internal.InfluxDBClientImpl;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import lombok.extern.slf4j.Slf4j;

@Requires(property = "influxdb.enabled", notEquals = "false")
@Requires(property = "influxdb.url")
@Factory
@Slf4j
public class InfluxDBFactory {

	@Bean
	InfluxDBClientOptions influxDBClientOptions(InfluxDBProperties properties) {
		var builder = InfluxDBClientOptions.builder();
		if (properties.getToken() != null) {
			builder.authenticateToken(properties.getToken());
		} else {
			log.warn("Do not use username/password in production, use token!");
			builder.authenticate(properties.getUsername(), properties.getPassword());
		}
		return builder
				.url(properties.getUrl())
				.org(properties.getOrg())
				.bucket(properties.getBucket())
				.logLevel(properties.getLogLevel())
				.build();
	}

	@Context
	@Bean(preDestroy = "close")
	InfluxDBClient influxDBClient(InfluxDBClientOptions options) {
		log.info("Connect to influx using url {}.", options.getUrl());
		InfluxDBClientImpl client = (InfluxDBClientImpl) InfluxDBClientFactory.create(options);
		log.info("Connected to influx: {}", client.health());
		return client;
	}
}

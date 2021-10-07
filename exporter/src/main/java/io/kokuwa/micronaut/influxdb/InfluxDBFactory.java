package io.kokuwa.micronaut.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;

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
		return properties.getClientOptions();
	}

	@Context
	@Bean(preDestroy = "close")
	InfluxDBClient influxDBClient(InfluxDBClientOptions options) {
		log.info("Connect to influx using url {}.", options.getUrl());
		var client = InfluxDBClientFactory.create(options);
		log.info("Connected to influx: {}", client.health());
		return client;
	}
}

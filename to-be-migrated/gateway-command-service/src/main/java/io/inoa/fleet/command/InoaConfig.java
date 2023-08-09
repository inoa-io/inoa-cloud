package io.inoa.fleet.command;

import java.util.ArrayList;
import java.util.List;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties("inoa")
public class InoaConfig {
	private List<String> tenantIds = new ArrayList<>();
	private String kafkaUrl = "kafka:9092";
	private String clientIdPrefix = "inoa.command";
	private String consumerGroupId = "inoa-command-application";
}

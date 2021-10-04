package io.inoa.fleet.command;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("inoa")
public class InoaConfig {
	private List<String> tenantIds = new ArrayList<>();
	private String kafkaUrl = "kafka-cluster-kafka-bootstrap.inoa-cloud.svc.cluster.local:9092";
}

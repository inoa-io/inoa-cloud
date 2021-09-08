package io.inoa.fleet.registry.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;

import lombok.Getter;
import lombok.Value;

@Singleton
@Getter
public class KafkaMessageStore {

	private final List<KafkaMessage> messages = new ArrayList<>();

	@Value
	public static class KafkaMessage {
		private final UUID tenantId;
		private final UUID gatewayId;
		private final String payload;
	}
}

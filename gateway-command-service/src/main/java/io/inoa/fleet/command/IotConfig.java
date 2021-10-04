package io.inoa.fleet.command;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.eclipse.hono.application.client.ApplicationClient;
import org.eclipse.hono.application.client.DownstreamMessage;
import org.eclipse.hono.application.client.MessageContext;
import org.eclipse.hono.application.client.kafka.impl.KafkaApplicationClientImpl;
import org.eclipse.hono.client.kafka.KafkaProducerConfigProperties;
import org.eclipse.hono.client.kafka.KafkaProducerFactory;
import org.eclipse.hono.client.kafka.consumer.KafkaConsumerConfigProperties;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class IotConfig {
	private final InoaConfig inoaConfig;

	@Bean
	public Vertx vertx() {
		return Vertx.vertx();
	}

	@Bean
	ApplicationClient<? extends MessageContext> honoClient() throws InterruptedException {
		var client = createKafkaApplicationClient(vertx());

		final CountDownLatch latch = new CountDownLatch(1);

		client.start().onSuccess(v -> latch.countDown());

		latch.await();
		return client;
	}

	@Bean
	ApplicationRunner applicationRunner(ApplicationClient<? extends MessageContext> honoClient) {
		return args -> {
			for (String tenant : inoaConfig.getTenantIds()) {
				honoClient.createEventConsumer(tenant, msg -> {
					// handle command readiness notification
					// msg.getTimeUntilDisconnectNotification().ifPresent(this::handleCommandReadinessNotification);
					if (msg.getTimeUntilDisconnectNotification().isPresent()) {
						log.info("ttl: {}", msg.getTimeUntilDisconnectNotification().get());
					}
					handleEventMessage(msg);
				}, cause -> log.error("event consumer closed by remote", cause));
			}
		};
	}

	private void handleEventMessage(final DownstreamMessage<? extends MessageContext> msg) {
		log.debug("received event [tenant: {}, device: {}, content-type: {}]: [{}].", msg.getTenantId(),
				msg.getDeviceId(), msg.getContentType(), msg.getPayload());
	}

	private ApplicationClient<? extends MessageContext> createKafkaApplicationClient(Vertx vertx) {

		final Map<String, String> properties = new HashMap<>();
		properties.put("bootstrap.servers", inoaConfig.getKafkaUrl());

		final KafkaConsumerConfigProperties consumerConfig = new KafkaConsumerConfigProperties();
		consumerConfig.setCommonClientConfig(properties);
		consumerConfig.setDefaultClientIdPrefix(inoaConfig.getClientIdPrefix());
		consumerConfig.setConsumerConfig(Map.of("group.id", inoaConfig.getConsumerGroupId()));

		final KafkaProducerConfigProperties producerConfig = new KafkaProducerConfigProperties();
		producerConfig.setCommonClientConfig(properties);
		producerConfig.setDefaultClientIdPrefix(inoaConfig.getClientIdPrefix());

		final KafkaProducerFactory<String, Buffer> producerFactory = KafkaProducerFactory.sharedProducerFactory(vertx);
		return new KafkaApplicationClientImpl(vertx, consumerConfig, producerFactory, producerConfig);
	}
}

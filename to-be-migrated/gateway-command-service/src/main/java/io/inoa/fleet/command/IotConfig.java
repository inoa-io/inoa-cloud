package io.inoa.fleet.command;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.eclipse.hono.application.client.ApplicationClient;
import org.eclipse.hono.application.client.DownstreamMessage;
import org.eclipse.hono.application.client.MessageContext;
import org.eclipse.hono.application.client.kafka.impl.KafkaApplicationClientImpl;
import org.eclipse.hono.client.kafka.CommonKafkaClientConfigProperties;
import org.eclipse.hono.client.kafka.consumer.MessagingKafkaConsumerConfigProperties;
import org.eclipse.hono.client.kafka.producer.CachingKafkaProducerFactory;
import org.eclipse.hono.client.kafka.producer.KafkaProducerFactory;
import org.eclipse.hono.client.kafka.producer.MessagingKafkaProducerConfigProperties;

import io.micronaut.context.annotation.Factory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Factory

@RequiredArgsConstructor
public class IotConfig {
	@Singleton
	public Vertx vertx() {
		return Vertx.vertx();
	}

	@Singleton
	ApplicationClient<? extends MessageContext> honoClient(InoaConfig inoaConfig) throws InterruptedException {
		var client = createKafkaApplicationClient(vertx(), inoaConfig);

		final CountDownLatch latch = new CountDownLatch(1);

		client.start().onSuccess(v -> latch.countDown());

		latch.await();
		return client;
	}

	private void handleEventMessage(final DownstreamMessage<? extends MessageContext> msg) {
		log.debug("received event [tenant: {}, device: {}, content-type: {}]: [{}].", msg.getTenantId(),
				msg.getDeviceId(), msg.getContentType(), msg.getPayload());
	}

	private ApplicationClient<? extends MessageContext> createKafkaApplicationClient(Vertx vertx,
			InoaConfig inoaConfig) {

		final Map<String, String> properties = new HashMap<>();
		properties.put("bootstrap.servers", inoaConfig.getKafkaUrl());

		final CommonKafkaClientConfigProperties commonClientConfig = new CommonKafkaClientConfigProperties();
		commonClientConfig.setCommonClientConfig(properties);

		final MessagingKafkaConsumerConfigProperties consumerConfig = new MessagingKafkaConsumerConfigProperties();
		consumerConfig.setCommonClientConfig(commonClientConfig);
		consumerConfig.setConsumerConfig(Map.of("group.id", inoaConfig.getConsumerGroupId()));

		final MessagingKafkaProducerConfigProperties producerConfig = new MessagingKafkaProducerConfigProperties();
		producerConfig.setCommonClientConfig(commonClientConfig);

		final KafkaProducerFactory<String, Buffer> producerFactory = CachingKafkaProducerFactory.sharedFactory(vertx);
		return new KafkaApplicationClientImpl(vertx, consumerConfig, producerFactory, producerConfig);
	}
}

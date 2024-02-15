package io.inoa.fleet.broker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import jakarta.inject.Singleton;

@Singleton
public class TestMqttListener implements IMqttMessageListener {

	private final Map<String, List<MqttMessage>> records = new HashMap<>();

	public void clear() {
		records.clear();
	}

	public MqttMessage await() {
		Awaitility.await().pollDelay(500, TimeUnit.MILLISECONDS).until(() -> !records.isEmpty());
		assertEquals(1, records.size(), "expected only one entry");
		assertEquals(1, records.entrySet().iterator().next().getValue().size(), "expected only one entry");
		return records.entrySet().iterator().next().getValue().get(0);
	}

	public MqttMessage await(String topic) {
		Awaitility.await().pollDelay(500, TimeUnit.MILLISECONDS)
				.until(() -> records.containsKey(topic) && !records.get(topic).isEmpty());
		assertEquals(1, records.get(topic).size(), "expected only one entry");
		return records.get(topic).get(0);
	}

	@Override
	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
		if (records.containsKey(topic)) {
			records.get(topic).add(mqttMessage);
		} else {
			List<MqttMessage> messages = new ArrayList<>();
			messages.add(mqttMessage);
			records.put(topic, messages);
		}
	}
}

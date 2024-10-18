package io.inoa.controller.translator.converter.common;

import io.inoa.controller.translator.AbstractTranslatorTest;
import io.inoa.messaging.TelemetryRawVO;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link JsonPathConverter}.
 *
 * @author stephan.schnabel@grayc.de
 */
@DisplayName("translator: converter json-path")
public class JsonPathConverterTest extends AbstractTranslatorTest {

  @Inject JsonPathConverter converter;

  @DisplayName("success")
  @Test
  void success() {
    var raw =
        TelemetryRawVO.of(
            "urn:shellyplug-s:0A1B2C:status",
            """
				{
					"wifi_sta": {
						"connected": true,
						"ssid": "landskron",
						"ip": "192.168.178.50",shplg-s
						"rssi": -63
					},
					"cloud": {
						"enabled": false,
						"connected": false
					},
					"mqtt": {
						"connected": false
					},
					"time": "10:55",
					"serial": 1,
					"has_update": false,
					"mac": "483FDA1D3A03",
					"relays": [
						{
							"ison": true,
							"has_timer": false,
							"overpower": false
						}
					],
					"meters": [
						{
							"power": 53.79,
							"is_valid": true,
							"timestamp": 1647255320,
							"counters": [
								46.361,
								0.000,
								0.000
							],
							"total": 46
						}
					],
					"temperature": 21.18,
					"overtemperature": false,
					"update": {
						"status": "unknown",
						"has_update": false,
						"new_version": "",
						"old_version": "20190516-073020/master@ea1b23db"
					},
					"ram_total": 50832,
					"ram_free": 40540,
					"fs_size": 233681,
					"fs_free": 171684,
					"uptime": 72
				}
				""");
    var messages = convert(converter, raw, "shellyplug-s", "status");
    assertCount(3, messages);
  }

  @DisplayName("invalid JSON input")
  @Test
  void invalidInput() {
    var raw =
        TelemetryRawVO.of(
            "urn:shellyplug-s:0A1B2C:status", "{\"wifi_sta\"::{\"connected\":true,\"ssid\":\"");
    var messages = convert(converter, raw, "shellyplug-s", "status");
    assertCount(0, messages);
  }
}

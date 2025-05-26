package io.inoa.measurement.translator.converter.common;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import jakarta.inject.Singleton;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import io.inoa.measurement.translator.TranslatorProperties;
import io.inoa.messaging.TelemetryRawVO;
import io.inoa.rest.TelemetryVO;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * This is a converter for JSON input strings based on JSON paths. One need to specify the datapoint
 * to json path correlation as config map in the sensor entry of the application.yaml. Each path
 * entry is mapped to a datapoint, so this converter is 1 sensor to N data points!
 *
 * @see "https://goessner.net/articles/JsonPath"
 */
@Singleton
public class JsonPathConverter extends CommonConverter {

	public static final String COUNTER_FAIL_PARSE = "translator_json_path_fail_parse";
	public static final String COUNTER_FAIL_PATH = "translator_json_path_fail_path";
	public static final String COUNTER_FAIL_NAN = "translator_json_path_fail_nan";
	public static final String COUNTER_SUCCESS = "translator_modbus_success";

	public JsonPathConverter(TranslatorProperties properties, MeterRegistry meterRegistry) {
		super(properties, meterRegistry, "json-path");
	}

	@Override
	public Stream<TelemetryVO> convert(TelemetryRawVO raw, String type, String sensor) {

		var properties = get(type, sensor);
		if (!properties.isPresent()) {
			// No config means no data
			return Stream.empty();
		}

		var jsonInput = new String(raw.value());
		DocumentContext jsonContext;

		// Parse input JSON
		try {
			jsonContext = JsonPath.parse(jsonInput);
		} catch (InvalidJsonException e) {
			increment(type, COUNTER_FAIL_PARSE);
			log.debug("Unable to parse invalid JSON '{}': {}", jsonInput, e.getMessage());
			return Stream.empty();
		}

		// Iterate JSON paths and apply each of them
		var result = new ArrayList<TelemetryVO>();
		for (var datapoint : properties.get().getConfig().keySet()) {
			try {
				// Get JSON path expression from config entry and apply it to the input JSON
				var value = jsonContext.read(properties.get().getConfig().get(datapoint).toString());
				// Result can either be an array or a single data object
				if (value instanceof Iterable<?> array) {
					// Simply take the first result (by convention)
					result.add(
							convert(type, datapoint, Double.parseDouble(array.iterator().next().toString())));
				} else {
					result.add(convert(type, datapoint, Double.parseDouble(value.toString())));
				}
			} catch (ClassCastException | JsonPathException | NoSuchElementException e) {
				increment(type, COUNTER_FAIL_PATH);
				log.debug(
						"Exception while applying JSON path '{}' to JSON '{}': {}",
						properties.get().getConfig().get(datapoint).toString(),
						new String(raw.value()),
						e.getMessage());
			} catch (NumberFormatException e) {
				increment(type, COUNTER_FAIL_NAN);
				log.debug(
						"JSON path '{}' on JSON '{}' is not a number: {}",
						properties.get().getConfig().get(datapoint).toString(),
						new String(raw.value()),
						e.getMessage());
			}
			increment(type, COUNTER_SUCCESS);
		}
		return result.stream();
	}
}

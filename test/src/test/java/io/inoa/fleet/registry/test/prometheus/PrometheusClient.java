package io.inoa.fleet.registry.test.prometheus;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Get;
import lombok.Value;

/**
 * Base for prometheus clients.
 *
 * @author Stephan Schnabel
 */
public interface PrometheusClient {

	@Value
	class Metric {
		final String name;
		final Map<String, String> tags;
		final Double value;
	}

	@Get("/endpoints/prometheus")
	@Consumes(MediaType.TEXT_PLAIN)
	String scrapRaw();

	default Set<Metric> scrap() {
		return Stream.of(scrapRaw().split("[\\r\\n]+"))
				.filter(line -> !line.startsWith("#"))
				.map(line -> {
					var name = line.substring(0, line.contains("{") ? line.indexOf("{") : line.lastIndexOf(" "));
					var tags = line.contains("{")
							? Stream.of(line.substring(line.indexOf("{") + 1, line.indexOf("}")).split(","))
									.map(tag -> tag.split("="))
									.collect(Collectors.toMap(tag -> tag[0], tag -> tag[1].replace("\"", "")))
							: Map.<String, String>of();
					var value = Double.parseDouble(line.substring(line.lastIndexOf(" ")));
					return new Metric(name, tags, value);
				})
				.collect(Collectors.toSet());
	}

	default Double scrap(String name) {
		return scrap().stream()
				.filter(metric -> Objects.equals(metric.getName(), name))
				.mapToDouble(Metric::getValue)
				.sum();
	}

	default Double scrap(String name, String tag, String value) {
		return scrap().stream()
				.filter(metric -> Objects.equals(metric.getName(), name))
				.filter(metric -> Objects.equals(metric.getTags().get(tag), value))
				.mapToDouble(Metric::getValue)
				.sum();
	}

	default long scrapCounter(String name) {
		return scrap(name + "_total").intValue();
	}

	default long scrapCounter(String name, String tag, String value) {
		return scrap(name + "_total", tag, value).intValue();
	}
}
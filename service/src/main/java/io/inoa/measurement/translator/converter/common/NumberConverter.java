package io.inoa.measurement.translator.converter.common;

import io.inoa.measurement.translator.TranslatorProperties;
import io.inoa.messaging.TelemetryRawVO;
import io.inoa.rest.TelemetryVO;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.stream.Stream;

/** Value converter for {@link Number}. */
@Singleton
public class NumberConverter extends CommonConverter {

  public static final String CONFIG_KEY_FACTOR = "factor";

  public NumberConverter(TranslatorProperties properties, MeterRegistry meterRegistry) {
    super(properties, meterRegistry, "number");
  }

  @Override
  public Stream<TelemetryVO> convert(TelemetryRawVO raw, String type, String sensor) {
    var properties = get(type, sensor);
    var number = toDouble(raw.value());

    if (number.isEmpty()) {
      return Stream.empty();
    }

    if (properties.isPresent()
        && properties.get().getConfig() != null
        && !properties.get().getConfig().isEmpty()) {
      var config = properties.get().getConfig();
      if (config.containsKey(CONFIG_KEY_FACTOR)) {
        try {
          var factor = Double.parseDouble(config.get("factor").toString());
          number = Optional.of(number.get() * factor);
        } catch (NumberFormatException e) {
          log.debug(
              "Failed to parse factor for type {}:  {}.", type, config.get("factor").toString());
          return Stream.empty();
        }
      }
    }

    return number.stream().map(value -> convert(type, sensor, value));
  }
}

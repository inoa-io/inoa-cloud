package io.inoa.measurement.translator.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConverter implements Converter {

  protected final Logger log = LoggerFactory.getLogger(getClass());
  protected final ObjectMapper mapper = new ObjectMapper();

  protected Optional<Double> toDouble(byte[] data) {
    try {
      return Optional.of(Double.parseDouble(new String(data)));
    } catch (NumberFormatException e) {
      log.debug("Failed to parse number {}.", new String(data));
      return Optional.empty();
    }
  }

  protected Optional<JsonNode> toJsonNode(byte[] data) {
    try {
      return Optional.of(mapper.readValue(data, JsonNode.class));
    } catch (IOException e) {
      log.debug("Failed to parse json {}.", new String(data));
      return Optional.empty();
    }
  }
}

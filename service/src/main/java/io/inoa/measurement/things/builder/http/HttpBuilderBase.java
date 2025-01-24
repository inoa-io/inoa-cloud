package io.inoa.measurement.things.builder.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.inoa.measurement.things.builder.modbus.Utils;
import io.inoa.measurement.things.domain.Thing;

public abstract class HttpBuilderBase {

  protected abstract ObjectMapper getObjectMapper();

  /**
   * Creates JSON structure for the datapoint from the given thing.
   *
   * @param serial serial of the thing
   * @param thingTypeReference type of the thing
   * @param thing thing reference
   * @param uri the uri the poll
   * @return JSON structure of the datapoint
   * @deprecated Use {@link #createHttpGetJsonNode(String, String, Thing, String)}.
   */
  @Deprecated(forRemoval = true)
  protected ObjectNode createHttpGetJsonNodeLegacy(
      String serial, String thingTypeReference, Thing thing, String uri) {
    ObjectNode node = getObjectMapper().createObjectNode();
    ObjectNode header = getObjectMapper().createObjectNode();
    header.put("id", Utils.buildUrn(serial, thingTypeReference, "status"));
    header.put("name", thing.getName());
    header.put("type", "HTTP_GET");
    header.put("interval", 30000);
    node.set("header", header);
    node.put("uri", uri);
    return node;
  }

  /**
   * Creates JSON structure for the datapoint from the given thing.
   *
   * @param serial serial of the thing
   * @param thingTypeReference type of the thing
   * @param thing thing reference
   * @param uri the uri the poll
   * @return JSON structure of the datapoint
   */
  protected ObjectNode createHttpGetJsonNode(
      String serial, String thingTypeReference, Thing thing, String uri) {
    ObjectNode node = getObjectMapper().createObjectNode();
    node.put("id", Utils.buildUrn(serial, thingTypeReference, "status"));
    node.put("name", thing.getName());
    node.put("type", "HTTP_GET");
    node.put("interval", 30000);
    node.put("enabled", true);
    node.put("uri", uri);
    return node;
  }
}

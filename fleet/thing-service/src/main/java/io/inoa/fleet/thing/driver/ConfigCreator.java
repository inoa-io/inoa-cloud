package io.inoa.fleet.thing.driver;

import com.fasterxml.jackson.databind.node.ArrayNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingType;

public interface ConfigCreator {
	ArrayNode build(Thing thing, ThingType thingType);
}

package io.inoa.measurement.thingtypes;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.inoa.thingtypes.ThingTypeDefinition;
import jakarta.inject.Singleton;

@Singleton
public class ThingTypeRegistry {

	private static final Logger log = LoggerFactory.getLogger(ThingTypeRegistry.class);

	private final ServiceLoader<ThingTypeDefinition> thingTypeDefinitions;

	private final Map<String, ThingTypeDefinition> thingTypeDefinitionMap = new HashMap<>();

	public ThingTypeRegistry() {
		this.thingTypeDefinitions = ServiceLoader.load(ThingTypeDefinition.class);
		reload();
	}

	public void reload() {
		this.thingTypeDefinitions.reload();
		thingTypeDefinitions.stream().forEach(thingTypeDefinitionProvider -> {
			ThingTypeDefinition definition = thingTypeDefinitionProvider.get();
			if (thingTypeDefinitionMap.containsKey(definition.getName())) {
				log.warn("Thing type {} already registered. Will not import instance from class {}.",
								definition.getName(),
								definition.getClass().getCanonicalName());
			} else {
				thingTypeDefinitionMap.put(definition.getName(), definition);
			}
		});
		log.info("Thing type registry reloaded.");
		log.info("These are the current ThingTypes {}.", thingTypeDefinitionMap.keySet());
	}
}

package io.inoa.thingtypes.shelly.plugs;

import io.inoa.thingtypes.ThingTypeDefinition;

public class ShellyPlugSThingTypeDefinition implements ThingTypeDefinition {
	@Override
	public String getName() {
		return "shelly-plugs";
	}

	@Override
	public String getDescription() {
		return "This is the INOA ThingTypeDefinition for Shelly Plugs.";
	}

	@Override
	public String getCategory() {
		return "smart-plug";
	}
}

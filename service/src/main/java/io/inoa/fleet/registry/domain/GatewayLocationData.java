package io.inoa.fleet.registry.domain;

import io.micronaut.data.annotation.*;
import lombok.Data;

/**
 * Location data for a gateway
 *
 * @author Ronny Schlegel
 */
@Data
@Embeddable
public class GatewayLocationData {
	private String houseNumber;
	private String road;
	private String neighbourhood;
	private String suburb;
	private String cityDistrict;
	private String city;
	private String state;
	private String postcode;
	private String country;
	private String countryCode;
	private Double latitude;
	private Double longitude;
}

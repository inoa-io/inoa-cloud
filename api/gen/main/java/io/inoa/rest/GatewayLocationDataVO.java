package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class GatewayLocationDataVO {

	public static final java.lang.String JSON_PROPERTY_HOUSE_NUMBER = "house_number";
	public static final java.lang.String JSON_PROPERTY_ROAD = "road";
	public static final java.lang.String JSON_PROPERTY_NEIGHBOURHOOD = "neighbourhood";
	public static final java.lang.String JSON_PROPERTY_SUBURB = "suburb";
	public static final java.lang.String JSON_PROPERTY_CITY_DISTRICT = "city_district";
	public static final java.lang.String JSON_PROPERTY_CITY = "city";
	public static final java.lang.String JSON_PROPERTY_STATE = "state";
	public static final java.lang.String JSON_PROPERTY_POSTCODE = "postcode";
	public static final java.lang.String JSON_PROPERTY_COUNTRY = "country";
	public static final java.lang.String JSON_PROPERTY_COUNTRY_CODE = "country_code";
	public static final java.lang.String JSON_PROPERTY_LATITUDE = "latitude";
	public static final java.lang.String JSON_PROPERTY_LONGITUDE = "longitude";

	/** Building number */
	@jakarta.validation.constraints.Size(max = 10)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_HOUSE_NUMBER)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String houseNumber;

	/** Street or road name */
	@jakarta.validation.constraints.Size(max = 100)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ROAD)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String road;

	/** Neighborhood or local area name */
	@jakarta.validation.constraints.Size(max = 100)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NEIGHBOURHOOD)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String neighbourhood;

	/** Suburb name */
	@jakarta.validation.constraints.Size(max = 100)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_SUBURB)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String suburb;

	/** District within the city */
	@jakarta.validation.constraints.Size(max = 100)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CITY_DISTRICT)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String cityDistrict;

	/** City name */
	@jakarta.validation.constraints.Size(max = 100)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CITY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String city;

	/** State or province name */
	@jakarta.validation.constraints.Size(max = 100)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_STATE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String state;

	/** Postal or ZIP code */
	@jakarta.validation.constraints.Size(max = 20)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_POSTCODE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String postcode;

	/** Country name */
	@jakarta.validation.constraints.Size(max = 100)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_COUNTRY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String country;

	/** Two-letter ISO country code */
	@jakarta.validation.constraints.Size(max = 2)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_COUNTRY_CODE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String countryCode;

	/** Latitude coordinate in decimal degrees */
	@jakarta.validation.constraints.DecimalMin(value = "-90", inclusive = true)
	@jakarta.validation.constraints.DecimalMax(value = "90", inclusive = true)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_LATITUDE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Double latitude;

	/** Longitude coordinate in decimal degrees */
	@jakarta.validation.constraints.DecimalMin(value = "-180", inclusive = true)
	@jakarta.validation.constraints.DecimalMax(value = "180", inclusive = true)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_LONGITUDE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Double longitude;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		GatewayLocationDataVO other = (GatewayLocationDataVO) object;
		return java.util.Objects.equals(houseNumber, other.houseNumber)
				&& java.util.Objects.equals(road, other.road)
				&& java.util.Objects.equals(neighbourhood, other.neighbourhood)
				&& java.util.Objects.equals(suburb, other.suburb)
				&& java.util.Objects.equals(cityDistrict, other.cityDistrict)
				&& java.util.Objects.equals(city, other.city)
				&& java.util.Objects.equals(state, other.state)
				&& java.util.Objects.equals(postcode, other.postcode)
				&& java.util.Objects.equals(country, other.country)
				&& java.util.Objects.equals(countryCode, other.countryCode)
				&& java.util.Objects.equals(latitude, other.latitude)
				&& java.util.Objects.equals(longitude, other.longitude);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(houseNumber, road, neighbourhood, suburb, cityDistrict, city, state, postcode, country, countryCode, latitude, longitude);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("GatewayLocationDataVO[")
				.append("houseNumber=").append(houseNumber).append(",")
				.append("road=").append(road).append(",")
				.append("neighbourhood=").append(neighbourhood).append(",")
				.append("suburb=").append(suburb).append(",")
				.append("cityDistrict=").append(cityDistrict).append(",")
				.append("city=").append(city).append(",")
				.append("state=").append(state).append(",")
				.append("postcode=").append(postcode).append(",")
				.append("country=").append(country).append(",")
				.append("countryCode=").append(countryCode).append(",")
				.append("latitude=").append(latitude).append(",")
				.append("longitude=").append(longitude)
				.append("]")
				.toString();
	}

	// fluent

	public GatewayLocationDataVO houseNumber(java.lang.String newHouseNumber) {
		this.houseNumber = newHouseNumber;
		return this;
	}

	public GatewayLocationDataVO road(java.lang.String newRoad) {
		this.road = newRoad;
		return this;
	}

	public GatewayLocationDataVO neighbourhood(java.lang.String newNeighbourhood) {
		this.neighbourhood = newNeighbourhood;
		return this;
	}

	public GatewayLocationDataVO suburb(java.lang.String newSuburb) {
		this.suburb = newSuburb;
		return this;
	}

	public GatewayLocationDataVO cityDistrict(java.lang.String newCityDistrict) {
		this.cityDistrict = newCityDistrict;
		return this;
	}

	public GatewayLocationDataVO city(java.lang.String newCity) {
		this.city = newCity;
		return this;
	}

	public GatewayLocationDataVO state(java.lang.String newState) {
		this.state = newState;
		return this;
	}

	public GatewayLocationDataVO postcode(java.lang.String newPostcode) {
		this.postcode = newPostcode;
		return this;
	}

	public GatewayLocationDataVO country(java.lang.String newCountry) {
		this.country = newCountry;
		return this;
	}

	public GatewayLocationDataVO countryCode(java.lang.String newCountryCode) {
		this.countryCode = newCountryCode;
		return this;
	}

	public GatewayLocationDataVO latitude(java.lang.Double newLatitude) {
		this.latitude = newLatitude;
		return this;
	}

	public GatewayLocationDataVO longitude(java.lang.Double newLongitude) {
		this.longitude = newLongitude;
		return this;
	}

	// getter/setter

	public java.lang.String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(java.lang.String newHouseNumber) {
		this.houseNumber = newHouseNumber;
	}

	public java.lang.String getRoad() {
		return road;
	}

	public void setRoad(java.lang.String newRoad) {
		this.road = newRoad;
	}

	public java.lang.String getNeighbourhood() {
		return neighbourhood;
	}

	public void setNeighbourhood(java.lang.String newNeighbourhood) {
		this.neighbourhood = newNeighbourhood;
	}

	public java.lang.String getSuburb() {
		return suburb;
	}

	public void setSuburb(java.lang.String newSuburb) {
		this.suburb = newSuburb;
	}

	public java.lang.String getCityDistrict() {
		return cityDistrict;
	}

	public void setCityDistrict(java.lang.String newCityDistrict) {
		this.cityDistrict = newCityDistrict;
	}

	public java.lang.String getCity() {
		return city;
	}

	public void setCity(java.lang.String newCity) {
		this.city = newCity;
	}

	public java.lang.String getState() {
		return state;
	}

	public void setState(java.lang.String newState) {
		this.state = newState;
	}

	public java.lang.String getPostcode() {
		return postcode;
	}

	public void setPostcode(java.lang.String newPostcode) {
		this.postcode = newPostcode;
	}

	public java.lang.String getCountry() {
		return country;
	}

	public void setCountry(java.lang.String newCountry) {
		this.country = newCountry;
	}

	public java.lang.String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(java.lang.String newCountryCode) {
		this.countryCode = newCountryCode;
	}

	public java.lang.Double getLatitude() {
		return latitude;
	}

	public void setLatitude(java.lang.Double newLatitude) {
		this.latitude = newLatitude;
	}

	public java.lang.Double getLongitude() {
		return longitude;
	}

	public void setLongitude(java.lang.Double newLongitude) {
		this.longitude = newLongitude;
	}
}

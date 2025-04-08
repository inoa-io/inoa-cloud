/**
 * Inoa API
 *
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


/**
 * Detailed location information for the gateway
 */
export interface GatewayLocationDataVO { 
    /**
     * Building number
     */
    house_number?: string;
    /**
     * Street or road name
     */
    road?: string;
    /**
     * Neighborhood or local area name
     */
    neighbourhood?: string;
    /**
     * Suburb name
     */
    suburb?: string;
    /**
     * District within the city
     */
    city_district?: string;
    /**
     * City name
     */
    city?: string;
    /**
     * State or province name
     */
    state?: string;
    /**
     * Postal or ZIP code
     */
    postcode?: string;
    /**
     * Country name
     */
    country?: string;
    /**
     * Two-letter ISO country code
     */
    country_code?: string;
    /**
     * Latitude coordinate in decimal degrees
     */
    latitude?: number;
    /**
     * Longitude coordinate in decimal degrees
     */
    longitude?: number;
}


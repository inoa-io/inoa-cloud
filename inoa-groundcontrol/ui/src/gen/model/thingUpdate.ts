/**
 * Inoa Measurement API
 * Definitions for Inoa Measurement.
 *
 * The version of the OpenAPI document: ${project.version}
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


/**
 * Thing to update.
 */
export interface ThingUpdate { 
    /**
     * Name.
     */
    name: string;
    /**
     * Id as technical reference (never changes).
     */
    gateway_id?: string;
    /**
     * External thing type id
     */
    thing_type_id: string;
    /**
     * config
     */
    config?: { [key: string]: any; };
}


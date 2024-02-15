/**
 * Inoa Fleet API
 * Definitions for Inoa Fleet.
 *
 * The version of the OpenAPI document: ${project.version}
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


/**
 * Inoa payload.
 */
export interface Telemetry { 
    /**
     * Id of tenant.
     */
    tenant_id: string;
    /**
     * Id of gateway @ tenant.
     */
    gateway_id: string;
    /**
     * TODO
     */
    urn: string;
    /**
     * TODO
     */
    device_type: string;
    /**
     * TODO
     */
    device_id: string;
    /**
     * TODO
     */
    sensor: string;
    /**
     * Timestamp of measurement value.
     */
    timestamp: string;
    /**
     * Value of measurement.
     */
    value: number;
    /**
     * Additional stuff describing this measurement.
     */
    ext?: { [key: string]: string; };
}


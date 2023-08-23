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
import { PropertyDefinition } from './propertyDefinition';


/**
 * Thing Type Channel with fields.
 */
export interface ThingTypeChannelCreate { 
    /**
     * Name.
     */
    name: string;
    /**
     * key for the channel type
     */
    key?: string;
    properties?: Array<PropertyDefinition>;
}


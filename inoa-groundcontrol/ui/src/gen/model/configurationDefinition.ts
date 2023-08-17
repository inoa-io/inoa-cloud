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
import { ConfigurationType } from './configurationType';


/**
 * Defines a configuration.
 */
export interface ConfigurationDefinition { 
    /**
     * Key for configuration.
     */
    key: string;
    type: ConfigurationType;
    /**
     * Describes a configuration.
     */
    description?: string;
}
export namespace ConfigurationDefinition {
}



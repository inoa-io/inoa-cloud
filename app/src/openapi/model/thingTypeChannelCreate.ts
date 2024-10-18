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
import { PropertyDefinitionVO } from "./propertyDefinition";

/**
 * Thing Type Channel with fields.
 */
export interface ThingTypeChannelCreateVO {
	/**
	 * Name.
	 */
	name: string;
	/**
	 * key for the channel type
	 */
	key?: string;
	properties?: Array<PropertyDefinitionVO>;
}

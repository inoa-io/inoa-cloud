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
 * Thing with fields.
 */
export interface ThingVO {
	/**
	 * Id as technical reference (never changes).
	 */
	id: string;
	/**
	 * Name.
	 */
	name: string;
	/**
	 * Id as technical reference (never changes).
	 */
	gateway_id?: string;
	/**
	 * thing_type_id
	 */
	thing_type_id?: string;
	/**
	 * config
	 */
	config?: { [key: string]: any };
	/**
	 * Common timestamp for created/updated timestamps.
	 */
	created: string;
	/**
	 * Common timestamp for created/updated timestamps.
	 */
	updated: string;
}

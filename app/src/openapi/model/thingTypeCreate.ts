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
 * User to create.
 */
export interface ThingTypeCreateVO {
	/**
	 * Name.
	 */
	name: string;
	/**
	 * Category.
	 */
	category?: string;
	/**
	 * Id as technical reference (never changes).
	 */
	thing_type_id?: string;
	/**
	 * json_schema
	 */
	json_schema?: { [key: string]: any };
}

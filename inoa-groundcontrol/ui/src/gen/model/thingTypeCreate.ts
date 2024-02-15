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
export interface ThingTypeCreate { 
    /**
     * Name.
     */
    name: string;
    /**
     * External thing type Id
     */
    thing_type_id?: string;
    /**
     * json_schema
     */
    json_schema?: { [key: string]: any; };
    /**
     * ui_layout
     */
    ui_layout?: Array<{ [key: string]: any; }>;
}


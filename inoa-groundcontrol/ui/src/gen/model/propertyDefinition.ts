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


export interface PropertyDefinition { 
    /**
     * Name.
     */
    name?: string;
    /**
     * key for the property
     */
    key?: string;
    /**
     * input type for the frontend
     */
    input_type?: PropertyDefinition.InputTypeEnum;
}
export namespace PropertyDefinition {
    export type InputTypeEnum = 'text' | 'number';
    export const InputTypeEnum = {
        Text: 'text' as InputTypeEnum,
        Number: 'number' as InputTypeEnum
    };
}



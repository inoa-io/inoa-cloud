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
 * Remote procedure call command
 */
export interface RpcCommand { 
    /**
     * ID of the command
     */
    id?: string;
    /**
     * TODO
     */
    method: string;
    /**
     * Parameters of the command as JSON object
     */
    params?: object;
}


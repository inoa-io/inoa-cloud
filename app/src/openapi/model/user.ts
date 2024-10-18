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
 * Current User.
 */
export interface UserVO { 
    /**
     * First name.
     */
    firstname: string;
    /**
     * Last name.
     */
    lastname: string;
    /**
     * Email.
     */
    email: string;
    /**
     * When does the current session end?
     */
    session_expires: string;
}


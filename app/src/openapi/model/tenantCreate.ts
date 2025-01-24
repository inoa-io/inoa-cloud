/**
 * Inoa API
 *
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


/**
 * Tenant to create.
 */
export interface TenantCreateVO { 
    /**
     * Id as tenant reference.
     */
    tenant_id: string;
    /**
     * Name of a tenant.
     */
    name: string;
    /**
     * Is tenant enabled
     */
    enabled: boolean;
    /**
     * Regular expression to force specific gateway IDs for this tenant
     */
    gateway_id_pattern: string;
}


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
import { GatewayStatusVO } from './gatewayStatus';
import { GatewayLocationDataVO } from './gatewayLocationData';


/**
 * Gateway with common fields.
 */
export interface GatewayVO { 
    /**
     * Id as technical reference (never changes).
     */
    gateway_id: string;
    /**
     * Human friendly description (can change).
     */
    name?: string;
    location?: GatewayLocationDataVO;
    /**
     * Flag if enabled or not.
     */
    enabled: boolean;
    /**
     * Common timestamp for created/updated timestamps.
     */
    created: string;
    /**
     * Common timestamp for created/updated timestamps.
     */
    updated: string;
    status?: GatewayStatusVO;
}


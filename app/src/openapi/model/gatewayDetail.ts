/**
 * Inoa API
 *
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { GatewayLocationDataVO } from './gatewayLocationData';


/**
 * Gateway with detailed fields.
 */
export interface GatewayDetailVO { 
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
     * Ids of groups where gateway is member.
     */
    group_ids: Set<string>;
    /**
     * Properties set by gateway.
     */
    properties: { [key: string]: string; };
    /**
     * Common timestamp for created/updated timestamps.
     */
    created: string;
    /**
     * Common timestamp for created/updated timestamps.
     */
    updated: string;
}


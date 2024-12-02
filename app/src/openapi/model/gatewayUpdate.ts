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
import { GatewayLocationDataVO } from './gatewayLocationData';


/**
 * Gateway to update.
 */
export interface GatewayUpdateVO { 
    /**
     * Human friendly description (can change).
     */
    name?: string;
    location?: GatewayLocationDataVO;
    /**
     * Flag if enabled or not.
     */
    enabled?: boolean;
    /**
     * Ids of groups where gateway is member.
     */
    group_ids?: Set<string>;
}


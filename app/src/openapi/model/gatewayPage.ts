/**
 * Inoa API
 *
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { GatewayVO } from './gateway';


/**
 * Page for gateway list.
 */
export interface GatewayPageVO { 
    /**
     * List of entries on page.
     */
    content: Array<GatewayVO>;
    /**
     * Total available entries.
     */
    total_size: number;
}


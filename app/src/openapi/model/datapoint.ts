/**
 * Inoa API
 *
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


export interface DatapointVO { 
    /**
     * Unique identifier
     */
    id: string;
    /**
     * Readable name
     */
    name: string;
    /**
     * Is datapoint enabled
     */
    enabled: boolean;
    /**
     * Poll interval in Seconds
     */
    interval: number;
    /**
     * Poll type
     */
    type: DatapointVO.TypeEnum;
    /**
     * Interface to poll
     */
    'interface'?: number;
    /**
     * Polling timeout
     */
    timeout?: number;
    /**
     * RS485 frame as hex string
     */
    frame?: string;
    /**
     * URI to poll
     */
    uri?: string;
}
export namespace DatapointVO {
    export type TypeEnum = 'RS485' | 'S0' | 'HTTP_GET';
    export const TypeEnum = {
        Rs485: 'RS485' as TypeEnum,
        S0: 'S0' as TypeEnum,
        HttpGet: 'HTTP_GET' as TypeEnum
    };
}



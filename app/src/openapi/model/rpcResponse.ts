/**
 * Inoa API
 *
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { RpcResponseErrorVO } from './rpcResponseError';


/**
 * Remote procedure call response
 */
export interface RpcResponseVO { 
    /**
     * ID of the command
     */
    id: string;
    error?: RpcResponseErrorVO;
    /**
     * Result of the command as JSON object
     */
    result?: object;
}


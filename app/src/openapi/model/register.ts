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
import { CredentialTypeVO } from "./credentialType";

/**
 * Contains all informations needed to create a new gateway.
 */
export interface RegisterVO {
	/**
	 * Id as technical reference (never changes).
	 */
	gateway_id: string;
	credential_type: CredentialTypeVO;
	/**
	 * Value for credential.
	 */
	credential_value: Blob;
}
export namespace RegisterVO {}

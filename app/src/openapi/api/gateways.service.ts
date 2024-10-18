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
/* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams, HttpResponse, HttpEvent, HttpParameterCodec, HttpContext } from "@angular/common/http";
import { CustomHttpParameterCodec } from "../encoder";
import { Observable } from "rxjs";

// @ts-ignore
import { GatewayCreateVO } from "../model/gatewayCreate";
// @ts-ignore
import { GatewayDetailVO } from "../model/gatewayDetail";
// @ts-ignore
import { GatewayPageVO } from "../model/gatewayPage";
// @ts-ignore
import { GatewayUpdateVO } from "../model/gatewayUpdate";
// @ts-ignore
import { MoveGatewayRequestVO } from "../model/moveGatewayRequest";

// @ts-ignore
import { BASE_PATH, COLLECTION_FORMATS } from "../variables";
import { Configuration } from "../configuration";

@Injectable({
	providedIn: "root"
})
export class GatewaysService {
	protected basePath = "http://fleet.127.0.0.1.nip.io:8080";
	public defaultHeaders = new HttpHeaders();
	public configuration = new Configuration();
	public encoder: HttpParameterCodec;

	constructor(protected httpClient: HttpClient, @Optional() @Inject(BASE_PATH) basePath: string | string[], @Optional() configuration: Configuration) {
		if (configuration) {
			this.configuration = configuration;
		}
		if (typeof this.configuration.basePath !== "string") {
			if (Array.isArray(basePath) && basePath.length > 0) {
				basePath = basePath[0];
			}

			if (typeof basePath !== "string") {
				basePath = this.basePath;
			}
			this.configuration.basePath = basePath;
		}
		this.encoder = this.configuration.encoder || new CustomHttpParameterCodec();
	}

	// @ts-ignore
	private addToHttpParams(httpParams: HttpParams, value: any, key?: string): HttpParams {
		if (typeof value === "object" && value instanceof Date === false) {
			httpParams = this.addToHttpParamsRecursive(httpParams, value);
		} else {
			httpParams = this.addToHttpParamsRecursive(httpParams, value, key);
		}
		return httpParams;
	}

	private addToHttpParamsRecursive(httpParams: HttpParams, value?: any, key?: string): HttpParams {
		if (value == null) {
			return httpParams;
		}

		if (typeof value === "object") {
			if (Array.isArray(value)) {
				(value as any[]).forEach((elem) => (httpParams = this.addToHttpParamsRecursive(httpParams, elem, key)));
			} else if (value instanceof Date) {
				if (key != null) {
					httpParams = httpParams.append(key, (value as Date).toISOString().substring(0, 10));
				} else {
					throw Error("key may not be null if value is Date");
				}
			} else {
				Object.keys(value).forEach((k) => (httpParams = this.addToHttpParamsRecursive(httpParams, value[k], key != null ? `${key}.${k}` : k)));
			}
		} else if (key != null) {
			httpParams = httpParams.append(key, value);
		} else {
			throw Error("key may not be null if value is not object or array");
		}
		return httpParams;
	}

	/**
	 * Create gateway
	 * Create gateway.
	 * @param gatewayCreateVO
	 * @param tenantSpecification If an issuer has multiple tenants granted, a specific tenant has to be given, so the method applies to only one tenant
	 * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
	 * @param reportProgress flag to report request and response progress.
	 */
	public createGateway(
		gatewayCreateVO: GatewayCreateVO,
		tenantSpecification?: string,
		observe?: "body",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<GatewayDetailVO>;
	public createGateway(
		gatewayCreateVO: GatewayCreateVO,
		tenantSpecification?: string,
		observe?: "response",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<HttpResponse<GatewayDetailVO>>;
	public createGateway(
		gatewayCreateVO: GatewayCreateVO,
		tenantSpecification?: string,
		observe?: "events",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<HttpEvent<GatewayDetailVO>>;
	public createGateway(
		gatewayCreateVO: GatewayCreateVO,
		tenantSpecification?: string,
		observe: any = "body",
		reportProgress: boolean = false,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<any> {
		if (gatewayCreateVO === null || gatewayCreateVO === undefined) {
			throw new Error("Required parameter gatewayCreateVO was null or undefined when calling createGateway.");
		}

		let localVarQueryParameters = new HttpParams({ encoder: this.encoder });
		if (tenantSpecification !== undefined && tenantSpecification !== null) {
			localVarQueryParameters = this.addToHttpParams(localVarQueryParameters, <any>tenantSpecification, "tenant_specification");
		}

		let localVarHeaders = this.defaultHeaders;

		let localVarCredential: string | undefined;
		// authentication (Keycloak) required
		localVarCredential = this.configuration.lookupCredential("Keycloak");
		if (localVarCredential) {
		}

		// authentication (BearerAuth) required
		localVarCredential = this.configuration.lookupCredential("BearerAuth");
		if (localVarCredential) {
			localVarHeaders = localVarHeaders.set("Authorization", "Bearer " + localVarCredential);
		}

		let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
		if (localVarHttpHeaderAcceptSelected === undefined) {
			// to determine the Accept header
			const httpHeaderAccepts: string[] = ["application/json"];
			localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
		}
		if (localVarHttpHeaderAcceptSelected !== undefined) {
			localVarHeaders = localVarHeaders.set("Accept", localVarHttpHeaderAcceptSelected);
		}

		let localVarHttpContext: HttpContext | undefined = options && options.context;
		if (localVarHttpContext === undefined) {
			localVarHttpContext = new HttpContext();
		}

		// to determine the Content-Type header
		const consumes: string[] = ["application/json"];
		const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
		if (httpContentTypeSelected !== undefined) {
			localVarHeaders = localVarHeaders.set("Content-Type", httpContentTypeSelected);
		}

		let responseType_: "text" | "json" | "blob" = "json";
		if (localVarHttpHeaderAcceptSelected) {
			if (localVarHttpHeaderAcceptSelected.startsWith("text")) {
				responseType_ = "text";
			} else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
				responseType_ = "json";
			} else {
				responseType_ = "blob";
			}
		}

		let localVarPath = `/gateways`;
		return this.httpClient.request<GatewayDetailVO>("post", `${this.configuration.basePath}${localVarPath}`, {
			context: localVarHttpContext,
			body: gatewayCreateVO,
			params: localVarQueryParameters,
			responseType: <any>responseType_,
			withCredentials: this.configuration.withCredentials,
			headers: localVarHeaders,
			observe: observe,
			reportProgress: reportProgress
		});
	}

	/**
	 * Delete gateway
	 * Delete gateway by id.
	 * @param gatewayId
	 * @param tenantSpecification If an issuer has multiple tenants granted, a specific tenant has to be given, so the method applies to only one tenant
	 * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
	 * @param reportProgress flag to report request and response progress.
	 */
	public deleteGateway(
		gatewayId: string,
		tenantSpecification?: string,
		observe?: "body",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: undefined; context?: HttpContext }
	): Observable<any>;
	public deleteGateway(
		gatewayId: string,
		tenantSpecification?: string,
		observe?: "response",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: undefined; context?: HttpContext }
	): Observable<HttpResponse<any>>;
	public deleteGateway(
		gatewayId: string,
		tenantSpecification?: string,
		observe?: "events",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: undefined; context?: HttpContext }
	): Observable<HttpEvent<any>>;
	public deleteGateway(
		gatewayId: string,
		tenantSpecification?: string,
		observe: any = "body",
		reportProgress: boolean = false,
		options?: { httpHeaderAccept?: undefined; context?: HttpContext }
	): Observable<any> {
		if (gatewayId === null || gatewayId === undefined) {
			throw new Error("Required parameter gatewayId was null or undefined when calling deleteGateway.");
		}

		let localVarQueryParameters = new HttpParams({ encoder: this.encoder });
		if (tenantSpecification !== undefined && tenantSpecification !== null) {
			localVarQueryParameters = this.addToHttpParams(localVarQueryParameters, <any>tenantSpecification, "tenant_specification");
		}

		let localVarHeaders = this.defaultHeaders;

		let localVarCredential: string | undefined;
		// authentication (Keycloak) required
		localVarCredential = this.configuration.lookupCredential("Keycloak");
		if (localVarCredential) {
		}

		// authentication (BearerAuth) required
		localVarCredential = this.configuration.lookupCredential("BearerAuth");
		if (localVarCredential) {
			localVarHeaders = localVarHeaders.set("Authorization", "Bearer " + localVarCredential);
		}

		let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
		if (localVarHttpHeaderAcceptSelected === undefined) {
			// to determine the Accept header
			const httpHeaderAccepts: string[] = [];
			localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
		}
		if (localVarHttpHeaderAcceptSelected !== undefined) {
			localVarHeaders = localVarHeaders.set("Accept", localVarHttpHeaderAcceptSelected);
		}

		let localVarHttpContext: HttpContext | undefined = options && options.context;
		if (localVarHttpContext === undefined) {
			localVarHttpContext = new HttpContext();
		}

		let responseType_: "text" | "json" | "blob" = "json";
		if (localVarHttpHeaderAcceptSelected) {
			if (localVarHttpHeaderAcceptSelected.startsWith("text")) {
				responseType_ = "text";
			} else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
				responseType_ = "json";
			} else {
				responseType_ = "blob";
			}
		}

		let localVarPath = `/gateways/${this.configuration.encodeParam({
			name: "gatewayId",
			value: gatewayId,
			in: "path",
			style: "simple",
			explode: false,
			dataType: "string",
			dataFormat: undefined
		})}`;
		return this.httpClient.request<any>("delete", `${this.configuration.basePath}${localVarPath}`, {
			context: localVarHttpContext,
			params: localVarQueryParameters,
			responseType: <any>responseType_,
			withCredentials: this.configuration.withCredentials,
			headers: localVarHeaders,
			observe: observe,
			reportProgress: reportProgress
		});
	}

	/**
	 * Find gateway
	 * Find gateway by id.
	 * @param gatewayId
	 * @param tenantSpecification If an issuer has multiple tenants granted, a specific tenant has to be given, so the method applies to only one tenant
	 * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
	 * @param reportProgress flag to report request and response progress.
	 */
	public findGateway(
		gatewayId: string,
		tenantSpecification?: string,
		observe?: "body",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<GatewayDetailVO>;
	public findGateway(
		gatewayId: string,
		tenantSpecification?: string,
		observe?: "response",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<HttpResponse<GatewayDetailVO>>;
	public findGateway(
		gatewayId: string,
		tenantSpecification?: string,
		observe?: "events",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<HttpEvent<GatewayDetailVO>>;
	public findGateway(
		gatewayId: string,
		tenantSpecification?: string,
		observe: any = "body",
		reportProgress: boolean = false,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<any> {
		if (gatewayId === null || gatewayId === undefined) {
			throw new Error("Required parameter gatewayId was null or undefined when calling findGateway.");
		}

		let localVarQueryParameters = new HttpParams({ encoder: this.encoder });
		if (tenantSpecification !== undefined && tenantSpecification !== null) {
			localVarQueryParameters = this.addToHttpParams(localVarQueryParameters, <any>tenantSpecification, "tenant_specification");
		}

		let localVarHeaders = this.defaultHeaders;

		let localVarCredential: string | undefined;
		// authentication (Keycloak) required
		localVarCredential = this.configuration.lookupCredential("Keycloak");
		if (localVarCredential) {
		}

		// authentication (BearerAuth) required
		localVarCredential = this.configuration.lookupCredential("BearerAuth");
		if (localVarCredential) {
			localVarHeaders = localVarHeaders.set("Authorization", "Bearer " + localVarCredential);
		}

		let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
		if (localVarHttpHeaderAcceptSelected === undefined) {
			// to determine the Accept header
			const httpHeaderAccepts: string[] = ["application/json"];
			localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
		}
		if (localVarHttpHeaderAcceptSelected !== undefined) {
			localVarHeaders = localVarHeaders.set("Accept", localVarHttpHeaderAcceptSelected);
		}

		let localVarHttpContext: HttpContext | undefined = options && options.context;
		if (localVarHttpContext === undefined) {
			localVarHttpContext = new HttpContext();
		}

		let responseType_: "text" | "json" | "blob" = "json";
		if (localVarHttpHeaderAcceptSelected) {
			if (localVarHttpHeaderAcceptSelected.startsWith("text")) {
				responseType_ = "text";
			} else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
				responseType_ = "json";
			} else {
				responseType_ = "blob";
			}
		}

		let localVarPath = `/gateways/${this.configuration.encodeParam({
			name: "gatewayId",
			value: gatewayId,
			in: "path",
			style: "simple",
			explode: false,
			dataType: "string",
			dataFormat: undefined
		})}`;
		return this.httpClient.request<GatewayDetailVO>("get", `${this.configuration.basePath}${localVarPath}`, {
			context: localVarHttpContext,
			params: localVarQueryParameters,
			responseType: <any>responseType_,
			withCredentials: this.configuration.withCredentials,
			headers: localVarHeaders,
			observe: observe,
			reportProgress: reportProgress
		});
	}

	/**
	 * Find gateways
	 * Returns all gateways ordered by name.
	 * @param page Page number for pagination.
	 * @param size Page size for pagination.
	 * @param sort Sorting.
	 * @param filter Search in name. Supports wildcard *, is case insensitive.
	 * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
	 * @param reportProgress flag to report request and response progress.
	 */
	public findGateways(
		page?: number,
		size?: number,
		sort?: Array<string>,
		filter?: string,
		observe?: "body",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<GatewayPageVO>;
	public findGateways(
		page?: number,
		size?: number,
		sort?: Array<string>,
		filter?: string,
		observe?: "response",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<HttpResponse<GatewayPageVO>>;
	public findGateways(
		page?: number,
		size?: number,
		sort?: Array<string>,
		filter?: string,
		observe?: "events",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<HttpEvent<GatewayPageVO>>;
	public findGateways(
		page?: number,
		size?: number,
		sort?: Array<string>,
		filter?: string,
		observe: any = "body",
		reportProgress: boolean = false,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<any> {
		let localVarQueryParameters = new HttpParams({ encoder: this.encoder });
		if (page !== undefined && page !== null) {
			localVarQueryParameters = this.addToHttpParams(localVarQueryParameters, <any>page, "page");
		}
		if (size !== undefined && size !== null) {
			localVarQueryParameters = this.addToHttpParams(localVarQueryParameters, <any>size, "size");
		}
		if (sort) {
			sort.forEach((element) => {
				localVarQueryParameters = this.addToHttpParams(localVarQueryParameters, <any>element, "sort");
			});
		}
		if (filter !== undefined && filter !== null) {
			localVarQueryParameters = this.addToHttpParams(localVarQueryParameters, <any>filter, "filter");
		}

		let localVarHeaders = this.defaultHeaders;

		let localVarCredential: string | undefined;
		// authentication (Keycloak) required
		localVarCredential = this.configuration.lookupCredential("Keycloak");
		if (localVarCredential) {
		}

		// authentication (BearerAuth) required
		localVarCredential = this.configuration.lookupCredential("BearerAuth");
		if (localVarCredential) {
			localVarHeaders = localVarHeaders.set("Authorization", "Bearer " + localVarCredential);
		}

		let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
		if (localVarHttpHeaderAcceptSelected === undefined) {
			// to determine the Accept header
			const httpHeaderAccepts: string[] = ["application/json"];
			localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
		}
		if (localVarHttpHeaderAcceptSelected !== undefined) {
			localVarHeaders = localVarHeaders.set("Accept", localVarHttpHeaderAcceptSelected);
		}

		let localVarHttpContext: HttpContext | undefined = options && options.context;
		if (localVarHttpContext === undefined) {
			localVarHttpContext = new HttpContext();
		}

		let responseType_: "text" | "json" | "blob" = "json";
		if (localVarHttpHeaderAcceptSelected) {
			if (localVarHttpHeaderAcceptSelected.startsWith("text")) {
				responseType_ = "text";
			} else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
				responseType_ = "json";
			} else {
				responseType_ = "blob";
			}
		}

		let localVarPath = `/gateways`;
		return this.httpClient.request<GatewayPageVO>("get", `${this.configuration.basePath}${localVarPath}`, {
			context: localVarHttpContext,
			params: localVarQueryParameters,
			responseType: <any>responseType_,
			withCredentials: this.configuration.withCredentials,
			headers: localVarHeaders,
			observe: observe,
			reportProgress: reportProgress
		});
	}

	/**
	 * Update gateway-to-tenant association
	 * Moves the gateway from one tenant to another. One would need to have access grants for the source and the target tenant
	 * @param moveGatewayRequestVO
	 * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
	 * @param reportProgress flag to report request and response progress.
	 */
	public moveGateway(moveGatewayRequestVO: MoveGatewayRequestVO, observe?: "body", reportProgress?: boolean, options?: { httpHeaderAccept?: undefined; context?: HttpContext }): Observable<any>;
	public moveGateway(
		moveGatewayRequestVO: MoveGatewayRequestVO,
		observe?: "response",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: undefined; context?: HttpContext }
	): Observable<HttpResponse<any>>;
	public moveGateway(
		moveGatewayRequestVO: MoveGatewayRequestVO,
		observe?: "events",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: undefined; context?: HttpContext }
	): Observable<HttpEvent<any>>;
	public moveGateway(
		moveGatewayRequestVO: MoveGatewayRequestVO,
		observe: any = "body",
		reportProgress: boolean = false,
		options?: { httpHeaderAccept?: undefined; context?: HttpContext }
	): Observable<any> {
		if (moveGatewayRequestVO === null || moveGatewayRequestVO === undefined) {
			throw new Error("Required parameter moveGatewayRequestVO was null or undefined when calling moveGateway.");
		}

		let localVarHeaders = this.defaultHeaders;

		let localVarCredential: string | undefined;
		// authentication (Keycloak) required
		localVarCredential = this.configuration.lookupCredential("Keycloak");
		if (localVarCredential) {
		}

		// authentication (BearerAuth) required
		localVarCredential = this.configuration.lookupCredential("BearerAuth");
		if (localVarCredential) {
			localVarHeaders = localVarHeaders.set("Authorization", "Bearer " + localVarCredential);
		}

		let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
		if (localVarHttpHeaderAcceptSelected === undefined) {
			// to determine the Accept header
			const httpHeaderAccepts: string[] = [];
			localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
		}
		if (localVarHttpHeaderAcceptSelected !== undefined) {
			localVarHeaders = localVarHeaders.set("Accept", localVarHttpHeaderAcceptSelected);
		}

		let localVarHttpContext: HttpContext | undefined = options && options.context;
		if (localVarHttpContext === undefined) {
			localVarHttpContext = new HttpContext();
		}

		// to determine the Content-Type header
		const consumes: string[] = ["application/json"];
		const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
		if (httpContentTypeSelected !== undefined) {
			localVarHeaders = localVarHeaders.set("Content-Type", httpContentTypeSelected);
		}

		let responseType_: "text" | "json" | "blob" = "json";
		if (localVarHttpHeaderAcceptSelected) {
			if (localVarHttpHeaderAcceptSelected.startsWith("text")) {
				responseType_ = "text";
			} else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
				responseType_ = "json";
			} else {
				responseType_ = "blob";
			}
		}

		let localVarPath = `/gateways`;
		return this.httpClient.request<any>("patch", `${this.configuration.basePath}${localVarPath}`, {
			context: localVarHttpContext,
			body: moveGatewayRequestVO,
			responseType: <any>responseType_,
			withCredentials: this.configuration.withCredentials,
			headers: localVarHeaders,
			observe: observe,
			reportProgress: reportProgress
		});
	}

	/**
	 * Update gateway
	 * Update gateway by id.
	 * @param gatewayId
	 * @param gatewayUpdateVO
	 * @param tenantSpecification If an issuer has multiple tenants granted, a specific tenant has to be given, so the method applies to only one tenant
	 * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
	 * @param reportProgress flag to report request and response progress.
	 */
	public updateGateway(
		gatewayId: string,
		gatewayUpdateVO: GatewayUpdateVO,
		tenantSpecification?: string,
		observe?: "body",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<GatewayDetailVO>;
	public updateGateway(
		gatewayId: string,
		gatewayUpdateVO: GatewayUpdateVO,
		tenantSpecification?: string,
		observe?: "response",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<HttpResponse<GatewayDetailVO>>;
	public updateGateway(
		gatewayId: string,
		gatewayUpdateVO: GatewayUpdateVO,
		tenantSpecification?: string,
		observe?: "events",
		reportProgress?: boolean,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<HttpEvent<GatewayDetailVO>>;
	public updateGateway(
		gatewayId: string,
		gatewayUpdateVO: GatewayUpdateVO,
		tenantSpecification?: string,
		observe: any = "body",
		reportProgress: boolean = false,
		options?: { httpHeaderAccept?: "application/json"; context?: HttpContext }
	): Observable<any> {
		if (gatewayId === null || gatewayId === undefined) {
			throw new Error("Required parameter gatewayId was null or undefined when calling updateGateway.");
		}
		if (gatewayUpdateVO === null || gatewayUpdateVO === undefined) {
			throw new Error("Required parameter gatewayUpdateVO was null or undefined when calling updateGateway.");
		}

		let localVarQueryParameters = new HttpParams({ encoder: this.encoder });
		if (tenantSpecification !== undefined && tenantSpecification !== null) {
			localVarQueryParameters = this.addToHttpParams(localVarQueryParameters, <any>tenantSpecification, "tenant_specification");
		}

		let localVarHeaders = this.defaultHeaders;

		let localVarCredential: string | undefined;
		// authentication (Keycloak) required
		localVarCredential = this.configuration.lookupCredential("Keycloak");
		if (localVarCredential) {
		}

		// authentication (BearerAuth) required
		localVarCredential = this.configuration.lookupCredential("BearerAuth");
		if (localVarCredential) {
			localVarHeaders = localVarHeaders.set("Authorization", "Bearer " + localVarCredential);
		}

		let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
		if (localVarHttpHeaderAcceptSelected === undefined) {
			// to determine the Accept header
			const httpHeaderAccepts: string[] = ["application/json"];
			localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
		}
		if (localVarHttpHeaderAcceptSelected !== undefined) {
			localVarHeaders = localVarHeaders.set("Accept", localVarHttpHeaderAcceptSelected);
		}

		let localVarHttpContext: HttpContext | undefined = options && options.context;
		if (localVarHttpContext === undefined) {
			localVarHttpContext = new HttpContext();
		}

		// to determine the Content-Type header
		const consumes: string[] = ["application/json"];
		const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
		if (httpContentTypeSelected !== undefined) {
			localVarHeaders = localVarHeaders.set("Content-Type", httpContentTypeSelected);
		}

		let responseType_: "text" | "json" | "blob" = "json";
		if (localVarHttpHeaderAcceptSelected) {
			if (localVarHttpHeaderAcceptSelected.startsWith("text")) {
				responseType_ = "text";
			} else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
				responseType_ = "json";
			} else {
				responseType_ = "blob";
			}
		}

		let localVarPath = `/gateways/${this.configuration.encodeParam({
			name: "gatewayId",
			value: gatewayId,
			in: "path",
			style: "simple",
			explode: false,
			dataType: "string",
			dataFormat: undefined
		})}`;
		return this.httpClient.request<GatewayDetailVO>("patch", `${this.configuration.basePath}${localVarPath}`, {
			context: localVarHttpContext,
			body: gatewayUpdateVO,
			params: localVarQueryParameters,
			responseType: <any>responseType_,
			withCredentials: this.configuration.withCredentials,
			headers: localVarHeaders,
			observe: observe,
			reportProgress: reportProgress
		});
	}
}

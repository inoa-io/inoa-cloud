package io.inoa.fleet.ui.internal;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.net.http.HttpConnectTimeoutException;
import java.util.function.Supplier;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientException;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.exceptions.ReadTimeoutException;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Wrapper for HTTP calls to catch common exceptions and log them properly.
 *
 * @author Stephan Schnabel
 */
@UtilityClass
@Slf4j
public class ClientWrapper {

	public <T> HttpResponse<T> invoke(String context, Supplier<HttpResponse<T>> supplier) {
		try {
			return supplier.get();
		} catch (HttpClientResponseException e) {
			log.warn("{} returned status {}.", context, e.getStatus().getCode());
		} catch (ReadTimeoutException e) {
			log.warn("{} read timeout occurred.", context);
		} catch (HttpClientException e) {
			if (e.getCause() instanceof HttpConnectTimeoutException) {
				log.warn("{} connect timeout occurred.", context);
			} else if (e.getCause() instanceof ConnectException) {
				log.warn("{} not available: {}", context, e.getMessage());
			} else if (e.getCause() instanceof NoRouteToHostException) {
				log.warn("{} not available: {}", context, e.getMessage());
			} else if (e.getCause() instanceof UnknownHostException) {
				log.warn("{} not available: {}", context, e.getMessage());
			} else {
				log.error("{} call failed.", context, e);
			}
		}
		throw new HttpStatusException(HttpStatus.BAD_GATEWAY, "Failed to communicate with " + context);
	}
}

package io.inoa.tenant.test;

import java.util.Optional;

import io.envoyproxy.envoy.service.auth.v3.CheckResponse;
import io.grpc.stub.StreamObserver;
import lombok.Getter;

/**
 * Test observer for auth request.
 *
 * @author Stephan Schnabel
 */
@Getter
public class TestStreamObserver implements StreamObserver<CheckResponse> {

	private Optional<CheckResponse> response = Optional.empty();
	private boolean completed = false;

	@Override
	public void onNext(CheckResponse checkResponse) {
		response = Optional.of(checkResponse);
	}

	@Override
	public void onCompleted() {
		completed = true;
	}

	@Override
	public void onError(Throwable throwable) {}
}

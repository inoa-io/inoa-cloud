package io.inoa.fleet.registry.hono.impl;

import java.util.List;
import java.util.Optional;

import org.eclipse.hono.auth.HonoPasswordEncoder;
import org.eclipse.hono.deviceregistry.service.credentials.AbstractCredentialsManagementService;
import org.eclipse.hono.deviceregistry.service.device.DeviceKey;
import org.eclipse.hono.service.management.OperationResult;
import org.eclipse.hono.service.management.credentials.CommonCredential;

import io.inoa.fleet.registry.hono.config.DeviceServiceProperties;
import io.opentracing.Span;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CredentialsManagementServiceImpl extends AbstractCredentialsManagementService {

	public CredentialsManagementServiceImpl(Vertx vertx, HonoPasswordEncoder passwordEncoder,
			final DeviceServiceProperties properties) {
		super(vertx, passwordEncoder, properties.getMaxBcryptCostfactor(), properties.getHashAlgorithmsAllowList());
	}

	@Override
	protected Future<OperationResult<Void>> processUpdateCredentials(DeviceKey key, List<CommonCredential> credentials,
			Optional<String> resourceVersion, Span span) {
		log.info("CredentialsManagementServiceImpl.processUpdateCredentials");
		return null;
	}

	@Override
	protected Future<OperationResult<List<CommonCredential>>> processReadCredentials(DeviceKey deviceKey, Span span) {
		log.info("CredentialsManagementServiceImpl.processUpdateCredentials");
		return null;
	}
}

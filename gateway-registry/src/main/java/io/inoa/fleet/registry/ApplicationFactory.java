package io.inoa.fleet.registry;

import java.time.Clock;

import org.mapstruct.factory.Mappers;

import io.inoa.fleet.registry.rest.mapper.ConfigurationMapper;
import io.inoa.fleet.registry.rest.mapper.CredentialMapper;
import io.inoa.fleet.registry.rest.mapper.GatewayMapper;
import io.inoa.fleet.registry.rest.mapper.GroupMapper;
import io.inoa.fleet.registry.rest.mapper.TenantMapper;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

/**
 * Factory for application.
 *
 * @author Stephan Schnabel
 */
@Factory
public class ApplicationFactory {

	@Singleton
	Clock clock() {
		return Clock.systemUTC();
	}

	@Singleton
	ConfigurationMapper mapperConfiguration() {
		return Mappers.getMapper(ConfigurationMapper.class);
	}

	@Singleton
	CredentialMapper mapperCredential() {
		return Mappers.getMapper(CredentialMapper.class);
	}

	@Singleton
	GatewayMapper mapperGateway() {
		return Mappers.getMapper(GatewayMapper.class);
	}

	@Singleton
	GroupMapper mapperGroup() {
		return Mappers.getMapper(GroupMapper.class);
	}

	@Singleton
	TenantMapper mapperTenant() {
		return Mappers.getMapper(TenantMapper.class);
	}
}

package io.inoa.fleet.registry;

import java.time.Clock;

import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import io.cloudevents.rw.CloudEventDataMapper;
import io.inoa.cloud.messages.TenantVO;
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
	ObjectMapper cloudEventObjectMapper() {
		return new ObjectMapper().findAndRegisterModules();
	}

	@Singleton
	CloudEventDataMapper<PojoCloudEventData<TenantVO>> cloudEventMapperforTenant(ObjectMapper cloudEventObjectMapper) {
		return PojoCloudEventDataMapper.from(cloudEventObjectMapper, TenantVO.class);
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

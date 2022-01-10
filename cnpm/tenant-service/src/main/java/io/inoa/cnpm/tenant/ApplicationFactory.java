package io.inoa.cnpm.tenant;

import org.mapstruct.factory.Mappers;

import io.inoa.cnpm.tenant.rest.RestMapper;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

/**
 * Factory for application.
 */
@Factory
public class ApplicationFactory {

	@Singleton
	RestMapper mapper() {
		return Mappers.getMapper(RestMapper.class);
	}
}

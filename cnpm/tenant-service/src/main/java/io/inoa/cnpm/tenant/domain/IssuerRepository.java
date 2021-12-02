package io.inoa.cnpm.tenant.domain;

import java.net.URL;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link Issuer}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface IssuerRepository extends GenericRepository<Issuer, Long> {

	boolean existsByTenantAndUrl(Tenant tenant, URL url);

	Issuer save(Issuer issuer);

	Issuer update(Issuer issuer);

	void delete(Issuer issuer);
}

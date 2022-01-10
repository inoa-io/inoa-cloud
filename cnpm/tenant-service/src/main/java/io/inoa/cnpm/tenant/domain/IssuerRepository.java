package io.inoa.cnpm.tenant.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link Issuer}.
 */
@JdbcRepository
public interface IssuerRepository extends GenericRepository<Issuer, Long> {

	Optional<Issuer> findByTenantAndName(Tenant tenant, String name);

	@Query("SELECT name FROM issuer_service WHERE issuer_id = :issuerId")
	Set<String> findServices(Long issuerId);

	List<Issuer> findByTenantOrderByName(Tenant tenant);

	@Query("INSERT INTO issuer_service (issuer_id,name) VALUES (:issuerId,:name)")
	void addService(Long issuerId, String name);

	@Query("DELETE FROM issuer_service WHERE issuer_id = :issuerId AND name = :name")
	void deleteService(Long issuerId, String name);

	Issuer save(Issuer issuer);

	Issuer update(Issuer issuer);

	void delete(Issuer issuer);
}

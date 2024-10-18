package io.inoa.fleet.registry.domain;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Tenant}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TenantRepository extends CrudRepository<Tenant, Long> {

  List<Tenant> findByDeletedIsNullOrderByTenantId();

  Optional<Tenant> findByTenantIdAndDeletedIsNull(String tenantId);

  List<Tenant> findByTenantIdInListAndDeletedIsNull(List<String> tenantId);

  Optional<Tenant> findByTenantId(String tenantId);

  Boolean existsByTenantId(String tenantId);
}

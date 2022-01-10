package io.inoa.cnpm.tenant.domain;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;
import io.micronaut.data.annotation.Transient;
import lombok.Data;

/**
 * This entity represents an OIDC issuer.
 */
@MappedEntity
@Data
public class Issuer {

	@Id
	@GeneratedValue
	private Long id;
	@Relation(Kind.MANY_TO_ONE)
	private Tenant tenant;

	@MappedProperty
	private String name;
	@MappedProperty
	private URL url;
	@MappedProperty
	private Duration cacheDuration;
	@Relation(value = Kind.ONE_TO_MANY, mappedBy = "issuer")
	private Set<IssuerService> serviceObjects;

	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;

	@Transient
	public Set<String> getServices() {
		return serviceObjects.stream().map(IssuerService::getName).collect(Collectors.toSet());
	}

	public Issuer setServices(Set<String> servicesToAdd) {
		serviceObjects = servicesToAdd.stream().map(svc -> new IssuerService(this, svc)).collect(Collectors.toSet());
		return this;
	}
}

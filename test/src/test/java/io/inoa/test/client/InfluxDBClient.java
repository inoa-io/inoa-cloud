package io.inoa.test.client;

import java.util.Objects;
import java.util.Optional;

import org.awaitility.Awaitility;

import com.influxdb.client.domain.Organization;

import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class InfluxDBClient {

	private final com.influxdb.client.InfluxDBClient influx;

	public void reset() {
		influx.getOrganizationsApi().findOrganizations().forEach(influx.getOrganizationsApi()::deleteOrganization);
	}

	public Optional<Organization> findOrganization(String tenantId) {
		return influx.getOrganizationsApi().findOrganizations().stream()
				.filter(organization -> Objects.equals(organization.getName(), tenantId))
				.findAny();
	}

	public Organization getOrganization(String tenantId) {
		return Awaitility
				.await("wait for organization created")
				.until(() -> findOrganization(tenantId), Optional::isPresent)
				.get();
	}
}

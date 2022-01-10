package io.inoa.test.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.awaitility.Awaitility;

import io.inoa.measurement.provisioner.grafana.Datasource;
import io.inoa.measurement.provisioner.grafana.Organization;
import io.inoa.measurement.provisioner.grafana.QueryRequest;
import io.inoa.measurement.provisioner.grafana.QueryRequest.Query;
import io.inoa.measurement.provisioner.grafana.User;
import io.inoa.measurement.provisioner.grafana.UserRole;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

/**
 * Client grafana.
 */
@Singleton
@RequiredArgsConstructor
public class GrafanaClient {

	private final io.inoa.measurement.provisioner.grafana.GrafanaClient client;

	public void reset() {
		client.findOrganizations().stream()
				.map(Organization::getId)
				.peek(id -> System.out.println(id))
				.filter(id -> id != 1)
				.forEach(client::deleteOrganizationbyId);
	}

	public Organization getOrganization(String tenantId) {
		return Awaitility
				.await("wait for organization created")
				.until(() -> client.findOrganizationByName(tenantId), Optional::isPresent)
				.get();
	}

	public Datasource getDatasource(String tenantId, String uid) {
		var organizationId = getOrganization(tenantId).getId();
		return Awaitility
				.await("wait for datasource created")
				.until(() -> client.findDatasourceByUid(organizationId, uid), Optional::isPresent)
				.get();
	}

	public User getUser(String email) {
		return Awaitility
				.await("wait for user created")
				.until(() -> client.findUserByEmail(email), Optional::isPresent)
				.get();
	}

	public void assertUserRole(String tenantId, String email, UserRole role) {
		var userId = getUser(email).getId();
		Awaitility
				.await("wait for user assigned")
				.until(() -> client.findOrganizationsByUser(userId).stream()
						.filter(assignment -> assignment.getName().equals(tenantId))
						.filter(assignment -> assignment.getRole().equals(role))
						.findAny().isPresent());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void assertDatasourceWorking(String tenantId, String uid) {

		var datasource = getDatasource(tenantId, uid);
		var query = new QueryRequest().setQueries(List.of(new Query()
				.setDatasourceId(datasource.getId())
				.setQuery("buckets()")));
		var response = ((Map<?, Map<?, Map<?, List<Map>>>>) (Map) client.query(datasource.getOrgId(), query))
				.get("results").get("A").get("frames").get(0);

		var expectedQuery = query.getQueries().get(0).getQuery();
		var actualQuery = ((Map<?, Map<?, Map>>) response).get("schema").get("meta").get("executedQueryString");
		assertEquals(expectedQuery, actualQuery, "query");
		var expectedValues = List.of("inoa");
		var actualValues = ((Map<?, Map<?, List<List>>>) response).get("data").get("values").get(0);
		assertEquals(expectedValues, actualValues, "values");
	}
}

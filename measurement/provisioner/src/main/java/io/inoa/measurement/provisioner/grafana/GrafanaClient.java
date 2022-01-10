package io.inoa.measurement.provisioner.grafana;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;

@Client("grafana")
public interface GrafanaClient {

	// organizations

	@Get("/api/orgs")
	List<Organization> findOrganizations();

	@Get("/api/orgs/name/{name}")
	Optional<Organization> findOrganizationByName(@PathVariable String name);

	@Post("/api/orgs")
	IdResponse createOrganization(@Body Organization organization);

	@Delete("/api/orgs/{organizationId}")
	Map<String, Object> deleteOrganizationbyId(@PathVariable Long organizationId);

	@Get("/api/orgs/{organizationId}/users")
	List<User> findUsers(@PathVariable Long organizationId);

	@Post("/api/orgs/{organizationId}/users")
	Map<String, Object> addUser(@PathVariable Long organizationId, @Body AssignmentAdd assignment);

	@Patch("/api/orgs/{organizationId}/users/{userId}")
	User patchUser(@PathVariable Long organizationId, @PathVariable Long userId, @Body User user);

	@Delete("/api/orgs/{organizationId}/users/{userId}")
	Map<String, Object> removeUser(@PathVariable Long organizationId, @PathVariable Long userId);

	// users

	@Get("/api/users/lookup")
	Optional<User> findUserByEmail(@QueryValue("loginOrEmail") String email);

	@Get("/api/users/{userId}/orgs")
	List<Assignment> findOrganizationsByUser(@PathVariable Long userId);

	@Post("/api/admin/users")
	IdResponse createUser(@Body User user);

	// datasources

	@Get("/api/datasources/uid/{uid}")
	Optional<Datasource> findDatasourceByUid(
			@Header("X-Grafana-Org-Id") Long organizationId,
			@PathVariable String uid);

	@Post("/api/datasources")
	IdResponse createDatasource(
			@Header("X-Grafana-Org-Id") Long organizationId,
			@Body Datasource Datasource);

	@Post("/api/ds/query")
	QueryResponse query(
			@Header("X-Grafana-Org-Id") Long organizationId,
			@Body QueryRequest query);
}

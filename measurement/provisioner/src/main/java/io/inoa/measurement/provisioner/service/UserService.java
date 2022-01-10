package io.inoa.measurement.provisioner.service;

import java.util.UUID;

import io.inoa.cnpm.tenant.messaging.UserVO;
import io.inoa.measurement.provisioner.grafana.Assignment;
import io.inoa.measurement.provisioner.grafana.AssignmentAdd;
import io.inoa.measurement.provisioner.grafana.GrafanaClient;
import io.inoa.measurement.provisioner.grafana.Organization;
import io.inoa.measurement.provisioner.grafana.User;
import io.inoa.measurement.provisioner.grafana.UserRole;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
@RequiredArgsConstructor
public class UserService {

	private final GrafanaClient grafana;

	public void save(String tenantId, UserVO user) {

		var organizationId = grafana.findOrganizationByName(tenantId).map(Organization::getId).orElse(null);
		if (organizationId == null) {
			log.warn("Tenant {} user {} ignored because organization was not found", tenantId, user.getUserId());
			return;
		}
		var userId = grafana.findUserByEmail(user.getEmail()).map(User::getId).orElse(null);
		var role = toRole(user);

		// create new user

		if (userId == null) {
			if (role != null) {
				userId = grafana
						.createUser(new User()
								.setOrgId(organizationId)
								.setEmail(user.getEmail())
								.setPassword(UUID.randomUUID().toString()))
						.getId();
				grafana.patchUser(organizationId, userId, new User().setRole(role));
				log.info("Tenant {} user {} created in grafana role {}", tenantId, user.getUserId(), role);
			} else {
				log.info("Tenant {} user {} create skipped because role {} is unmapped",
						tenantId, user.getUserId(), user.getRole());
			}
			return;
		}

		// get assignment

		var actualRole = grafana
				.findOrganizationsByUser(userId).stream()
				.filter(organization -> organization.getOrgId() == organizationId)
				.map(Assignment::getRole)
				.findAny().orElse(null);

		// delete assignment if no role is present

		if (role == null) {
			if (actualRole == null) {
				log.info("Tenant {} user {} ignored because role {} is unmapped",
						tenantId, user.getUserId(), user.getRole());
			} else {
				grafana.removeUser(organizationId, userId);
				log.info("Tenant {} user {} removed because role {} is unmapped",
						tenantId, user.getUserId(), user.getRole());
			}
			return;
		}

		// update assignment

		if (actualRole == null) {
			grafana.addUser(organizationId, new AssignmentAdd().setLoginOrEmail(user.getEmail()).setRole(role));
			log.info("Tenant {} user {} added in grafana role {}", tenantId, user.getUserId(), role);
			return;
		}
		if (actualRole == role) {
			log.debug("Tenant {} user {} ignored because grafana role {} not changed",
					tenantId, user.getUserId(), role);
			return;
		}
		grafana.patchUser(organizationId, userId, new User().setRole(role));
		log.info("Tenant {} user {} updated grafana role from {} to {}", tenantId, user.getUserId(), actualRole, role);
	}

	public void delete(String tenantId, UserVO user) {

		var organizationId = grafana.findOrganizationByName(tenantId).map(Organization::getId).orElse(null);
		if (organizationId == null) {
			log.info("Tenant {} user {} ignored because organization was not found", tenantId, user.getUserId());
			return;
		}

		var userId = grafana.findUserByEmail(user.getEmail()).map(User::getId).orElse(null);
		if (userId == null) {
			log.info("Tenant {} user {} ignored because user was not found", tenantId, user.getUserId());
			return;
		}

		grafana.removeUser(organizationId, userId);
		log.info("Tenant {} user {} removed", tenantId, user.getUserId());
	}

	private UserRole toRole(UserVO user) {
		switch (user.getRole().toLowerCase()) {
			case "admin":
				return UserRole.Admin;
			case "editor":
				return UserRole.Editor;
			case "viewer":
				return UserRole.Viewer;
			default:
				return null;
		}
	}
}

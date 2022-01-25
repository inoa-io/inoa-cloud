package io.inoa.measurement.provisioner.service;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.Authorization;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.BucketRetentionRules;
import com.influxdb.client.domain.Permission;
import com.influxdb.client.domain.Permission.ActionEnum;
import com.influxdb.client.domain.PermissionResource;
import com.influxdb.client.domain.PermissionResource.TypeEnum;

import io.inoa.cnpm.tenant.messaging.TenantVO;
import io.inoa.measurement.provisioner.grafana.Datasource;
import io.inoa.measurement.provisioner.grafana.GrafanaClient;
import io.inoa.measurement.provisioner.grafana.Organization;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class TenantService {

	public TenantService(InfluxDBClient influx, GrafanaClient grafana, @Value("${influxdb.url}") String influxdbUrl) {
		this.influx = influx;
		this.grafana = grafana;
		this.influxdbUrl = influxdbUrl;
	}

	private final InfluxDBClient influx;
	private final GrafanaClient grafana;
	private final String influxdbUrl;

	public void save(TenantVO vo) {
		var tenantId = vo.getTenantId();

		// create influx organization

		var influxId = influx.getOrganizationsApi().findOrganizations().stream()
				.filter(organization -> Objects.equals(organization.getName(), tenantId))
				.findAny()
				.map(organization -> {
					log.info("Tenant {} - influxdb - organization found", tenantId);
					return organization;
				})
				.orElseGet(() -> {
					var organization = influx.getOrganizationsApi().createOrganization(tenantId);
					log.info("Tenant {} - influxdb - organization created", tenantId);
					return organization;
				})
				.getId();

		// create influx bucket

		var bucket = new Bucket()
				.orgID(influxId)
				.name("inoa")
				.addRetentionRulesItem(
						new BucketRetentionRules().everySeconds((int) Duration.ofDays(365).getSeconds()));
		influx.getBucketsApi().findBucketsByOrgName(vo.getTenantId()).stream()
				.filter(other -> Objects.equals(other.getName(), bucket.getName()))
				.findAny()
				.ifPresentOrElse(other -> {
					log.info("Tenant {} - influxdb - bucket found", tenantId);
				}, () -> {
					influx.getBucketsApi().createBucket(bucket);
					log.info("Tenant {} - influxdb - bucket created", tenantId);
				});

		// create influx token for grafana with buckets:read

		var token = influx.getAuthorizationsApi().findAuthorizationsByOrgID(influxId).stream()
				.filter(authorization -> "grafana".equals(authorization.getDescription()))
				.findAny()
				.map(authorization -> {
					log.info("Tenant {} - influxdb - authorization found", tenantId);
					return authorization.getToken();
				})
				.orElseGet(() -> {
					var auth = influx.getAuthorizationsApi()
							.createAuthorization((Authorization) new Authorization()
									.orgID(influxId)
									.addPermissionsItem(new Permission()
											.action(ActionEnum.READ)
											.resource(new PermissionResource().orgID(influxId).type(TypeEnum.BUCKETS)))
									.description("grafana"))
							.getToken();
					log.info("Tenant {} - influxdb - authorization created", tenantId);
					return auth;
				});

		// create organization organization

		var grafanaId = grafana.findOrganizationByName(tenantId)
				.map(organization -> {
					log.info("Tenant {} - grafana - organization found", tenantId);
					return organization.getId();
				})
				.orElseGet(() -> {
					var id = grafana.createOrganization(new Organization().setName(tenantId)).getOrgId();
					log.info("Tenant {} - grafana - organization created", tenantId);
					return id;
				});

		// create datasource

		var datasource = new Datasource()
				.setDefault(true)
				.setUid("influxdb")
				.setName("InfluxDB")
				.setType("influxdb")
				.setAccess("proxy")
				.setUrl(influxdbUrl)
				.setJsonData(Map.of("organization", tenantId, "version", "Flux", "defaultBucket", bucket.getName()))
				.setSecureJsonData(Map.of("token", token))
				.setSecureJsonFields(Map.of("token", "true"));
		grafana.findDatasourceByUid(grafanaId, datasource.getUid())
				.ifPresentOrElse(ds -> {
					log.info("Tenant {} - grafana - datasource found", tenantId);
				}, () -> {
					grafana.createDatasource(grafanaId, datasource);
					log.info("Tenant {} - grafana - datasource created", tenantId);
				});
	}

	public void delete(TenantVO tenant) {

		grafana.findOrganizationByName(tenant.getTenantId())
				.ifPresentOrElse(organization -> {
					grafana.deleteOrganizationbyId(organization.getId());
					log.info("Tenant {} - grafana - deleted", tenant.getTenantId());
				}, () -> {
					log.debug("Tenant {} - grafana - delete skipped because not found", tenant.getTenantId());
				});

		influx.getOrganizationsApi().findOrganizations().stream()
				.filter(organization -> Objects.equals(organization.getName(), tenant.getTenantId()))
				.findAny()
				.ifPresentOrElse(organization -> {
					influx.getOrganizationsApi().deleteOrganization(organization);
					log.info("Tenant {} - influxdb - deleted", tenant.getTenantId());
				}, () -> {
					log.debug("Tenant {} - influxdb - delete skipped because not found", tenant.getTenantId());
				});
	}
}

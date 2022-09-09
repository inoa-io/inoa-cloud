package io.inoa.fleet.registry.rest.gateway;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.slf4j.MDC;

import io.inoa.fleet.registry.domain.Credential;
import io.inoa.fleet.registry.domain.CredentialRepository;
import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.inoa.fleet.registry.domain.GatewayStatus;
import io.inoa.fleet.registry.domain.GatewayStatusMqtt;
import io.inoa.fleet.registry.domain.TenantRepository;
import io.inoa.fleet.registry.rest.management.CredentialTypeVO;
import io.inoa.fleet.registry.rest.mapper.ConfigurationMapper;
import io.inoa.fleet.registry.service.ConfigurationService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link GatewayApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class GatewayController implements GatewayApi {

	private final Security security;
	private final ConfigurationService service;
	private final ConfigurationMapper mapper;
	private final TenantRepository tenantRepository;
	private final GatewayRepository gatewayRepository;
	private final CredentialRepository credentialRepository;

	@Secured(SecurityRule.IS_AUTHENTICATED)
	@Override
	public HttpResponse<Map<String, Object>> getConfiguration() {
		return HttpResponse.ok(mapper.toConfigurationMap(service.findByGateway(security.getGateway())));
	}

	@Secured(SecurityRule.IS_ANONYMOUS)
	@Override
	public HttpResponse<Object> register(@Valid RegisterVO vo) {
		try {

			// TODO used fixed tenant, tenancy will be implemented later
			var tenant = tenantRepository.findByTenantId("inoa").get();

			MDC.put("tenantId", tenant.getTenantId());
			MDC.put("gatewayId", vo.getGatewayId());

			var gatewayIdPattern = tenant.getGatewayIdPattern();
			if (!Pattern.matches(gatewayIdPattern, vo.getGatewayId())) {
				throw new HttpStatusException(HttpStatus.BAD_REQUEST, "GatewayId must match " + gatewayIdPattern + ".");
			}
			if (gatewayRepository.findByGatewayId(vo.getGatewayId()).isPresent()) {
				throw new HttpStatusException(HttpStatus.CONFLICT, "GatewayId already exists.");
			}

			var gateway = gatewayRepository.save(new Gateway()
					.setTenant(tenant)
					.setGatewayId(vo.getGatewayId())
					.setEnabled(false)
					.setGroups(List.of())
					.setStatus(new GatewayStatus().setMqtt(new GatewayStatusMqtt().setConnected(false))));
			credentialRepository.save(new Credential()
					.setCredentialId(UUID.randomUUID())
					.setGateway(gateway)
					.setEnabled(true)
					.setName("initial")
					.setType(CredentialTypeVO.valueOf(vo.getCredentialType().name()))
					.setValue(vo.getCredentialValue()));
			log.info("Gateway registered: {}", gateway);

		} finally {
			MDC.remove("tenantId");
			MDC.remove("gatewayId");
		}

		return HttpResponse.noContent();
	}
}

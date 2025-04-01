package io.inoa.fleet.registry.rest.gateway;

import static io.inoa.fleet.registry.rest.management.TenantsController.DEFAULT_TENANT_ID;

import io.inoa.fleet.ota.OTAService;
import io.inoa.fleet.registry.domain.Credential;
import io.inoa.fleet.registry.domain.CredentialRepository;
import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.inoa.fleet.registry.domain.GatewayStatus;
import io.inoa.fleet.registry.domain.GatewayStatusMqtt;
import io.inoa.fleet.registry.domain.TenantRepository;
import io.inoa.fleet.registry.rest.mapper.ConfigurationMapper;
import io.inoa.fleet.registry.service.ConfigurationService;
import io.inoa.rest.CredentialTypeVO;
import io.inoa.rest.GatewayApi;
import io.inoa.rest.RegisterVO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

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

  private final OTAService otaService;

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Override
  public HttpResponse<Map<String, Object>> getConfiguration() {
    return HttpResponse.ok(mapper.toConfigurationMap(service.findByGateway(security.getGateway())));
  }

  /**
   * This method is periodically called by all gateways. They do not check if they are already
   * registered - this would cause a similar load anyway.
   *
   * @param vo the {@link RegisterVO}
   * @return {@link HttpResponse}
   */
  @Secured(SecurityRule.IS_ANONYMOUS)
  @Override
  public HttpResponse<Object> register(@Valid RegisterVO vo) {
    try {

      // New gateways are always associated with the default tenant
      var tenant =
          tenantRepository
              .findByTenantId(DEFAULT_TENANT_ID)
              // This should never happen
              .orElseThrow(
                  () ->
                      new HttpStatusException(
                          HttpStatus.INTERNAL_SERVER_ERROR, "Default tenant is missing."));

      MDC.put("tenantId", tenant.getTenantId());
      MDC.put("gatewayId", vo.getGatewayId());

      // Fail on bad ID
      if (!Pattern.matches(tenant.getGatewayIdPattern(), vo.getGatewayId())) {
        throw new HttpStatusException(
            HttpStatus.BAD_REQUEST, "GatewayId must match " + tenant.getGatewayIdPattern() + ".");
      }

      // Register Gateway for OTA
      try {
        if (!otaService.checkGatewayRegistered(vo.getGatewayId())) {
          otaService.registerGateway(vo.getGatewayId(), new String(vo.getCredentialValue()));
        }
      } catch (Exception e) {
        // We do not stop here, but just log the error to at least register the gateway
        // in database
        log.error("Unable to register gateway in Hawkbit.", e);
      }

      // Fail if Gateway is already in database
      if (gatewayRepository.findByGatewayId(vo.getGatewayId()).isPresent()) {
        throw new HttpStatusException(HttpStatus.CONFLICT, "GatewayId already exists.");
      }

      var gateway =
          gatewayRepository.save(
              new Gateway()
                  .setTenant(tenant)
                  .setGatewayId(vo.getGatewayId())
                  .setEnabled(false)
                  .setGroups(List.of())
                  .setStatus(
                      new GatewayStatus().setMqtt(new GatewayStatusMqtt().setConnected(false))));
      credentialRepository.save(
          new Credential()
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

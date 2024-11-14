package io.inoa.measurement;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Context
@ConfigurationProperties("inoa.measurement")
@Getter
@Setter
public class MeasurementProperties {

  private SecurityProperties security = new SecurityProperties();

  /** Micronaut security related properties. */
  @ConfigurationProperties("security")
  @Getter
  @Setter
  public static class SecurityProperties {

    /** Claim for tenant. */
    private String claimTenants = "tenants";

    /** Audience whitelists. */
    private List<String> tenantAudienceWhitelist = new ArrayList<>();

    /** Header name for tenantId if audience is in whitelist. */
    private String tenantHeaderName = "x-tenant-id";
  }
}

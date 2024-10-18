package io.inoa.fleet.registry.domain;

import io.micronaut.data.annotation.Embeddable;
import io.micronaut.data.annotation.Relation;
import lombok.Data;

@Embeddable
@Data
public class GatewayStatus {

  @Relation(Relation.Kind.EMBEDDED)
  private GatewayStatusMqtt mqtt;
}

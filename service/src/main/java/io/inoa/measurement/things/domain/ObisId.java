package io.inoa.measurement.things.domain;

public enum ObisId {
  OBIS_1_8_0("1-0:1.8.0"),
  OBIS_2_8_0("1-0:2.8.0"),
  OBIS_3_0_0("7-20:3.0.0");

  private final String obisId;

  ObisId(String obisId) {
    this.obisId = obisId;
  }

  public String getObisId() {
    return obisId;
  }
}

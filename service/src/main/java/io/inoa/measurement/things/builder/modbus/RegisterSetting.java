package io.inoa.measurement.things.builder.modbus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterSetting {
  private byte functionCode;
  private short registerOffset;
  private short registerLength;
}

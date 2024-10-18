package io.inoa.measurement.things.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDefinition {
  private String key;
  private String name;
  private String inputType;
}

package io.inoa.measurement.provisioner.grafana;

import lombok.Data;

@Data
public class Assignment {

	private Long orgId;
	private String name;
	private UserRole role;
}

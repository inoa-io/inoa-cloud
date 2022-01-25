package io.inoa.measurement.provisioner.grafana;

import lombok.Data;

@Data
public class AssignmentAdd {

	private String loginOrEmail;
	private UserRole role;
}

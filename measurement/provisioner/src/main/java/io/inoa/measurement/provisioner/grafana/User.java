package io.inoa.measurement.provisioner.grafana;

import lombok.Data;

@Data
public class User {

	private Long orgId;
	private Long id;
	private String password;
	private String email;
	private UserRole role;
}

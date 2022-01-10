package io.inoa.measurement.provisioner.grafana;

import java.util.Map;

import lombok.Data;

@Data
public class Datasource {

	private Long id;
	private Long orgId;
	private String uid;
	private String name;
	private String type;
	private String access;
	private String url;
	private boolean isDefault;
	private boolean editable;
	private Map<String, String> jsonData;
	private Map<String, String> secureJsonData;
	private Map<String, String> secureJsonFields;
}

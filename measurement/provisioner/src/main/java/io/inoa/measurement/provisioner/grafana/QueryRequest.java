package io.inoa.measurement.provisioner.grafana;

import java.util.List;

import lombok.Data;

@Data
public class QueryRequest {

	private List<Query> queries;

	@Data
	public static class Query {
		private String query;
		private Long datasourceId;
	}
}

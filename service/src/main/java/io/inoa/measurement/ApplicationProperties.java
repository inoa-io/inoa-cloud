package io.inoa.measurement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Getter;
import lombok.Setter;

@Context
@ConfigurationProperties("inoa.measurement")
@Getter
@Setter
public class ApplicationProperties {

	private SecurityProperties security = new SecurityProperties();
	private TranslatorProperties translator = new TranslatorProperties();

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

	@ConfigurationProperties("translator")
	@Getter
	@Setter
	public static class TranslatorProperties {

		private Set<TypeProperties> types = new HashSet<>();

		@Getter
		@Setter
		public static class TypeProperties {
			private String name;
			private Set<SensorProperties> sensors = new HashSet<>();
		}

		@Getter
		@Setter
		public static class SensorProperties {
			private String name;
			private String namePattern;
			private String converter;
			private boolean ignore = false;
			private Optional<Double> modifier = Optional.empty();
			private Map<String, String> ext = new HashMap<>();
			private Map<String, Object> config = new HashMap<>();
		}
	}
}

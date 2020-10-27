package io.kokuwa.fleet.registry.test;

import java.util.Map;
import java.util.UUID;

import io.kokuwa.fleet.registry.domain.Gateway;

/**
 * Testdata from <b>src/main/resources/database/H2/V999__testdata.sql</b>.
 *
 * @author Stephan Schnabel
 */
public class Data {

	public static final UUID TENANT_1_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	public static final UUID GATEWAY_1_TENANT_UUID = TENANT_1_UUID;
	public static final UUID GATEWAY_1_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	public static final String GATEWAY_1_SECRET = "pleaseChangeThisSecretForANewOne";
	public static final Map<String, String> GATEWAY_1_PROPERTIES = Map.of("aaa", "a", "bbb", "b");

	public static final UUID GATEWAY_2_TENANT_UUID = TENANT_1_UUID;
	public static final UUID GATEWAY_2_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
	public static final String GATEWAY_2_SECRET = "pleaseChangeThisSecretForANewOne";
	public static final Map<String, String> GATEWAY_2_PROPERTIES = Map.of();

	public static final Gateway GATEWAY_3 = (Gateway) new Gateway().setId(3L);
	public static final Long GATEWAY_3_ID = 3L;
	public static final UUID GATEWAY_3_TENANT_UUID = TENANT_1_UUID;
	public static final UUID GATEWAY_3_UUID = UUID.fromString("00000000-0000-0000-0000-000000000002");
	public static final String GATEWAY_3_SECRET = "pleaseChangeThisSecretForANewOne";
}

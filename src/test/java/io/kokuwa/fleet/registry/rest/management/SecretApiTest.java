package io.kokuwa.fleet.registry.rest.management;

import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert204;
import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert401;
import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert404;
import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.kokuwa.fleet.registry.AbstractTest;

/**
 * Test for {@link SecretApi}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("management: secret")
public class SecretApiTest extends AbstractTest implements SecretApiTestSpec {

	@Inject
	SecretApiTestClient client;

	@DisplayName("getSecrets(200): multiple secrets")
	@Test
	@Override
	public void getSecrets200() {

		// create secrets

		var gateway = data.gateway();
		var secret1 = data.secret(gateway);
		var secret2 = data.secret(gateway);
		var secret3 = data.secret(data.gateway());

		// execute

		var secrets = assert200(() -> client.getSecrets(bearerAdmin(), gateway.getExternalId()));
		assertTrue(secrets.stream().anyMatch(s -> s.getSecretId().equals(secret1.getExternalId())), "secrets 1");
		assertTrue(secrets.stream().anyMatch(s -> s.getSecretId().equals(secret2.getExternalId())), "secrets 2");
		assertTrue(secrets.stream().noneMatch(s -> s.getSecretId().equals(secret3.getExternalId())), "secrets 3");
		assertEquals(2, secrets.size(), "secret list");
	}

	@DisplayName("getSecrets(200): empty list")
	@Test
	public void getSecrets200Empty() {

		// create secrets

		var gateway = data.gateway();
		data.secret(data.gateway());

		// execute

		var secrets = assert200(() -> client.getSecrets(bearerAdmin(), gateway.getExternalId()));
		assertTrue(secrets.isEmpty(), "secrets found");
	}

	@DisplayName("getSecrets(401): no token")
	@Test
	@Override
	public void getSecrets401() {
		assert401(() -> client.getSecrets(null, data.gateway().getExternalId()));
	}

	@DisplayName("getSecrets(404): gateway not exists")
	@Test
	@Override
	public void getSecrets404() {
		assert404(() -> client.getSecrets(bearerAdmin(), UUID.randomUUID()));
	}

	@DisplayName("getSecret(200): hmac")
	@Test
	@Override
	public void getSecret200() {
		var gateway = data.gateway();
		var expected = data.secretHmac(gateway, data.secretName(), "foo");
		var actual = assert200(
				() -> client.getSecret(bearerAdmin(), gateway.getExternalId(), expected.getExternalId()));
		assertEquals(expected.getExternalId(), actual.getSecretId(), "secretId");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
		assertEquals(SecretTypeVO.HMAC, actual.getType(), "type");
		assertEquals("foo", new String(((SecretDetailHmacVO) actual).getHmac()), "hmac");
	}

	@DisplayName("getSecret(200): rsa")
	@Test
	public void getSecret200Rsa() {
		var gateway = data.gateway();
		var expected = data.secretRSA(gateway, data.secretName(), "public", "private");
		var actual = assert200(
				() -> client.getSecret(bearerAdmin(), gateway.getExternalId(), expected.getExternalId()));
		assertEquals(expected.getExternalId(), actual.getSecretId(), "secretId");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
		assertEquals(SecretTypeVO.RSA, actual.getType(), "type");
		assertEquals("public", new String(((SecretDetailRSAVO) actual).getPublicKey()), "public");
		assertEquals("private", new String(((SecretDetailRSAVO) actual).getPrivateKey()), "private");
	}

	@DisplayName("getSecret(401): no token")
	@Test
	@Override
	public void getSecret401() {
		var gateway = data.gateway();
		var secret = data.secret(gateway);
		assert401(() -> client.getSecret(null, gateway.getExternalId(), secret.getExternalId()));

	}

	@DisplayName("getSecret(404): gateway not found")
	@Test
	@Override
	public void getSecret404() {
		assert404(() -> client.getSecret(bearerAdmin(), UUID.randomUUID(), UUID.randomUUID()));

	}

	@DisplayName("getSecret(404): secret not found")
	@Test
	public void getSecret40Secret() {
		var gateway = data.gateway();
		assert404(() -> client.getSecret(bearerAdmin(), gateway.getExternalId(), UUID.randomUUID()));
	}

	@DisplayName("createSecret(201): hmac")
	@Test
	@Override
	public void createSecret201() {

		var gateway = data.gateway();
		var vo = new SecretCreateHmacVO()
				.setHmac("foo".getBytes())
				.setName(data.secretName());
		var created = assert201(() -> client.createSecret(bearerAdmin(), gateway.getExternalId(), vo));

		assertEquals(gateway.getExternalId(), created.getGatewayId(), "gatewayId");
		assertNotNull(created.getSecretId(), "secretId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertEquals(vo.getEnabled(), created.getEnabled(), "enabled");
		assertEquals(SecretTypeVO.HMAC, created.getType(), "type");
		assertEquals("foo", new String(((SecretDetailHmacVO) created).getHmac()), "hmac");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created,
				assert200(() -> client.getSecret(bearerAdmin(), gateway.getExternalId(), created.getSecretId())), "vo");
	}

	@DisplayName("createSecret(201): rsa with public key")
	@Test
	public void createSecret201RSAPublicKey() {

		var gateway = data.gateway();
		var vo = new SecretCreateRSAVO()
				.setPublicKey("public".getBytes())
				.setPrivateKey(null)
				.setName(data.secretName());
		var created = assert201(() -> client.createSecret(bearerAdmin(), gateway.getExternalId(), vo));

		assertEquals(gateway.getExternalId(), created.getGatewayId(), "gatewayId");
		assertNotNull(created.getSecretId(), "secretId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertEquals(vo.getEnabled(), created.getEnabled(), "enabled");
		assertEquals(SecretTypeVO.RSA, created.getType(), "type");
		assertEquals("public", new String(((SecretDetailRSAVO) created).getPublicKey()), "public");
		assertEquals(null, ((SecretDetailRSAVO) created).getPrivateKey(), "private");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created,
				assert200(() -> client.getSecret(bearerAdmin(), gateway.getExternalId(), created.getSecretId())), "vo");
	}

	@DisplayName("createSecret(201): rsa with public/private key")
	@Test
	public void createSecret201RSAPrivateKey() {

		var gateway = data.gateway();
		var vo = new SecretCreateRSAVO()
				.setPublicKey("public".getBytes())
				.setPrivateKey("private".getBytes())
				.setName(data.secretName());
		var created = assert201(() -> client.createSecret(bearerAdmin(), gateway.getExternalId(), vo));

		assertEquals(gateway.getExternalId(), created.getGatewayId(), "gatewayId");
		assertNotNull(created.getSecretId(), "secretId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertEquals(vo.getEnabled(), created.getEnabled(), "enabled");
		assertEquals(SecretTypeVO.RSA, created.getType(), "type");
		assertEquals("public", new String(((SecretDetailRSAVO) created).getPublicKey()), "public");
		assertEquals("private", new String(((SecretDetailRSAVO) created).getPrivateKey()), "private");
		assertNotNull(created.getCreated(), "created");
		assertEquals(created,
				assert200(() -> client.getSecret(bearerAdmin(), gateway.getExternalId(), created.getSecretId())), "vo");
	}

	@DisplayName("createSecret(400): check bean validation")
	@Test
	@Override
	public void createSecret400() {
		var gateway = data.gateway();
		var vo = new SecretCreateHmacVO().setHmac("foo".getBytes()).setName("");
		assert400(() -> client.createSecret(bearerAdmin(), gateway.getExternalId(), vo));
		assertEquals(0, data.countSecrets(gateway), "created");
	}

	@DisplayName("createSecret(400): hmac missing")
	@Test
	public void createSecret400Hmac() {
		var gateway = data.gateway();
		var vo = new SecretCreateHmacVO().setHmac(null).setName(data.secretName());
		assert400(() -> client.createSecret(bearerAdmin(), gateway.getExternalId(), vo));
		assertEquals(0, data.countSecrets(gateway), "created");
	}

	@DisplayName("createSecret(401): no token")
	@Test
	@Override
	public void createSecret401() {
		var gateway = data.gateway();
		var vo = new SecretCreateHmacVO().setHmac("foo".getBytes()).setName(data.secretName());
		assert401(() -> client.createSecret(null, gateway.getExternalId(), vo));
		assertEquals(0, data.countSecrets(gateway), "created");
	}

	@DisplayName("createSecret(404): gateway not exists")
	@Test
	@Override
	public void createSecret404() {
		var vo = new SecretCreateHmacVO().setHmac("foo".getBytes()).setName(data.secretName());
		assert404(() -> client.createSecret(bearerAdmin(), UUID.randomUUID(), vo));
	}

	@DisplayName("createSecret(409): name exists")
	@Test
	@Override
	public void createSecret409() {
		var gateway = data.gateway();
		var existing = data.secret(gateway);
		assert409(() -> client.createSecret(bearerAdmin(), gateway.getExternalId(), new SecretCreateRSAVO()
				.setPublicKey("".getBytes())
				.setName(existing.getName())));
		assertEquals(1, data.countSecrets(gateway), "created");
	}

	@DisplayName("deleteSecret(204): success")
	@Test
	@Override
	public void deleteSecret204() {

		var gateway = data.gateway();
		var secret = data.secret(gateway);

		assert204(() -> client.deleteSecret(bearerAdmin(), gateway.getExternalId(), secret.getExternalId()));
		assertEquals(0, data.countSecrets(gateway), "secret not deleted");
		assertEquals(1, data.countGateways(), "gateway deleted");
	}

	@DisplayName("deleteSecret(401): no token")
	@Test
	@Override
	public void deleteSecret401() {
		var gateway = data.gateway();
		var secret = data.secret(gateway);
		assert401(() -> client.deleteSecret(null, gateway.getExternalId(), secret.getExternalId()));
		assertEquals(1, data.countSecrets(gateway), "secret deleted");
		assertEquals(1, data.countGateways(), "gateway deleted");
	}

	@DisplayName("deleteSecret(404): gateway/secret not found")
	@Test
	@Override
	public void deleteSecret404() {
		var gateway = data.gateway();
		assert404(() -> client.deleteSecret(bearerAdmin(), gateway.getExternalId(), UUID.randomUUID()));
		assert404(() -> client.deleteSecret(bearerAdmin(), UUID.randomUUID(), UUID.randomUUID()));
	}
}

package io.inoa.fleet.registry.rest.management;

import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert204;
import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert401;
import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.registry.AbstractTest;

/**
 * Test for {@link CredentialsApi}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("management: credentials")
public class CredentialsApiTest extends AbstractTest implements CredentialsApiTestSpec {

	@Inject
	CredentialsApiTestClient client;

	@DisplayName("findCredentials(200): success")
	@Test
	@Override
	public void findCredentials200() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		data.credential(gateway, CredentialTypeVO.RSA);
		data.credential(gateway, CredentialTypeVO.PSK);
		data.credential(gateway, CredentialTypeVO.PASSWORD);
		var credentials = assert200(() -> client.findCredentials(auth(tenant), gateway.getGatewayId()));
		assertEquals(3, credentials.size(), "credentials");
	}

	@DisplayName("findCredentials(401): no token")
	@Test
	@Override
	public void findCredentials401() {
		assert401(() -> client.findCredentials(null, UUID.randomUUID()));
	}

	@DisplayName("findCredentials(404): not found")
	@Test
	@Override
	public void findCredentials404() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		assert404("Gateway not found.", () -> client.findCredentials(auth(tenant), UUID.randomUUID()));
		assert404("Gateway not found.", () -> client.findCredentials(auth(data.tenant()), gateway.getGatewayId()));
	}

	@DisplayName("findCredential(200): without secrets")
	@Test
	@Override
	public void findCredential200() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var expected = data.credential(gateway, CredentialTypeVO.RSA);
		var actual = assert200(
				() -> client.findCredential(auth(tenant), gateway.getGatewayId(), expected.getCredentialId()));

		assertEquals(expected.getCredentialId(), actual.getCredentialId(), "credentialId");
		assertEquals(expected.getAuthId(), actual.getAuthId(), "authId");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(expected.getType(), actual.getType(), "type");
		assertEquals(List.of(), actual.getSecrets(), "secret");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("findCredential(200): with secrets")
	@Test
	public void findCredential200WithSecrets() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var expected = data.credential(gateway, CredentialTypeVO.RSA);
		var secret = data.secret(expected);
		var actual = assert200(() -> client.findCredential(
				auth(tenant), gateway.getGatewayId(), expected.getCredentialId()));

		assertEquals(expected.getCredentialId(), actual.getCredentialId(), "credentialId");
		assertEquals(expected.getAuthId(), actual.getAuthId(), "authId");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(expected.getType(), actual.getType(), "type");
		assertEquals(1, actual.getSecrets().size(), "secret");
		assertEquals(secret.getSecretId(), actual.getSecrets().get(0).getSecretId(), "secret.secretId");
		assertEquals(expected.getType(), actual.getSecrets().get(0).getType(), "secret.type");
		assertEquals(secret.getCreated(), actual.getSecrets().get(0).getCreated(), "secret.created");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("findCredential(401): no token")
	@Test
	@Override
	public void findCredential401() {
		assert401(() -> client.findCredential(null, UUID.randomUUID(), UUID.randomUUID()));
	}

	@DisplayName("findCredential(404): not found")
	@Test
	@Override
	public void findCredential404() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var gatewayId = gateway.getGatewayId();
		var credential = data.credential(gateway, CredentialTypeVO.PASSWORD);
		var credentialId = credential.getCredentialId();

		var auth = auth(tenant);
		var authOther = auth(data.tenant());
		assert404("Credential not found.", () -> client.findCredential(auth, gatewayId, UUID.randomUUID()));
		assert404("Gateway not found.", () -> client.findCredential(auth, UUID.randomUUID(), credentialId));
		assert404("Gateway not found.", () -> client.findCredential(authOther, gatewayId, credentialId));
	}

	@DisplayName("findSecret(200): RSA")
	@Test
	@Override
	public void findSecret200() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var credential = data.credential(gateway, CredentialTypeVO.RSA);
		var expected = data.secret(credential);
		var actual = assert200(() -> client.findSecret(
				auth(tenant), gateway.getGatewayId(), credential.getCredentialId(), expected.getSecretId()));

		assertEquals(expected.getSecretId(), actual.getSecretId(), "secretId");
		assertEquals(expected.getCredential().getType(), actual.getType(), "type");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertTrue(actual instanceof SecretDetailRSAVO, "instance");
		assertArrayEquals(expected.getPublicKey(), ((SecretDetailRSAVO) actual).getPublicKey(), "public key");
		assertArrayEquals(expected.getPrivateKey(), ((SecretDetailRSAVO) actual).getPrivateKey(), "private key");
	}

	@DisplayName("findSecret(200): PSK")
	@Test
	public void findSecret200PSK() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var credential = data.credential(gateway, CredentialTypeVO.PSK);
		var expected = data.secret(credential);
		var actual = assert200(() -> client.findSecret(
				auth(tenant), gateway.getGatewayId(), credential.getCredentialId(), expected.getSecretId()));

		assertEquals(expected.getSecretId(), actual.getSecretId(), "secretId");
		assertEquals(expected.getCredential().getType(), actual.getType(), "type");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertTrue(actual instanceof SecretDetailPSKVO, "instance");
		assertArrayEquals(expected.getSecret(), ((SecretDetailPSKVO) actual).getSecret(), "secret");
	}

	@DisplayName("findSecret(200): RSA")
	@Test
	public void findSecret200Password() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var credential = data.credential(gateway, CredentialTypeVO.PASSWORD);
		var expected = data.secret(credential);

		var actual = assert200(() -> client.findSecret(
				auth(tenant), gateway.getGatewayId(), credential.getCredentialId(), expected.getSecretId()));
		assertEquals(expected.getSecretId(), actual.getSecretId(), "secretId");
		assertEquals(expected.getCredential().getType(), actual.getType(), "type");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertTrue(actual instanceof SecretDetailPasswordVO, "instance");
		assertArrayEquals(expected.getPassword(), ((SecretDetailPasswordVO) actual).getPassword(), "password");
	}

	@DisplayName("findSecret(401): no token")
	@Test
	@Override
	public void findSecret401() {
		assert401(() -> client.findSecret(null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
	}

	@DisplayName("findSecret(404): secret not found")
	@Test
	@Override
	public void findSecret404() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var gatewayId = gateway.getGatewayId();
		var credential = data.credential(gateway, CredentialTypeVO.PASSWORD);
		var credentialId = credential.getCredentialId();
		var secretId = data.secret(credential).getSecretId();

		var auth = auth(tenant);
		var authOther = auth(data.tenant());
		assert404("Secret not found.", () -> client.deleteSecret(auth, gatewayId, credentialId, UUID.randomUUID()));
		assert404("Credential not found.", () -> client.deleteSecret(auth, gatewayId, UUID.randomUUID(), secretId));
		assert404("Gateway not found.", () -> client.deleteSecret(auth, UUID.randomUUID(), credentialId, secretId));
		assert404("Gateway not found.", () -> client.deleteSecret(authOther, gatewayId, credentialId, secretId));
	}

	@DisplayName("createCredential(201): with mandatory properties")
	@Test
	@Override
	public void createCredential201() {
		var tenant = data.tenant();
		var gatewayId = data.gateway(tenant).getGatewayId();
		var vo = new CredentialCreateVO()
				.setAuthId(data.credentialAuthId())
				.setType(CredentialTypeVO.PASSWORD);
		var auth = auth(tenant);
		var created = assert201(() -> client.createCredential(auth, gatewayId, vo));
		assertNotNull(created.getCredentialId(), "credentialId");
		assertEquals(vo.getAuthId(), created.getAuthId(), "authId");
		assertEquals(true, created.getEnabled(), "enabled");
		assertEquals(vo.getType(), created.getType(), "type");
		assertEquals(List.of(), created.getSecrets(), "secrets");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findCredential(auth, gatewayId, created.getCredentialId())), "vo");
	}

	@DisplayName("createCredential(201): with optional properties")
	@Test
	public void createCredential201All() {
		var tenant = data.tenant();
		var gatewayId = data.gateway(tenant).getGatewayId();
		var password = UUID.randomUUID().toString().getBytes();
		var vo = new CredentialCreateVO()
				.setAuthId(data.credentialAuthId())
				.setType(CredentialTypeVO.PASSWORD)
				.setEnabled(false)
				.setSecrets(List.of(new SecretCreatePasswordVO().setPassword(password)));
		var auth = auth(tenant);
		var created = assert201(() -> client.createCredential(auth, gatewayId, vo));
		assertNotNull(created.getCredentialId(), "credentialId");
		assertEquals(vo.getAuthId(), created.getAuthId(), "authId");
		assertEquals(false, created.getEnabled(), "enabled");
		assertEquals(vo.getType(), created.getType(), "type");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(1, created.getSecrets().size(), "secrets");
		assertEquals(vo.getType(), created.getSecrets().get(0).getType(), "secret.type");
		assertNotNull(created.getSecrets().get(0).getSecretId(), "secret.secretId");
		assertNotNull(created.getSecrets().get(0).getCreated(), "secret.created");
		assertEquals(created, assert200(() -> client.findCredential(auth, gatewayId, created.getCredentialId())), "vo");
	}

	@DisplayName("createCredential(400): is beanvalidation active")
	@Test
	@Override
	public void createCredential400() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		assert400(() -> client.createCredential(auth(tenant), gateway.getGatewayId(), new CredentialCreateVO()));
		assertEquals(0, data.countCredentials(gateway), "created");
	}

	@DisplayName("createCredential(401): no token")
	@Test
	@Override
	public void createCredential401() {
		assert401(() -> client.createCredential(null, UUID.randomUUID(), new CredentialCreateVO()));
	}

	@DisplayName("createCredential(404): not found")
	@Test
	@Override
	public void createCredential404() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var vo = new CredentialCreateVO().setAuthId(data.credentialAuthId()).setType(CredentialTypeVO.PASSWORD);
		assert404("Gateway not found.", () -> client.createCredential(auth(tenant), UUID.randomUUID(), vo));
		assert404("Gateway not found.", () -> client.createCredential(auth(data.tenant()), gateway.getGatewayId(), vo));
		assertEquals(0, data.countCredentials(gateway), "created");
	}

	@DisplayName("createCredential(409): success")
	@Test
	@Override
	public void createCredential409() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var authId = data.credential(gateway, CredentialTypeVO.PSK).getAuthId();
		var vo = new CredentialCreateVO().setAuthId(authId).setType(CredentialTypeVO.PASSWORD);
		assert409(() -> client.createCredential(auth(tenant), gateway.getGatewayId(), vo));
		assertEquals(1, data.countCredentials(gateway), "created");
	}

	@DisplayName("deleteCredential(204): without secret")
	@Test
	@Override
	public void deleteCredential204() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var credential = data.credential(gateway, CredentialTypeVO.PASSWORD);
		assert204(() -> client.deleteCredential(auth(tenant), gateway.getGatewayId(), credential.getCredentialId()));
		assertEquals(0, data.countCredentials(gateway), "credential not deleted");
	}

	@DisplayName("deleteCredential(204): with secret")
	@Test
	public void deleteCredential204WithSecret() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var credential = data.credential(gateway, CredentialTypeVO.PASSWORD);
		data.secret(credential);
		assert204(() -> client.deleteCredential(auth(tenant), gateway.getGatewayId(), credential.getCredentialId()));
		assertEquals(0, data.countCredentials(gateway), "credential not deleted");
		assertEquals(0, data.countSecrets(credential), "group not deleted");
	}

	@DisplayName("deleteCredential(401): no token")
	@Test
	@Override
	public void deleteCredential401() {
		assert401(() -> client.deleteCredential(null, UUID.randomUUID(), UUID.randomUUID()));
	}

	@DisplayName("deleteCredential(404): not found")
	@Test
	@Override
	public void deleteCredential404() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var gatewayId = gateway.getGatewayId();
		var credential = data.credential(gateway, CredentialTypeVO.PASSWORD);
		var credentialId = credential.getCredentialId();

		var auth = auth(tenant);
		var authOther = auth(data.tenant());
		assert404("Credential not found.", () -> client.deleteCredential(auth, gatewayId, UUID.randomUUID()));
		assert404("Gateway not found.", () -> client.deleteCredential(auth, UUID.randomUUID(), credentialId));
		assert404("Gateway not found.", () -> client.deleteCredential(authOther, gatewayId, credentialId));
		assertEquals(1, data.countCredentials(gateway), "deleted");
	}

	@DisplayName("createSecret(201): Password")
	@Test
	@Override
	public void createSecret201() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var credentialId = data.credential(gateway, CredentialTypeVO.PASSWORD).getCredentialId();
		var vo = new SecretCreatePasswordVO().setPassword(UUID.randomUUID().toString().getBytes());
		var auth = auth(tenant);
		var created = assert201(() -> client.createSecret(auth, gateway.getGatewayId(), credentialId, vo));

		assertNotNull(created.getSecretId(), "secretId");
		assertNotNull(created.getCreated(), "created");
		assertEquals(CredentialTypeVO.PASSWORD, created.getType(), "created");
		assertArrayEquals(vo.getPassword(), ((SecretDetailPasswordVO) created).getPassword(), "password");
		assertEquals(created, assert200(() -> client.findSecret(
				auth, gateway.getGatewayId(), credentialId, created.getSecretId())), "vo");
	}

	@DisplayName("createSecret(201): PSK")
	@Test
	public void createSecret201PSK() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var credentialId = data.credential(gateway, CredentialTypeVO.PSK).getCredentialId();
		var vo = new SecretCreatePSKVO().setSecret(UUID.randomUUID().toString().getBytes());
		var auth = auth(tenant);
		var created = assert201(() -> client.createSecret(auth, gateway.getGatewayId(), credentialId, vo));

		assertNotNull(created.getSecretId(), "secretId");
		assertNotNull(created.getCreated(), "created");
		assertEquals(CredentialTypeVO.PSK, created.getType(), "created");
		assertArrayEquals(vo.getSecret(), ((SecretDetailPSKVO) created).getSecret(), "secret");
		assertEquals(created, assert200(() -> client.findSecret(
				auth, gateway.getGatewayId(), credentialId, created.getSecretId())), "vo");
	}

	@DisplayName("createSecret(201): RSA with private key")
	@Test
	public void createSecret201RSAWithPrivateKey() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var credentialId = data.credential(gateway, CredentialTypeVO.RSA).getCredentialId();
		var vo = new SecretCreateRSAVO()
				.setPublicKey(UUID.randomUUID().toString().getBytes())
				.setPrivateKey(UUID.randomUUID().toString().getBytes());
		var auth = auth(tenant);
		var created = assert201(() -> client.createSecret(auth, gateway.getGatewayId(), credentialId, vo));

		assertNotNull(created.getSecretId(), "secretId");
		assertNotNull(created.getCreated(), "created");
		assertEquals(CredentialTypeVO.RSA, created.getType(), "created");
		assertArrayEquals(vo.getPublicKey(), ((SecretDetailRSAVO) created).getPublicKey(), "public key");
		assertArrayEquals(vo.getPrivateKey(), ((SecretDetailRSAVO) created).getPrivateKey(), "private key");
		assertEquals(created, assert200(() -> client.findSecret(
				auth, gateway.getGatewayId(), credentialId, created.getSecretId())), "vo");
	}

	@DisplayName("createSecret(201): RSA without private key")
	@Test
	public void createSecret201RSAWithoutPrivateKey() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var credentialId = data.credential(gateway, CredentialTypeVO.RSA).getCredentialId();
		var vo = new SecretCreateRSAVO().setPublicKey(UUID.randomUUID().toString().getBytes());
		var auth = auth(tenant);
		var created = assert201(() -> client.createSecret(auth, gateway.getGatewayId(), credentialId, vo));
		assertNotNull(created.getSecretId(), "secretId");
		assertNotNull(created.getCreated(), "created");
		assertEquals(CredentialTypeVO.RSA, created.getType(), "created");
		assertArrayEquals(vo.getPublicKey(), ((SecretDetailRSAVO) created).getPublicKey(), "public key");
		assertNull(((SecretDetailRSAVO) created).getPrivateKey(), "private key");
		assertEquals(created, assert200(() -> client.findSecret(
				auth, gateway.getGatewayId(), credentialId, created.getSecretId())), "vo");
	}

	@DisplayName("createSecret(400): is beanvalidation active")
	@Test
	@Override
	public void createSecret400() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var credential = data.credential(gateway, CredentialTypeVO.PSK);
		var vo = new SecretCreatePSKVO();
		assert400(() -> client.createSecret(auth(tenant), gateway.getGatewayId(), credential.getCredentialId(), vo));
		assertEquals(0, data.countSecrets(credential), "created");
	}

	@DisplayName("createSecret(401): no token")
	@Test
	@Override
	public void createSecret401() {
		assert401(() -> client.createSecret(null, UUID.randomUUID(), UUID.randomUUID(), new SecretCreatePSKVO()));
	}

	@DisplayName("createSecret(404): not found")
	@Test
	@Override
	public void createSecret404() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var gatewayId = gateway.getGatewayId();
		var credential = data.credential(gateway, CredentialTypeVO.PASSWORD);
		var credentialId = credential.getCredentialId();
		var vo = new SecretCreatePasswordVO().setPassword("".getBytes());

		var auth = auth(tenant);
		var authOther = auth(data.tenant());
		assert404("Credential not found.", () -> client.createSecret(auth, gatewayId, UUID.randomUUID(), vo));
		assert404("Gateway not found.", () -> client.createSecret(auth, UUID.randomUUID(), credentialId, vo));
		assert404("Gateway not found.", () -> client.createSecret(authOther, gatewayId, credentialId, vo));
		assertEquals(0, data.countSecrets(credential), "created");
	}

	@DisplayName("deleteSecret(204): success")
	@Test
	@Override
	public void deleteSecret204() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var credential = data.credential(gateway, CredentialTypeVO.PASSWORD);
		var secret = data.secret(credential);
		assert204(() -> client.deleteSecret(auth(tenant),
				gateway.getGatewayId(), credential.getCredentialId(), secret.getSecretId()));
		assertEquals(1, data.countCredentials(gateway), "credential deleted");
		assertEquals(0, data.countSecrets(credential), "group not deleted");
	}

	@DisplayName("deleteSecret(401): no token")
	@Test
	@Override
	public void deleteSecret401() {
		assert401(() -> client.deleteSecret(null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
	}

	@DisplayName("deleteSecret(404): not found")
	@Test
	@Override
	public void deleteSecret404() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var gatewayId = gateway.getGatewayId();
		var credential = data.credential(gateway, CredentialTypeVO.PASSWORD);
		var credentialId = credential.getCredentialId();
		var secretId = data.secret(credential).getSecretId();

		var auth = auth(tenant);
		var authOther = auth(data.tenant());
		assert404("Secret not found.", () -> client.deleteSecret(auth, gatewayId, credentialId, UUID.randomUUID()));
		assert404("Credential not found.", () -> client.deleteSecret(auth, gatewayId, UUID.randomUUID(), secretId));
		assert404("Gateway not found.", () -> client.deleteSecret(auth, UUID.randomUUID(), credentialId, secretId));
		assert404("Gateway not found.", () -> client.deleteSecret(authOther, gatewayId, credentialId, secretId));
		assertEquals(1, data.countSecrets(credential), "deleted");
	}
}

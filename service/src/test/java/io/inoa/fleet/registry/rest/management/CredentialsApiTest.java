package io.inoa.fleet.registry.rest.management;

import static io.inoa.test.HttpAssertions.assert200;
import static io.inoa.test.HttpAssertions.assert201;
import static io.inoa.test.HttpAssertions.assert204;
import static io.inoa.test.HttpAssertions.assert400;
import static io.inoa.test.HttpAssertions.assert401;
import static io.inoa.test.HttpAssertions.assert404;
import static io.inoa.test.HttpAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.inoa.rest.CredentialCreateVO;
import io.inoa.rest.CredentialTypeVO;
import io.inoa.rest.CredentialUpdateVO;
import io.inoa.rest.CredentialsApiTestClient;
import io.inoa.rest.CredentialsApiTestSpec;
import io.inoa.test.AbstractUnitTest;
import jakarta.inject.Inject;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link CredentialsController}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("management: credentials")
public class CredentialsApiTest extends AbstractUnitTest implements CredentialsApiTestSpec {

  @Inject CredentialsApiTestClient client;

  @DisplayName("findCredentials(200): success")
  @Test
  @Override
  public void findCredentials200() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    data.credentialRSA(gateway, data.generateKeyPair());
    data.credentialPSK(gateway);
    data.credentialPSK(gateway);
    var credentials =
        assert200(() -> client.findCredentials(auth(tenant), gateway.getGatewayId(), null));
    assertEquals(3, credentials.size(), "credentials");
  }

  @DisplayName("findCredentials(400): ambiguous tenants")
  @Test
  @Override
  public void findCredentials400() {
    var tenant1 = data.tenant();
    var tenant2 = data.tenant("inoa");
    var gateway = data.gateway(tenant1);
    data.credentialRSA(gateway, data.generateKeyPair());
    data.credentialPSK(gateway);
    data.credentialPSK(gateway);
    assert400(() -> client.findCredentials(auth(tenant1, tenant2), gateway.getGatewayId(), null));
  }

  @DisplayName("findCredentials(401): no token")
  @Test
  @Override
  public void findCredentials401() {
    assert401(() -> client.findCredentials(null, data.gatewayId(), null));
  }

  @DisplayName("findCredentials(404): not found")
  @Test
  @Override
  public void findCredentials404() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    assert404(
        "Gateway not found.", () -> client.findCredentials(auth(tenant), data.gatewayId(), null));
    assert404(
        "Gateway not found.",
        () -> client.findCredentials(auth(data.tenant()), gateway.getGatewayId(), null));
  }

  @DisplayName("findCredential(200): without secrets")
  @Test
  @Override
  public void findCredential200() {

    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var expected = data.credentialRSA(gateway, data.generateKeyPair());
    var actual =
        assert200(
            () ->
                client.findCredential(
                    auth(tenant), gateway.getGatewayId(), expected.getCredentialId(), null));

    assertEquals(expected.getCredentialId(), actual.getCredentialId(), "credentialId");
    assertEquals(expected.getName(), actual.getName(), "name");
    assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
    assertEquals(expected.getType(), actual.getType(), "type");
    assertEquals(expected.getCreated(), actual.getCreated(), "created");
    assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
  }

  @DisplayName("findCredential(400): ambiguous tenants")
  @Test
  @Override
  public void findCredential400() {
    var tenant1 = data.tenant();
    var tenant2 = data.tenant("inoa");
    var gateway = data.gateway(tenant1);
    var expected = data.credentialRSA(gateway, data.generateKeyPair());
    assert400(
        () ->
            client.findCredential(
                auth(tenant1, tenant2), gateway.getGatewayId(), expected.getCredentialId(), null));
  }

  @DisplayName("findCredential(401): no token")
  @Test
  @Override
  public void findCredential401() {
    assert401(() -> client.findCredential(null, data.gatewayId(), UUID.randomUUID(), null));
  }

  @DisplayName("findCredential(404): not found")
  @Test
  @Override
  public void findCredential404() {

    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var gatewayId = gateway.getGatewayId();
    var credential = data.credentialPSK(gateway);
    var credentialId = credential.getCredentialId();

    var auth = auth(tenant);
    var authOther = auth(data.tenant());
    assert404(
        "Credential not found.",
        () -> client.findCredential(auth, gatewayId, UUID.randomUUID(), null));
    assert404(
        "Gateway not found.",
        () -> client.findCredential(auth, data.gatewayId(), credentialId, null));
    assert404(
        "Gateway not found.",
        () -> client.findCredential(authOther, gatewayId, credentialId, null));
  }

  @DisplayName("createCredential(201): with mandatory properties")
  @Test
  @Override
  public void createCredential201() {

    var tenant = data.tenant();
    var gatewayId = data.gateway(tenant).getGatewayId();
    var vo =
        new CredentialCreateVO()
            .name(data.credentialName())
            .type(CredentialTypeVO.PSK)
            .value(UUID.randomUUID().toString().getBytes());
    var auth = auth(tenant);
    var created = assert201(() -> client.createCredential(auth, gatewayId, vo, null));

    assertNotNull(created.getCredentialId(), "credentialId");
    assertEquals(vo.getName(), created.getName(), "name");
    assertEquals(true, created.getEnabled(), "enabled");
    assertEquals(vo.getType(), created.getType(), "type");
    assertArrayEquals(vo.getValue(), created.getValue(), "value");
    assertNotNull(created.getCreated(), "created");
    assertNotNull(created.getUpdated(), "updated");
    assertEquals(
        created,
        assert200(() -> client.findCredential(auth, gatewayId, created.getCredentialId(), null)),
        "vo");
  }

  @DisplayName("createCredential(201): with optional properties")
  @Test
  public void createCredential201All() {

    var tenant = data.tenant();
    var gatewayId = data.gateway(tenant).getGatewayId();
    var vo =
        new CredentialCreateVO()
            .name(data.credentialName())
            .type(CredentialTypeVO.PSK)
            .enabled(false)
            .value(UUID.randomUUID().toString().getBytes());
    var auth = auth(tenant);
    var created = assert201(() -> client.createCredential(auth, gatewayId, vo, null));

    assertNotNull(created.getCredentialId(), "credentialId");
    assertEquals(vo.getName(), created.getName(), "name");
    assertEquals(false, created.getEnabled(), "enabled");
    assertEquals(vo.getType(), created.getType(), "type");
    assertArrayEquals(vo.getValue(), created.getValue(), "value");
    assertNotNull(created.getCreated(), "created");
    assertNotNull(created.getUpdated(), "updated");
    assertEquals(
        created,
        assert200(() -> client.findCredential(auth, gatewayId, created.getCredentialId(), null)),
        "vo");
  }

  @DisplayName("createCredential(400): is beanvalidation active")
  @Test
  @Override
  public void createCredential400() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    assert400(
        () ->
            client.createCredential(
                auth(tenant), gateway.getGatewayId(), new CredentialCreateVO(), null));
    assertEquals(0, data.countCredentials(gateway), "created");
  }

  @DisplayName("createCredential(400): rsa key invalid")
  @Disabled("NYI")
  @Test
  public void createCredential400KeyInvalid() {}

  @DisplayName("createCredential(401): no token")
  @Test
  @Override
  public void createCredential401() {
    assert401(
        () -> client.createCredential(null, data.gatewayId(), new CredentialCreateVO(), null));
  }

  @DisplayName("createCredential(404): not found")
  @Test
  @Override
  public void createCredential404() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var vo =
        new CredentialCreateVO()
            .name(data.credentialName())
            .type(CredentialTypeVO.PSK)
            .value(UUID.randomUUID().toString().getBytes());
    assert404(
        "Gateway not found.",
        () -> client.createCredential(auth(tenant), data.gatewayId(), vo, null));
    assert404(
        "Gateway not found.",
        () -> client.createCredential(auth(data.tenant()), gateway.getGatewayId(), vo, null));
    assertEquals(0, data.countCredentials(gateway), "created");
  }

  @DisplayName("createCredential(409): name already in use")
  @Test
  @Override
  public void createCredential409() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var name = data.credentialPSK(gateway).getName();
    var vo =
        new CredentialCreateVO()
            .name(name)
            .type(CredentialTypeVO.PSK)
            .value(UUID.randomUUID().toString().getBytes());
    assert409(() -> client.createCredential(auth(tenant), gateway.getGatewayId(), vo, null));
    assertEquals(1, data.countCredentials(gateway), "created");
  }

  @DisplayName("updateCredential(200): update nothing")
  @Test
  @Override
  public void updateCredential200() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var gatewayId = gateway.getGatewayId();
    var credential = data.credentialPSK(gateway);
    var credentialId = credential.getCredentialId();
    var vo = new CredentialUpdateVO().enabled(null).name(null);
    var auth = auth(tenant);
    var updated = assert200(() -> client.updateCredential(auth, gatewayId, credentialId, vo, null));
    assertEquals(credential.getCredentialId(), updated.getCredentialId(), "credentialId");
    assertEquals(credential.getName(), updated.getName(), "name");
    assertEquals(credential.getEnabled(), updated.getEnabled(), "enabled");
    assertEquals(credential.getType(), updated.getType(), "type");
    assertArrayEquals(credential.getValue(), updated.getValue(), "value");
    assertEquals(credential.getCreated(), updated.getCreated(), "created");
    assertEquals(credential.getUpdated(), updated.getUpdated(), "updated");
    assertEquals(
        updated, assert200(() -> client.findCredential(auth, gatewayId, credentialId, null)), "vo");
  }

  @DisplayName("updateCredential(200): update unchanged")
  @Test
  public void updateCredential200Unchanged() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var gatewayId = gateway.getGatewayId();
    var credential = data.credentialPSK(gateway);
    var credentialId = credential.getCredentialId();
    var vo = new CredentialUpdateVO().enabled(credential.getEnabled()).name(credential.getName());
    var auth = auth(tenant);
    var updated = assert200(() -> client.updateCredential(auth, gatewayId, credentialId, vo, null));
    assertEquals(credential.getCredentialId(), updated.getCredentialId(), "credentialId");
    assertEquals(credential.getName(), updated.getName(), "name");
    assertEquals(credential.getEnabled(), updated.getEnabled(), "enabled");
    assertEquals(credential.getType(), updated.getType(), "type");
    assertArrayEquals(credential.getValue(), updated.getValue(), "value");
    assertEquals(credential.getCreated(), updated.getCreated(), "created");
    assertEquals(credential.getUpdated(), updated.getUpdated(), "updated");
    assertEquals(
        updated, assert200(() -> client.findCredential(auth, gatewayId, credentialId, null)), "vo");
  }

  @DisplayName("updateCredential(200): update name")
  @Test
  public void updateCredential200Name() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var gatewayId = gateway.getGatewayId();
    var credential = data.credentialPSK(gateway);
    var credentialId = credential.getCredentialId();
    var vo = new CredentialUpdateVO().enabled(null).name(data.credentialName());
    var auth = auth(tenant);
    var updated = assert200(() -> client.updateCredential(auth, gatewayId, credentialId, vo, null));
    assertEquals(credential.getCredentialId(), updated.getCredentialId(), "credentialId");
    assertEquals(vo.getName(), updated.getName(), "name");
    assertEquals(credential.getEnabled(), updated.getEnabled(), "enabled");
    assertEquals(credential.getType(), updated.getType(), "type");
    assertArrayEquals(credential.getValue(), updated.getValue(), "value");
    assertEquals(credential.getCreated(), updated.getCreated(), "created");
    assertTrue(updated.getUpdated().isAfter(credential.getUpdated()), "updated");
    assertEquals(
        updated, assert200(() -> client.findCredential(auth, gatewayId, credentialId, null)), "vo");
  }

  @DisplayName("updateCredential(200): update enabled")
  @Test
  public void updateCredential200Enabled() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var gatewayId = gateway.getGatewayId();
    var credential = data.credentialPSK(gateway);
    var credentialId = credential.getCredentialId();
    var vo = new CredentialUpdateVO().enabled(!credential.getEnabled()).name(null);
    var auth = auth(tenant);
    var updated = assert200(() -> client.updateCredential(auth, gatewayId, credentialId, vo, null));
    assertEquals(credential.getCredentialId(), updated.getCredentialId(), "credentialId");
    assertEquals(credential.getName(), updated.getName(), "name");
    assertEquals(vo.getEnabled(), updated.getEnabled(), "enabled");
    assertEquals(credential.getType(), updated.getType(), "type");
    assertArrayEquals(credential.getValue(), updated.getValue(), "value");
    assertEquals(credential.getCreated(), updated.getCreated(), "created");
    assertTrue(updated.getUpdated().isAfter(credential.getUpdated()), "updated");
    assertEquals(
        updated, assert200(() -> client.findCredential(auth, gatewayId, credentialId, null)), "vo");
  }

  @DisplayName("updateCredential(204): update all")
  @Test
  public void updateCredential200All() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var gatewayId = gateway.getGatewayId();
    var credential = data.credentialPSK(gateway);
    var credentialId = credential.getCredentialId();
    var vo = new CredentialUpdateVO().enabled(!credential.getEnabled()).name(data.credentialName());
    var auth = auth(tenant);
    var updated = assert200(() -> client.updateCredential(auth, gatewayId, credentialId, vo, null));
    assertEquals(credential.getCredentialId(), updated.getCredentialId(), "credentialId");
    assertEquals(vo.getName(), updated.getName(), "name");
    assertEquals(vo.getEnabled(), updated.getEnabled(), "enabled");
    assertEquals(credential.getType(), updated.getType(), "type");
    assertArrayEquals(credential.getValue(), updated.getValue(), "value");
    assertEquals(credential.getCreated(), updated.getCreated(), "created");
    assertTrue(updated.getUpdated().isAfter(credential.getUpdated()), "updated");
    assertEquals(
        updated, assert200(() -> client.findCredential(auth, gatewayId, credentialId, null)), "vo");
  }

  @DisplayName("updateCredential(400): is beanvalidation active")
  @Test
  @Override
  public void updateCredential400() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant).setLocation(data.getNullLocation());
    var gatewayId = gateway.getGatewayId();
    var credentialId = data.credentialPSK(gateway).getCredentialId();
    var vo = new CredentialUpdateVO().name("");
    assert400(() -> client.updateCredential(auth(tenant), gatewayId, credentialId, vo, null));
    assertEquals(gateway, data.find(gateway), "entity changed");
  }

  @DisplayName("updateCredential(400): rsa key invalid")
  @Disabled("NYI")
  @Test
  public void updateCredential400KeyInvalid() {}

  @DisplayName("updateCredential(401): no token")
  @Test
  @Override
  public void updateCredential401() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant).setLocation(data.getNullLocation());
    var gatewayId = gateway.getGatewayId();
    var credentialId = data.credentialPSK(gateway).getCredentialId();
    var vo = new CredentialUpdateVO().name(data.gatewayName());
    assert401(() -> client.updateCredential(null, gatewayId, credentialId, vo, null));
    assertEquals(gateway, data.find(gateway), "entity changed");
  }

  @DisplayName("updateCredential(404): not found")
  @Test
  @Override
  public void updateCredential404() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var gatewayId = gateway.getGatewayId();
    var credentialId = data.credentialPSK(gateway).getCredentialId();
    var vo = new CredentialUpdateVO();
    assert404(
        "Credential not found.",
        () -> client.updateCredential(auth(tenant), gatewayId, UUID.randomUUID(), vo, null));
    assert404(
        "Gateway not found.",
        () -> client.updateCredential(auth(tenant), data.gatewayId(), credentialId, vo, null));
    assert404(
        "Gateway not found.",
        () -> client.updateCredential(auth(data.tenant()), gatewayId, credentialId, vo, null));
  }

  @DisplayName("updateCredential(409): name already in use")
  @Test
  @Override
  public void updateCredential409() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var gatewayId = gateway.getGatewayId();
    var credential = data.credentialPSK(gateway);
    var otherCredential = data.credentialPSK(gateway);
    var vo = new CredentialUpdateVO().name(otherCredential.getName());
    assert409(
        () ->
            client.updateCredential(
                auth(tenant), gatewayId, credential.getCredentialId(), vo, null));
  }

  @DisplayName("deleteCredential(204): without secret")
  @Test
  @Override
  public void deleteCredential204() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var credential = data.credentialPSK(gateway);
    assert204(
        () ->
            client.deleteCredential(
                auth(tenant), gateway.getGatewayId(), credential.getCredentialId(), null));
    assertEquals(0, data.countCredentials(gateway), "credential not deleted");
  }

  @DisplayName("deleteCredential(400): ambiguous tenant")
  @Test
  @Override
  public void deleteCredential400() {
    var tenant1 = data.tenant();
    var tenant2 = data.tenant("inoa");
    var gateway = data.gateway(tenant1);
    var credential = data.credentialPSK(gateway);
    assert400(
        () ->
            client.deleteCredential(
                auth(tenant1, tenant2),
                gateway.getGatewayId(),
                credential.getCredentialId(),
                null));
  }

  @DisplayName("deleteCredential(204): with secret")
  @Test
  public void deleteCredential204WithSecret() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var credential = data.credentialPSK(gateway);
    assert204(
        () ->
            client.deleteCredential(
                auth(tenant), gateway.getGatewayId(), credential.getCredentialId(), null));
    assertEquals(0, data.countCredentials(gateway), "credential not deleted");
  }

  @DisplayName("deleteCredential(401): no token")
  @Test
  @Override
  public void deleteCredential401() {
    assert401(() -> client.deleteCredential(null, data.gatewayId(), UUID.randomUUID(), null));
  }

  @DisplayName("deleteCredential(404): not found")
  @Test
  @Override
  public void deleteCredential404() {

    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var gatewayId = gateway.getGatewayId();
    var credential = data.credentialPSK(gateway);
    var credentialId = credential.getCredentialId();

    var auth = auth(tenant);
    var authOther = auth(data.tenant());
    assert404(
        "Credential not found.",
        () -> client.deleteCredential(auth, gatewayId, UUID.randomUUID(), null));
    assert404(
        "Gateway not found.",
        () -> client.deleteCredential(auth, data.gatewayId(), credentialId, null));
    assert404(
        "Gateway not found.",
        () -> client.deleteCredential(authOther, gatewayId, credentialId, null));
    assertEquals(1, data.countCredentials(gateway), "deleted");
  }
}

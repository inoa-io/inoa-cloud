package io.inoa.fleet.registry.rest.mapper;

import io.inoa.fleet.registry.domain.Credential;
import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.rest.CredentialCreateVO;
import io.inoa.rest.CredentialVO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

/**
 * Mapper for {@link Credential}.
 *
 * @author Stephan Schnabel
 */
@Mapper(componentModel = ComponentModel.JAKARTA)
public interface CredentialMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "gateway", source = "gateway")
  @Mapping(target = "credentialId", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "name", source = "credential.name")
  @Mapping(target = "enabled", source = "credential.enabled")
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "updated", ignore = true)
  Credential toCredential(Gateway gateway, CredentialCreateVO credential);

  CredentialVO toCredential(Credential credential);

  List<CredentialVO> toCredentials(List<Credential> credentials);
}

UPDATE tenant_configuration SET value='ssl://${k3s.ip}:8883' WHERE definition_id=(SELECT id FROM configuration_definition WHERE KEY='mqtt.url');

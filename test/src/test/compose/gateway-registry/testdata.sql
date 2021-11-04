INSERT INTO tenant(id,tenant_id,name,enabled,created,updated) VALUES (1,'tenant-a','Tenant A',true,'2021-01-01 00:00:00','2021-01-01 00:00:00');
INSERT INTO configuration_definition(id,tenant_id,key,type,description,pattern) VALUES (1,1,'mqtt.url','STRING','URL to use vor MQTT.','tcp:\/\/[a-z0-9-]+:[0-9]+');
INSERT INTO tenant_configuration(definition_id,value) VALUES (1,'tcp://hono-adapter-mqtt:1883');

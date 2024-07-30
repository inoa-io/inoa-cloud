CREATE TABLE thing_type
(
    id                   SERIAL         NOT NULL,
    thing_type_id        VARCHAR(100)   NOT NULL,
    name                 VARCHAR(100)   NOT NULL,
    category             VARCHAR(100)   NOT NULL,
    json_schema          VARCHAR(10000) NOT NULL,
    created              TIMESTAMP      NOT NULL,
    updated              TIMESTAMP      NOT NULL,
    CONSTRAINT pk_thing_type PRIMARY KEY (id),
    CONSTRAINT uq_thing_type_thing_type_id UNIQUE (thing_type_id)
);

CREATE TABLE thing
(
    id            SERIAL         NOT NULL,
    thing_id      UUID           NOT NULL,
    tenant_id     VARCHAR(100)   NOT NULL,
    gateway_id    VARCHAR(100)   NULL,
    urn           VARCHAR(255)   NULL,
    name          VARCHAR(100)   NOT NULL,
    config        VARCHAR(10000) NOT NULL,
    thing_type_id INTEGER        NOT NULL,
    created       TIMESTAMP      NOT NULL,
    updated       TIMESTAMP      NOT NULL,
    CONSTRAINT pk_thing PRIMARY KEY (id),
    CONSTRAINT uq_thing_thing_id UNIQUE (thing_id),
    CONSTRAINT fk_thing_thing_type_id FOREIGN KEY (thing_type_id) REFERENCES thing_type (id)
);
INSERT INTO thing_type (id,thing_type_id,name,category,json_schema,created,updated) VALUES (1, 'dvh4013', 'DZG DVH4013', 'energy_meter', '{"title":"DZG - DVH 4013 Power Meter","description":"a bi-directional power meter","type":"object","required":["name","serial","modbus_interface"],"properties":{"name":{"type":"string","default":"My new DVH4013","title":"Name your thing..."},"serial":{"type":"string","title":"The serial number of the energy meter" },"modbus_interface":{"title":"RS485 interface where the power meter is connected.","type":"number","default":0,"widget":{"formlyConfig":{"type":"enum","props":{"options":[{"value":0,"label":"RS485 - Port 1"},{"value": 1,"label":"RS485 - Port 2"}]}}}}}}', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO thing_type (id,thing_type_id,name,category,json_schema,created,updated) VALUES (2, 'mdvh4006', 'DZG MDVH4006', 'energy_meter', '{"title":"MDVH4006","description":"a bi-directional power meter","type":"object","required":["name","serial","modbus_interface"],"properties":{"name":{"type":"string","default":"My new MDVH4006","title":"Name your thing..."},"serial":{"type":"string","title":"The serial number of the energy meter"},"modbus_interface":{"title":"RS485 interface where the power meter is connected.","type":"number","default": 0,"widget":{"formlyConfig":{"type":"enum","props":{"options":[{"value":0,"label":"RS485 - Port 1"},{"value":1,"label":"RS485 - Port 2"}]}}}}}}', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO thing_type (id,thing_type_id,name,category,json_schema,created,updated) VALUES (3, 'shplg-s', 'Shelly Plug S', 'smart_plug', '{"title":"Shelly Plug S","description":"a smart plug made by Shelly.","type":"object","required":["name","host"],"properties":{"name":{"type":"string","default":"My new Shelly Plug S","title":"Name your thing..."},"host":{"type":"string","title":"Host"}}}', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO thing_type (id,thing_type_id,name,category,json_schema,created,updated) VALUES (4, 'shplg-pm2', 'Shelly Plus PM2', 'smart_plug', '{"title":"Shelly Plus PM2","description":"a smart plug made by Shelly.","type":"object","required":["name","host"],"properties":{"name":{"type":"string","default":"My new Shelly Plus PM2","title":"Name your thing..."},"host":{"type":"string","title":"Host"}}}', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

CREATE
    TABLE
        thing_type(
            id SERIAL NOT NULL,
            thing_type_id VARCHAR(100) NOT NULL,
            name VARCHAR(100) NOT NULL,
            json_schema VARCHAR(10000) NOT NULL,
            ui_layout VARCHAR(10000) NOT NULL,
            created TIMESTAMP NOT NULL,
            updated TIMESTAMP NOT NULL,
            CONSTRAINT pk_thing_type PRIMARY KEY(id),
            CONSTRAINT uq_thing_type_thing_type_id UNIQUE(thing_type_id)
        );

CREATE
    TABLE
        thing(
            id SERIAL NOT NULL,
            thing_id UUID NOT NULL,
            tenant_id VARCHAR(100) NOT NULL,
            gateway_id VARCHAR(100) NULL,
            urn VARCHAR(255) NULL,
            name VARCHAR(100) NOT NULL,
            config VARCHAR(10000) NOT NULL,
            thing_type_id INTEGER NOT NULL,
            created TIMESTAMP NOT NULL,
            updated TIMESTAMP NOT NULL,
            CONSTRAINT pk_thing PRIMARY KEY(id),
            CONSTRAINT uq_thing_thing_id UNIQUE(thing_id),
            CONSTRAINT fk_thing_thing_type_id FOREIGN KEY(thing_type_id) REFERENCES thing_type(id)
        );

INSERT
    INTO
        thing_type(
            id,
            thing_type_id,
            name,
            json_schema,
            ui_layout,
            created,
            updated
        )
    VALUES(
        1,
        'dvmodbusir',
        'DvModbusIR',
        '{"type":"object","properties":{"properties":{"type":"object","properties":{"serial":{"type":"number"},"modbus_interface":{"type":"number","enum":[1,2]}},"required":["serial","modbus_interface"]},"channels":{"type":"object","properties":{"obis_1_8_0":{"type":"boolean"},"obis_1_8_1":{"type":"boolean"},"obis_1_8_2":{"type":"boolean"},"obis_2_8_0":{"type":"boolean"},"obis_2_8_1":{"type":"boolean"},"obis_2_8_2":{"type":"boolean"},"obis_1_7_0":{"type":"boolean"}}}}}',
        '[{"key":"properties.serial","title":"Serial","placeholder":"Serial"},{"key":"properties.modbus_interface","title":"Modbus Interface"},{"type":"div","display":"flex","flex-direction":"row","title":"Channels","items":[{"key":"channels.obis_1_8_0","title":"Work In 1-0:1.8.0*255"},{"key":"channels.obis_1_8_1","title":"Work In 1-0:1.8.1*255"},{"key":"channels.obis_1_8_2","title":"Work In 1-0:1.8.2*255"},{"key":"channels.obis_2_8_0","title":"Work Out 1-0:2.8.0*255"},{"key":"channels.obis_2_8_1","title":"Work Out 1-0:2.8.1*255"},{"key":"channels.obis_2_8_2","title":"Work Out 1-0:2.8.2*255"},{"key":"channels.obis_1_7_0","title":"Power In 1-0:1.7.0*255"}]}]',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT
    INTO
        thing_type(
            id,
            thing_type_id,
            name,
            json_schema,
            ui_layout,
            created,
            updated
        )
    VALUES(
        4,
        's0',
        'S0',
        '{"type":"object","properties":{"properties":{"type":"object","properties":{"serial":{"type":"string"},"sensor":{"type":"string","enum":["gas"]},"interface":{"type":"number","enum":[1,2]}},"required":["serial","sensor","interface"]}}}',
        '[{"key":"properties.serial","title":"Serial","placeholder":"Serial"},{"key":"properties.sensor","title":"Sensor"},{"key":"properties.interface","title":"Interface"}]',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT
    INTO
        thing_type(
            id,
            thing_type_id,
            name,
            json_schema,
            ui_layout,
            created,
            updated
        )
    VALUES(
        5,
        'dvh4013',
        'DZG DVH4013',
        '{"type":"object","properties":{"properties":{"type":"object","properties":{"serial":{"type":"number"},"modbus_interface":{"type":"number","enum":[1,2]}},"required":["serial","modbus_interface"]},"channels":{"type":"object","properties":{"obis_1_8_0":{"type":"boolean"},"obis_2_8_0":{"type":"boolean"},"power_in":{"type":"boolean"},"power_out":{"type":"boolean"}}}}}',
        '[{"key":"properties.serial","title":"Serial","placeholder":"Serial"},{"key":"properties.modbus_interface","title":"Modbus Interface"},{"type":"div","display":"flex","flex-direction":"row","title":"Channels","items":[{"key":"channels.obis_1_8_0","title":"Work In 1-0:1.8.0*255"},{"key":"channels.obis_2_8_0","title":"Work Out 1-0:2.8.0*255"},{"key":"channels.power_in","title":"Power In"},{"key":"channels.power_out","title":"Power Out"}]}]',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT
    INTO
        thing_type(
            id,
            thing_type_id,
            name,
            json_schema,
            ui_layout,
            created,
            updated
        )
    VALUES(
        6,
        'shplg-s',
        'Shelly Plug S',
        '{"type":"object","properties":{"properties":{"type":"object","properties":{"serial":{"type":"string"},"uri":{"type":"string"}},"required":["serial","uri"]}}}',
        '[{"key":"properties.serial","title":"Serial","placeholder":"Serial"},{"key":"properties.uri","title":"Host","placeholder":"Host"}]',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT
    INTO
        thing_type(
            id,
            thing_type_id,
            name,
            json_schema,
            ui_layout,
            created,
            updated
        )
    VALUES(
        7,
        'mdvh4006',
        'DZG MDVH4006',
        '{"type":"object","properties":{"properties":{"type":"object","properties":{"serial":{"type":"number"},"modbus_interface":{"type":"number","enum":[1,2]}}},"channels":{"type":"object","properties":{"obis_1_8_0":{"type":"boolean"},"obis_2_8_0":{"type":"boolean"},"power_in":{"type":"boolean"},"power_out":{"type":"boolean"}}}}}',
        '[{"key":"properties.serial","title":"Serial","placeholder":"Serial"},{"key":"properties.modbus_interface","title":"Modbus Interface"},{"type":"div","display":"flex","flex-direction":"row","title":"Channels","items":[{"key":"channels.obis_1_8_0","title":"Work In 1-0:1.8.0*255"},{"key":"channels.obis_2_8_0","title":"Work Out 1-0:2.8.0*255"},{"key":"channels.power_in","title":"Power In"},{"key":"channels.power_out","title":"Power Out"}]}]',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );
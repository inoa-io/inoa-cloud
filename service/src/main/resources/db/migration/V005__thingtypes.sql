DROP
    TABLE
        thing;

DROP
    TABLE
        thing_type;

CREATE
    TABLE
        thing_type(
            id SERIAL NOT NULL,
            identifier VARCHAR(128) NOT NULL,
            name VARCHAR(256) NOT NULL,
            description VARCHAR(4096),
            version VARCHAR(128),
            protocol VARCHAR(16) NOT NULL,
            CONSTRAINT pk_thing_type PRIMARY KEY(id),
            CONSTRAINT uq_thing_type UNIQUE(
                identifier,
                version
            ),
            CONSTRAINT chk_protocol CHECK(
                protocol ~ '^JSON_REST_HTTP|MODBUS_RS458|MODBUS_TCP|S0|MBUS|WMBUS$'
            )
        );

CREATE
    TABLE
        thing(
            id SERIAL NOT NULL,
            thing_id UUID NOT NULL,
            tenant_id INTEGER NOT NULL,
            urn VARCHAR(256) NOT NULL,
            name VARCHAR(256) NOT NULL,
            description VARCHAR(4096),
            gateway_id INTEGER NOT NULL,
            thing_type INTEGER NOT NULL,
            CONSTRAINT pk_thing PRIMARY KEY(id),
            CONSTRAINT uq_thing_thing_id UNIQUE(thing_id),
            CONSTRAINT fk_thing_thing_tenant FOREIGN KEY(tenant_id) REFERENCES tenant(id),
            CONSTRAINT fk_thing_thing_type FOREIGN KEY(thing_type) REFERENCES thing_type(id),
            CONSTRAINT fk_thing_gateway FOREIGN KEY(gateway_id) REFERENCES gateway(id)
        );

CREATE
    TABLE
        measurand_type(
            id SERIAL NOT NULL,
            obis_id VARCHAR(64) NOT NULL,
            name VARCHAR(256) NOT NULL,
            description VARCHAR(4096),
            CONSTRAINT pk_measurand_type PRIMARY KEY(id),
            CONSTRAINT uq_measurend_type_obis_id UNIQUE(obis_id)
        );

CREATE
    TABLE
        thing_configuration(
            id SERIAL NOT NULL,
            name VARCHAR(256) NOT NULL,
            description VARCHAR(4096) NOT NULL,
            thing_type_id INTEGER NOT NULL,
            TYPE VARCHAR(8) NOT NULL,
            validation_regex VARCHAR(4096),
            CONSTRAINT pk_thing_configuration PRIMARY KEY(id),
            CONSTRAINT fk_thing_configuration_thing_type FOREIGN KEY(thing_type_id) REFERENCES thing_type(id),
            CONSTRAINT uq_thing_configuration_name_thing_type UNIQUE(
                name,
                thing_type_id
            ),
            CONSTRAINT chk_type CHECK(
                TYPE ~ '^STRING|NUMBER|BOOLEAN$'
            )
        );

CREATE
    TABLE
        thing_configuration_value(
            thing_id INTEGER NOT NULL,
            thing_configuration_id INTEGER NOT NULL,
            value VARCHAR(8192),
            CONSTRAINT pk_thing_configuration_value PRIMARY KEY(
                thing_id,
                thing_configuration_id
            ),
            CONSTRAINT fk_thing_configuration_value_thing FOREIGN KEY(thing_id) REFERENCES thing(id),
            CONSTRAINT fk_thing_configuration_value_thing_configuration FOREIGN KEY(thing_configuration_id) REFERENCES thing_configuration(id)
        );

CREATE
    TABLE
        thing_type_measurand_type(
            thing_type_id INTEGER NOT NULL,
            measurand_type_id INTEGER NOT NULL,
            CONSTRAINT pk_thing_type_measurand_type PRIMARY KEY(
                thing_type_id,
                measurand_type_id
            ),
            CONSTRAINT fk_thing_type_measurand_types_thing_type FOREIGN KEY(thing_type_id) REFERENCES thing_type(id),
            CONSTRAINT fk_thing_type_measurand_types_measurand_type FOREIGN KEY(measurand_type_id) REFERENCES measurand_type(id)
        );

CREATE
    TABLE
        measurand(
            id SERIAL NOT NULL,
            measurand_type_id INTEGER NOT NULL,
            thing_id INTEGER NOT NULL,
            enabled BOOLEAN NOT NULL,
            INTERVAL INTEGER NOT NULL,
            timeout INTEGER NOT NULL,
            CONSTRAINT pk_measurand PRIMARY KEY(id),
            CONSTRAINT fk_measurand_measurand_type FOREIGN KEY(measurand_type_id) REFERENCES measurand_type(id),
            CONSTRAINT fk_measurand_thing FOREIGN KEY(thing_id) REFERENCES thing(id),
            CONSTRAINT uq_measurand_thing_measurand_type UNIQUE(
                thing_id,
                measurand_type_id
            )
        );

INSERT
    INTO
        thing_type
    VALUES(
        1,
        'dvh4013',
        'DZG DVH4013',
        'DZG DVH4013 bi-directional power meter',
        NULL,
        'MODBUS_RS458'
    );

INSERT
    INTO
        thing_type
    VALUES(
        2,
        'mdvh4006',
        'DZG MDVH4006',
        'DZG MDVH4006 bi-directional load profile power meter',
        NULL,
        'MODBUS_RS458'
    );

INSERT
    INTO
        measurand_type
    VALUES(
        1,
        '1-0:1.8.0',
        'Electric Energy In',
        'Active energy consumed from the grid in kWh'
    );

INSERT
    INTO
        measurand_type
    VALUES(
        2,
        '1-0:2.8.0',
        'Electric Energy Out',
        'Active energy returned to the grid in kWh'
    );

INSERT
    INTO
        measurand_type
    VALUES(
        3,
        '7-20:3.0.0',
        'Gas In',
        'Gas consumed from the grid in m³'
    );

INSERT
    INTO
        thing_type_measurand_type
    VALUES(
        1,
        1
    );

INSERT
    INTO
        thing_type_measurand_type
    VALUES(
        1,
        2
    );

INSERT
    INTO
        thing_type_measurand_type
    VALUES(
        2,
        1
    );

INSERT
    INTO
        thing_type_measurand_type
    VALUES(
        2,
        2
    );

INSERT
    INTO
        thing_configuration
    VALUES(
        1,
        'Serial',
        'Serial number',
        1,
        'STRING',
        '[0-9]*'
    );

INSERT
    INTO
        thing_configuration
    VALUES(
        2,
        'Modbus Interface',
        'Modbus interface the meter is connected to',
        1,
        'NUMBER',
        NULL
    );

INSERT
    INTO
        thing_configuration
    VALUES(
        3,
        'Serial',
        'Serial number',
        2,
        'STRING',
        '[0-9]*'
    );

INSERT
    INTO
        thing_configuration
    VALUES(
        4,
        'Modbus Interface',
        'Modbus interface the meter is connected to',
        2,
        'NUMBER',
        NULL
    );

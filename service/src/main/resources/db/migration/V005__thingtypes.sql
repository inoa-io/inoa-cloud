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
            category VARCHAR(19) NOT NULL DEFAULT 'NONE',
            version VARCHAR(128),
            protocol VARCHAR(16) NOT NULL,
            CONSTRAINT pk_thing_type PRIMARY KEY(id),
            CONSTRAINT uq_thing_type UNIQUE(
                identifier,
                version
            ),
            CONSTRAINT chk_protocol CHECK(
                protocol ~ '^JSON_REST_HTTP|MODBUS_RS458|MODBUS_TCP|S0|MBUS|WMBUS|ADC|RMS$'
            ),
            CONSTRAINT chk_category CHECK(
                category ~ '^NONE|ELECTRIC_METER|GAS_METER|CURRENT_TRANSFORMER|SMART_PLUG$'
            )
        );

CREATE
    TABLE
        thing(
            id SERIAL NOT NULL,
            thing_id UUID NOT NULL,
            name VARCHAR(256) NOT NULL,
            description VARCHAR(4096),
            gateway_id INTEGER NOT NULL,
            thing_type_id INTEGER NOT NULL,
            CONSTRAINT pk_thing PRIMARY KEY(id),
            CONSTRAINT uq_thing_thing_id UNIQUE(thing_id),
            CONSTRAINT uq_thing_name_tenant UNIQUE(
                name,
                gateway_id
            ),
            CONSTRAINT fk_thing_thing_type FOREIGN KEY(thing_type_id) REFERENCES thing_type(id),
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
            CONSTRAINT fk_thing_configuration_thing_type FOREIGN KEY(thing_type_id) REFERENCES thing_type(id) ON
            DELETE
                CASCADE,
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
            id SERIAL NOT NULL,
            thing_id INTEGER NOT NULL,
            thing_configuration_id INTEGER NOT NULL,
            value VARCHAR(8192),
            CONSTRAINT pk_thing_configuration_value PRIMARY KEY(id),
            CONSTRAINT fk_thing_configuration_value_thing FOREIGN KEY(thing_id) REFERENCES thing(id) ON
            DELETE
                CASCADE,
                CONSTRAINT fk_thing_configuration_value_thing_configuration FOREIGN KEY(thing_configuration_id) REFERENCES thing_configuration(id) ON
                DELETE
                    CASCADE
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
            CONSTRAINT fk_thing_type_measurand_types_thing_type FOREIGN KEY(thing_type_id) REFERENCES thing_type(id) ON
            DELETE
                CASCADE,
                CONSTRAINT fk_thing_type_measurand_types_measurand_type FOREIGN KEY(measurand_type_id) REFERENCES measurand_type(id) ON
                DELETE
                    CASCADE
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
            CONSTRAINT fk_measurand_measurand_type FOREIGN KEY(measurand_type_id) REFERENCES measurand_type(id) ON
            DELETE
                CASCADE,
                CONSTRAINT fk_measurand_thing FOREIGN KEY(thing_id) REFERENCES thing(id) ON
                DELETE
                    CASCADE,
                    CONSTRAINT uq_measurand_thing_measurand_type UNIQUE(
                        thing_id,
                        measurand_type_id
                    )
        );

INSERT
    INTO
        thing_type
    VALUES(
        DEFAULT,
        'dvh4013',
        'DZG DVH4013',
        'DZG DVH4013 bi-directional power meter',
        'ELECTRIC_METER',
        NULL,
        'MODBUS_RS458'
    );

INSERT
    INTO
        thing_type
    VALUES(
        DEFAULT,
        'mdvh4006',
        'DZG MDVH4006',
        'DZG MDVH4006 bi-directional load profile power meter',
        'ELECTRIC_METER',
        NULL,
        'MODBUS_RS458'
    );

INSERT
    INTO
        measurand_type
    VALUES(
        DEFAULT,
        '0.2.0',
        'Firmware version',
        'Firmware of the meter'
    );

INSERT
    INTO
        measurand_type
    VALUES(
        DEFAULT,
        'C.1.0',
        'Meter serial number',
        'Serial number of the meter'
    );

INSERT
    INTO
        measurand_type
    VALUES(
        DEFAULT,
        'S.1.1.9',
        'Temperature',
        'Environmental temperature of the meter in °C'
    );

INSERT
    INTO
        measurand_type
    VALUES(
        DEFAULT,
        '1-0:1.8.0',
        'Electric energy in',
        'Active energy consumed from the grid in kWh'
    );

INSERT
    INTO
        measurand_type
    VALUES(
        DEFAULT,
        '1-0:2.8.0',
        'Electric energy out',
        'Active energy returned to the grid in kWh'
    );

INSERT
    INTO
        measurand_type
    VALUES(
        DEFAULT,
        '1-0:1.7.0',
        'Electric power in',
        'Positive active instantaneous power in kW'
    );

INSERT
    INTO
        measurand_type
    VALUES(
        DEFAULT,
        '1-0:2.7.0',
        'Electric power out',
        'Negative active instantaneous power in kW'
    );

INSERT
    INTO
        measurand_type
    VALUES(
        DEFAULT,
        '1-0:11.7.0',
        'Current',
        'Instantaneous current in A'
    );

INSERT
    INTO
        measurand_type
    VALUES(
        DEFAULT,
        '1-0:12.7.0',
        'Voltage',
        'Instantaneous voltage in V'
    );

INSERT
    INTO
        measurand_type
    VALUES(
        DEFAULT,
        '1-0:14.7.0',
        'AC frequency',
        'Frequency of signal in the installation in Hz'
    );

INSERT
    INTO
        measurand_type
    VALUES(
        DEFAULT,
        '7-20:3.0.0',
        'Gas In',
        'Gas consumed from the grid in m³'
    );

INSERT
    INTO
        thing_type_measurand_type(
            thing_type_id,
            measurand_type_id
        )
    VALUES(
        1,
        1
    ),
    (
        1,
        4
    ),
    (
        1,
        5
    ),
    (
        1,
        6
    ),
    (
        1,
        7
    ),
    (
        1,
        8
    ),
    (
        1,
        9
    ),
    (
        1,
        10
    ),
    (
        2,
        1
    ),
    (
        2,
        4
    ),
    (
        2,
        5
    ),
    (
        2,
        6
    ),
    (
        2,
        7
    ),
    (
        2,
        8
    ),
    (
        2,
        9
    ),
    (
        2,
        10
    );

INSERT
    INTO
        thing_configuration
    VALUES(
        DEFAULT,
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
        DEFAULT,
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
        DEFAULT,
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
        DEFAULT,
        'Modbus Interface',
        'Modbus interface the meter is connected to',
        2,
        'NUMBER',
        NULL
    );

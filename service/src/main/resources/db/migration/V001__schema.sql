CREATE
    TABLE
        tenant(
            id SERIAL NOT NULL,
            tenant_id VARCHAR(30) NOT NULL,
            name VARCHAR(100) NOT NULL,
            enabled BOOLEAN NOT NULL,
            gateway_id_pattern VARCHAR(100) NOT NULL,
            created TIMESTAMP NOT NULL,
            updated TIMESTAMP NOT NULL,
            deleted TIMESTAMP NULL,
            CONSTRAINT pk_tenant PRIMARY KEY(id),
            CONSTRAINT uq_tenant_tenant_id UNIQUE(tenant_id),
            CONSTRAINT chk_tenant_id CHECK(
                tenant_id ~ '^[a-z0-9\-]{4,30}$'
            ),
            CONSTRAINT chk_tenant_name CHECK(
                LENGTH(name)>= 4
            )
        );

CREATE
    TABLE
        "group"(
            id SERIAL NOT NULL,
            group_id UUID NOT NULL,
            tenant_id INTEGER NOT NULL,
            name VARCHAR(20) NOT NULL,
            created TIMESTAMP NOT NULL,
            updated TIMESTAMP NOT NULL,
            CONSTRAINT pk_group PRIMARY KEY(id),
            CONSTRAINT uq_group_group_id UNIQUE(
                tenant_id,
                group_id
            ),
            CONSTRAINT uq_group_name UNIQUE(
                tenant_id,
                name
            ),
            CONSTRAINT chk_group_name CHECK(
                name ~ '^[a-zA-Z0-9\-]{3,20}$'
            ),
            CONSTRAINT fk_group_tenant FOREIGN KEY(tenant_id) REFERENCES tenant(id) ON
            DELETE
                CASCADE
        );

CREATE
    TABLE
        gateway(
            id SERIAL NOT NULL,
            gateway_id VARCHAR(20) NOT NULL,
            tenant_id INTEGER NOT NULL,
            name VARCHAR(100) NULL,
            enabled BOOLEAN NOT NULL,
            mqtt_timestamp TIMESTAMP NULL,
            mqtt_connected BOOLEAN NOT NULL,
            created TIMESTAMP NOT NULL,
            updated TIMESTAMP NOT NULL,
            CONSTRAINT pk_gateway PRIMARY KEY(id),
            CONSTRAINT uq_gateway_gateway_id UNIQUE(gateway_id),
            CONSTRAINT chk_gateway_id CHECK(
                gateway_id ~ '^[A-Z][A-Z0-9\-_]{3,19}$'
            ),
            CONSTRAINT fk_gateway_tenant FOREIGN KEY(tenant_id) REFERENCES tenant(id)
        );

CREATE
    TABLE
        gateway_property(
            gateway_id INTEGER NOT NULL,
            KEY VARCHAR(100) NOT NULL,
            value VARCHAR(1000) NOT NULL,
            created TIMESTAMP NOT NULL,
            updated TIMESTAMP NOT NULL,
            CONSTRAINT pk_gateway_property PRIMARY KEY(
                gateway_id,
                KEY
            ),
            CONSTRAINT chk_gateway_property_key CHECK(
                KEY ~ '^[a-z0-9_\-\.]{2,100}$'
            ),
            CONSTRAINT fk_gateway_property FOREIGN KEY(gateway_id) REFERENCES gateway(id) ON
            DELETE
                CASCADE
        );

CREATE
    TABLE
        gateway_group(
            gateway_id INTEGER NOT NULL,
            group_id INTEGER NOT NULL,
            created TIMESTAMP NOT NULL,
            CONSTRAINT pk_gateway_group PRIMARY KEY(
                gateway_id,
                group_id
            ),
            CONSTRAINT fk_gateway_group_gateway FOREIGN KEY(gateway_id) REFERENCES gateway(id) ON
            DELETE
                CASCADE,
                CONSTRAINT fk_gateway_group_group FOREIGN KEY(group_id) REFERENCES "group"(id) ON
                DELETE
                    CASCADE
        );

CREATE
    TABLE
        credential(
            id SERIAL NOT NULL,
            gateway_id INTEGER NOT NULL,
            credential_id UUID NOT NULL,
            name VARCHAR(32) NOT NULL,
            TYPE CHAR(3) NOT NULL,
            value BYTEA NOT NULL,
            enabled BOOLEAN NOT NULL,
            created TIMESTAMP NOT NULL,
            updated TIMESTAMP NOT NULL,
            CONSTRAINT pk_credential PRIMARY KEY(id),
            CONSTRAINT uq_credential_credential_id UNIQUE(
                gateway_id,
                credential_id
            ),
            CONSTRAINT uq_credential_name UNIQUE(
                gateway_id,
                name
            ),
            CONSTRAINT chk_credential_type CHECK(
                TYPE ~ '^PSK|RSA$'
            ),
            CONSTRAINT chk_credential_name CHECK(
                name ~ '^[a-zA-Z0-9\-]{3,32}$'
            ),
            CONSTRAINT fk_credential_gateway FOREIGN KEY(gateway_id) REFERENCES gateway(id) ON
            DELETE
                CASCADE
        );

CREATE
    TABLE
        configuration_definition(
            id SERIAL NOT NULL,
            tenant_id INTEGER NOT NULL,
            KEY VARCHAR(48) NOT NULL,
            TYPE VARCHAR(7) NOT NULL,
            description VARCHAR(200) NULL,
            minimum INTEGER NULL,
            maximum INTEGER NULL,
            pattern VARCHAR(1000) NULL,
            CONSTRAINT pk_configuration_definition PRIMARY KEY(id),
            CONSTRAINT uq_configuration_definition UNIQUE(
                tenant_id,
                KEY
            ),
            CONSTRAINT chk_configuration_definition_key CHECK(
                KEY ~ '^[a-zA-Z0-9\-\.\_]{3,48}$'
            ),
            CONSTRAINT chk_configuration_definition_type CHECK(
                TYPE ~ '^INTEGER|BOOLEAN|STRING|URL$'
            ),
            CONSTRAINT chk_configuration_definition_minimum CHECK(
                minimum IS NULL
                OR TYPE = 'STRING'
                OR TYPE = 'INTEGER'
            ),
            CONSTRAINT chk_configuration_definition_maximum CHECK(
                maximum IS NULL
                OR TYPE = 'STRING'
                OR TYPE = 'INTEGER'
            ),
            CONSTRAINT chk_configuration_definition_pattern CHECK(
                pattern IS NULL
                OR TYPE = 'STRING'
            ),
            CONSTRAINT fk_configuration_definition FOREIGN KEY(tenant_id) REFERENCES tenant(id) ON
            DELETE
                CASCADE
        );

CREATE
    TABLE
        tenant_configuration(
            id SERIAL NOT NULL,
            definition_id INTEGER NOT NULL,
            value VARCHAR(1000) NOT NULL,
            CONSTRAINT pk_tenant_configuration PRIMARY KEY(id),
            CONSTRAINT uq_tenant_configuration UNIQUE(definition_id),
            CONSTRAINT fk_tenant_configuration_definition FOREIGN KEY(definition_id) REFERENCES configuration_definition(id) ON
            DELETE
                CASCADE
        );

CREATE
    TABLE
        group_configuration(
            id SERIAL NOT NULL,
            group_id INTEGER NOT NULL,
            definition_id INTEGER NOT NULL,
            value VARCHAR(1000) NOT NULL,
            CONSTRAINT pk_group_configuration PRIMARY KEY(id),
            CONSTRAINT uq_group_configuration UNIQUE(
                group_id,
                definition_id
            ),
            CONSTRAINT fk_group_configuration_group FOREIGN KEY(group_id) REFERENCES "group"(id) ON
            DELETE
                CASCADE,
                CONSTRAINT fk_group_configuration_definition FOREIGN KEY(definition_id) REFERENCES configuration_definition(id) ON
                DELETE
                    CASCADE
        );

CREATE
    TABLE
        gateway_configuration(
            id SERIAL NOT NULL,
            gateway_id INTEGER NOT NULL,
            definition_id INTEGER NOT NULL,
            value VARCHAR(1000) NOT NULL,
            CONSTRAINT pk_gateway_configuration PRIMARY KEY(id),
            CONSTRAINT uq_gateway_configuration UNIQUE(
                gateway_id,
                definition_id
            ),
            CONSTRAINT fk_gateway_configuration_gateway FOREIGN KEY(gateway_id) REFERENCES gateway(id) ON
            DELETE
                CASCADE,
                CONSTRAINT fk_gateway_configuration_definition FOREIGN KEY(definition_id) REFERENCES configuration_definition(id) ON
                DELETE
                    CASCADE
        );

CREATE TABLE thing_type
(
    id                   SERIAL       NOT NULL,
    thing_type_id        VARCHAR(100) NOT NULL,
    thing_type_reference VARCHAR(100) NOT NULL,
    name                 VARCHAR(100) NOT NULL,
    json_schema          JSON         NOT NULL,
    ui_layout            JSON         NOT NULL,
    created              TIMESTAMP    NOT NULL,
    updated              TIMESTAMP    NOT NULL,
    CONSTRAINT pk_thing_type PRIMARY KEY (id),
    CONSTRAINT uq_thing_type_thing_type_id UNIQUE (thing_type_id)
);

CREATE TABLE thing
(
    id            SERIAL       NOT NULL,
    thing_id      VARCHAR(100) NOT NULL,
    tenant_id     VARCHAR(100) NOT NULL,
    gateway_id    VARCHAR(100) NULL,
    urn           VARCHAR(255) NULL,
    name          VARCHAR(100) NOT NULL,
    config        JSON         NOT NULL,
    thing_type_id INTEGER      NOT NULL,
    created       TIMESTAMP    NOT NULL,
    updated       TIMESTAMP    NOT NULL,
    CONSTRAINT pk_thing PRIMARY KEY (id),
    CONSTRAINT uq_thing_thing_id UNIQUE (thing_id),
    CONSTRAINT fk_thing_thing_type_id FOREIGN KEY (thing_type_id) REFERENCES thing_type (id)
);

CREATE TABLE thing_type
(
    id            SERIAL       NOT NULL,
    thing_type_id UUID         NOT NULL,
    name          VARCHAR(100) NOT NULL,
    properties    JSON         NOT NULL,
    created       TIMESTAMP    NOT NULL,
    updated       TIMESTAMP    NOT NULL,
    CONSTRAINT pk_thing_type PRIMARY KEY (id),
    CONSTRAINT uq_thing_type_thing_type_id UNIQUE (thing_type_id)
);
CREATE TABLE thing_type_channel
(
    id                    SERIAL       NOT NULL,
    thing_type_id         INTEGER      NOT NULL,
    thing_type_channel_id UUID         NOT NULL,
    key                   VARCHAR(100) NOT NULL,
    name                  VARCHAR(100) NOT NULL,
    description           VARCHAR(100) NULL,
    properties            JSON         NOT NULL,
    created               TIMESTAMP    NOT NULL,
    updated               TIMESTAMP    NOT NULL,
    CONSTRAINT pk_thing_type_channel PRIMARY KEY (id),
    CONSTRAINT uq_thing_type_channel_thing_type_channel_id UNIQUE (thing_type_channel_id),
    CONSTRAINT fk_thing_type_thing_type_id FOREIGN KEY (thing_type_id) REFERENCES thing_type (id) ON DELETE CASCADE
);
CREATE TABLE thing
(
    id            SERIAL       NOT NULL,
    thing_id      UUID         NOT NULL,
    tenant_id     VARCHAR(100) NOT NULL,
    gateway_id    VARCHAR(100) NULL,
    name          VARCHAR(100) NOT NULL,
    properties    JSON         NOT NULL,
    thing_type_id INTEGER      NOT NULL,
    created       TIMESTAMP    NOT NULL,
    updated       TIMESTAMP    NOT NULL,
    CONSTRAINT pk_thing PRIMARY KEY (id),
    CONSTRAINT uq_thing_thing_id UNIQUE (thing_id),
    CONSTRAINT fk_thing_thing_type_id FOREIGN KEY (thing_type_id) REFERENCES thing_type (id)
);
CREATE TABLE thing_channel
(
    id               SERIAL       NOT NULL,
    thing_channel_id UUID         NOT NULL,
    key              VARCHAR(100) NOT NULL,
    properties       JSON         NOT NULL,
    thing_id         INTEGER      NOT NULL,
    created          TIMESTAMP    NOT NULL,
    updated          TIMESTAMP    NOT NULL,
    CONSTRAINT pk_thing_channel PRIMARY KEY (id),
    CONSTRAINT uq_thing_channel_thing_channel_id UNIQUE (thing_channel_id),
    CONSTRAINT fk_thing_channel_thing_id FOREIGN KEY (thing_id) REFERENCES thing (id) ON DELETE CASCADE
);

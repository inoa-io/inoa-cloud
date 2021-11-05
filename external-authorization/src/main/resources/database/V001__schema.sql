CREATE TABLE tenant
(
    id        SERIAL       NOT NULL,
    tenant_id VARCHAR(30)  NOT NULL,
    enabled   BOOLEAN      NOT NULL,
    name      VARCHAR(100) NOT NULL,
    created   TIMESTAMP    NOT NULL,
    updated   TIMESTAMP    NOT NULL,
    CONSTRAINT pk_tenant PRIMARY KEY (id),
    CONSTRAINT uq_tenant_tenant_id UNIQUE (tenant_id),
    CONSTRAINT chk_tenant_tenant_id CHECK (tenant_id ~ '^[a-z0-9\-]{4,30}$')
);
CREATE TABLE user_
(
    id      SERIAL       NOT NULL,
    user_id UUID         NOT NULL,
    email   VARCHAR(255) NOT NULL,
    created TIMESTAMP    NOT NULL,
    updated TIMESTAMP    NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_user_id UNIQUE (user_id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE tenant_user
(
    tenant_id INTEGER   NOT NULL,
    user_id   INTEGER   NOT NULL,
    created   TIMESTAMP NOT NULL,
    CONSTRAINT pk_tenant_user PRIMARY KEY (tenant_id, user_id),
    CONSTRAINT fk_tenant_user_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id) ON DELETE CASCADE,
    CONSTRAINT fk_tenant_user_user FOREIGN KEY (user_id) REFERENCES user_ (id) ON DELETE CASCADE
);

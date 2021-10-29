CREATE TABLE tenant
(
    id        SERIAL      NOT NULL,
    tenant_id UUID        NOT NULL,
    name      VARCHAR(20) NOT NULL,
    created   TIMESTAMP   NOT NULL,
    updated   TIMESTAMP   NOT NULL,
    CONSTRAINT pk_tenant PRIMARY KEY (id),
    CONSTRAINT uq_tenant_tenant_id UNIQUE (tenant_id),
    CONSTRAINT uq_tenant_name UNIQUE (name),
    CONSTRAINT chk_tenant_name CHECK (name ~ '^[a-zA-Z0-9\-]{3,20}$')
);
CREATE TABLE "user"
(
    id      SERIAL    NOT NULL,
    user_id UUID      NOT NULL,
    email   UUID      NOT NULL,
    created TIMESTAMP NOT NULL,
    updated TIMESTAMP NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email),
    CONSTRAINT uq_user_user_id UNIQUE (user_id)
);

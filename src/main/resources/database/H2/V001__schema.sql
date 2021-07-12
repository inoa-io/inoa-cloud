CREATE TABLE tenant (
	id INTEGER auto_increment NOT NULL,
	tenant_id CHAR(36) NOT NULL,
	name VARCHAR(20) NOT NULL,
	enabled BOOLEAN NOT NULL,
	created TIMESTAMP NOT NULL,
	updated TIMESTAMP NOT NULL,
	CONSTRAINT pk_tenant PRIMARY KEY (id),
	CONSTRAINT uq_tenant_tenant_id UNIQUE (tenant_id),
	CONSTRAINT uq_tenant_name UNIQUE (name),
	CONSTRAINT chk_tenant_tenant_id CHECK (tenant_id ~ '^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'),
	CONSTRAINT chk_tenant_name CHECK (name ~ '^[a-zA-Z0-9\-]{3,20}$')
);
CREATE TABLE "group" (
	id INTEGER auto_increment NOT NULL,
	group_id CHAR(36) NOT NULL,
	tenant_id LONG NOT NULL,
	name VARCHAR(20) NOT NULL,
	created TIMESTAMP NOT NULL,
	updated TIMESTAMP NOT NULL,
	CONSTRAINT pk_group PRIMARY KEY (id),
	CONSTRAINT uq_group_group_id UNIQUE (tenant_id,group_id),
	CONSTRAINT uq_group_name UNIQUE (tenant_id,name),
	CONSTRAINT chk_group_group_id CHECK (group_id ~ '^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'),
	CONSTRAINT chk_group_name CHECK (name ~ '^[a-zA-Z0-9\-]{3,20}$'),
	CONSTRAINT fk_group_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE CASCADE
);
CREATE TABLE gateway (
	id INTEGER auto_increment NOT NULL,
	gateway_id CHAR(36) NOT NULL,
	tenant_id INTEGER NOT NULL,
	name VARCHAR(32) NOT NULL,
	enabled BOOLEAN NOT NULL,
	created TIMESTAMP NOT NULL,
	updated TIMESTAMP NOT NULL,
	CONSTRAINT pk_gateway PRIMARY KEY (id),
	CONSTRAINT uq_gateway_gateway_id UNIQUE (gateway_id),
	CONSTRAINT uq_gateway_name UNIQUE (tenant_id,name),
	CONSTRAINT chk_gateway_gateway_id CHECK (gateway_id ~ '^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'),
	CONSTRAINT chk_gateway_name CHECK (name ~ '^[a-zA-Z0-9\-]{3,32}$'),
	CONSTRAINT fk_gateway_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id)
);
CREATE TABLE gateway_property (
	gateway_id INTEGER NOT NULL,
	key VARCHAR(100) NOT NULL,
	value VARCHAR(1000) NOT NULL,
	created TIMESTAMP NOT NULL,
	updated TIMESTAMP NOT NULL,
	CONSTRAINT pk_gateway_property PRIMARY KEY (gateway_id,key),
	CONSTRAINT chk_gateway_property_key CHECK (key ~ '^[a-z0-9_\-\.]{2,100}$'),
	CONSTRAINT fk_gateway_property FOREIGN KEY (gateway_id) REFERENCES gateway(id) ON DELETE CASCADE
);
CREATE TABLE gateway_group (
	gateway_id INTEGER NOT NULL,
	group_id INTEGER NOT NULL,
	created TIMESTAMP NOT NULL,
	CONSTRAINT pk_gateway_group PRIMARY KEY (gateway_id,group_id),
	CONSTRAINT fk_gateway_group_gateway FOREIGN KEY (gateway_id) REFERENCES gateway(id) ON DELETE CASCADE,
	CONSTRAINT fk_gateway_group_group FOREIGN KEY (group_id) REFERENCES "group"(id) ON DELETE CASCADE
);
CREATE TABLE gateway_secret (
	id INTEGER auto_increment NOT NULL,
	gateway_id INTEGER NOT NULL,
	secret_id CHAR(36) NOT NULL,
	name VARCHAR(32) NOT NULL,
	enabled BOOLEAN NOT NULL,
	type VARCHAR(4) NOT NULL,
	hmac BINARY NULL,
	public_key BINARY NULL,
	private_key BINARY NULL,
	created TIMESTAMP NOT NULL,
	updated TIMESTAMP NOT NULL,
	CONSTRAINT pk_gateway_secret PRIMARY KEY (id),
	CONSTRAINT uq_gateway_secret_secret_id UNIQUE (secret_id),
	CONSTRAINT uq_gateway_secret_name UNIQUE (gateway_id,name),
	CONSTRAINT chk_gateway_secret_secret_id CHECK (secret_id ~ '^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'),
	CONSTRAINT chk_gateway_secret_name CHECK (name ~ '^[a-zA-Z0-9\-]{3,32}$'),
	CONSTRAINT chk_gateway_secret_type CHECK (type ~ '^HMAC|RSA$'),
	CONSTRAINT chk_gateway_secret_hmac CHECK ((type = 'HMAC' AND hmac IS NOT NULL) OR (type <> 'HMAC' AND hmac IS NULL)),
	CONSTRAINT chk_gateway_secret_public_key CHECK ((type = 'RSA' AND public_key IS NOT NULL) OR (type <> 'RSA' AND public_key IS NULL)),
	CONSTRAINT chk_gateway_secret_private_key CHECK ((type = 'RSA' AND private_key IS NOT NULL) OR private_key IS NULL),
	CONSTRAINT fk_gateway_secret FOREIGN KEY (gateway_id) REFERENCES gateway(id) ON DELETE CASCADE
);


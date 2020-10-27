CREATE TABLE tenant (
	id SERIAL NOT NULL,
	uuid CHAR(36) NOT NULL,
	name VARCHAR(20) NOT NULL,
	created TIMESTAMP NOT NULL,
	updated TIMESTAMP NOT NULL,
	CONSTRAINT pk_tenant PRIMARY KEY (id),
	CONSTRAINT uq_tenant_uuid UNIQUE (uuid),
	CONSTRAINT uq_tenant_name UNIQUE (name),
	CONSTRAINT chk_tenant_uuid CHECK (uuid ~ '^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'),
	CONSTRAINT chk_tenant_name CHECK (name ~ '^[a-zA-Z0-9\-]{3,20}$')
);
CREATE TABLE gateway (
	id SERIAL NOT NULL,
	tenant_id INTEGER NOT NULL,
	uuid CHAR(36) NOT NULL,
	serial VARCHAR(64) NOT NULL,
	created TIMESTAMP NOT NULL,
	updated TIMESTAMP NOT NULL,
	CONSTRAINT pk_gateway PRIMARY KEY (id),
	CONSTRAINT uq_gateway_uuid UNIQUE (uuid),
	CONSTRAINT uq_gateway_serial UNIQUE (tenant_id,serial),
	CONSTRAINT chk_gateway_uuid CHECK (uuid ~ '^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'),
	CONSTRAINT chk_gateway_serial CHECK (serial ~ '^[a-zA-Z0-9\-]{3,64}$'),
	CONSTRAINT fk_gateway FOREIGN KEY (tenant_id) REFERENCES tenant(id)
);
CREATE TABLE gateway_property (
	id SERIAL NOT NULL,
	gateway_id INTEGER NOT NULL,
	key VARCHAR(100) NOT NULL,
	value VARCHAR(1000) NOT NULL,
	created TIMESTAMP NOT NULL,
	updated TIMESTAMP NOT NULL,
	CONSTRAINT pk_gateway_property PRIMARY KEY (id),
	CONSTRAINT uq_gateway_property UNIQUE (gateway_id,key),
	CONSTRAINT chk_gateway_property_key CHECK (key ~ '^[a-z0-9_\-\.]{3,100}$'),
	CONSTRAINT fk_gateway_property FOREIGN KEY (gateway_id) REFERENCES gateway(id)
);

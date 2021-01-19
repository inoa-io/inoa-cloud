CREATE TABLE tenant (
	id CHAR(36) NOT NULL,
	name VARCHAR(20) NOT NULL,
	enabled BOOLEAN NOT NULL,
	created TIMESTAMP(6) NOT NULL,
	updated TIMESTAMP(6) NOT NULL,
	CONSTRAINT pk_tenant PRIMARY KEY (id),
	CONSTRAINT uq_tenant_name UNIQUE (name)
);
CREATE TABLE `group` (
	id CHAR(36) NOT NULL,
	tenant_id CHAR(36) NOT NULL,
	name VARCHAR(20) NOT NULL,
	created TIMESTAMP(6) NOT NULL,
	updated TIMESTAMP(6) NOT NULL,
	CONSTRAINT pk_group PRIMARY KEY (id),
	CONSTRAINT uq_group_name UNIQUE (tenant_id,name),
	CONSTRAINT fk_group_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE CASCADE
);
CREATE TABLE gateway (
	id CHAR(36) NOT NULL,
	tenant_id CHAR(36) NOT NULL,
	name VARCHAR(32) NOT NULL,
	enabled BOOLEAN NOT NULL,
	created TIMESTAMP(6) NOT NULL,
	updated TIMESTAMP(6) NOT NULL,
	CONSTRAINT pk_gateway PRIMARY KEY (id),
	CONSTRAINT uq_gateway_name UNIQUE (tenant_id,name),
	CONSTRAINT fk_gateway_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id)
);
CREATE TABLE gateway_property (
	gateway_id CHAR(36) NOT NULL,
	`key` VARCHAR(100) NOT NULL,
	value VARCHAR(1000) NOT NULL,
	created TIMESTAMP(6) NOT NULL,
	updated TIMESTAMP(6) NOT NULL,
	CONSTRAINT pk_gateway_property PRIMARY KEY (gateway_id,`key`),
	CONSTRAINT fk_gateway_property FOREIGN KEY (gateway_id) REFERENCES gateway(id) ON DELETE CASCADE
);
CREATE TABLE gateway_group (
	gateway_id CHAR(36) NOT NULL,
	group_id CHAR(36) NOT NULL,
	created TIMESTAMP(6) NOT NULL,
	CONSTRAINT pk_gateway_group PRIMARY KEY (gateway_id,group_id),
	CONSTRAINT fk_gateway_group_gateway FOREIGN KEY (gateway_id) REFERENCES gateway(id) ON DELETE CASCADE,
	CONSTRAINT fk_gateway_group_group FOREIGN KEY (group_id) REFERENCES `group`(id) ON DELETE CASCADE
);

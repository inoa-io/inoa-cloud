CREATE TABLE issuer (
	id				SERIAL		NOT NULL,
	tenant_id		INTEGER		NOT NULL,
	name			VARCHAR(10)	NOT NULL,
	url				VARCHAR		NOT NULL,
	cache_duration	VARCHAR		NOT NULL,
	created			TIMESTAMP	NOT NULL,
	updated			TIMESTAMP	NOT NULL,
	CONSTRAINT pk_issuer PRIMARY KEY (id),
	CONSTRAINT uq_issuer_name UNIQUE (id,name),
	CONSTRAINT uq_issuer_url UNIQUE (id,url),
	CONSTRAINT fk_issuer_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id) ON DELETE CASCADE,
	CONSTRAINT chk_issuer_name CHECK (name ~ '^[a-z0-9\-]*$')
);

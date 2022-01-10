CREATE TABLE tenant (
	id			SERIAL			NOT NULL,
	tenant_id	VARCHAR(30)		NOT NULL,
	enabled		BOOLEAN			NOT NULL,
	name		VARCHAR(100)	NOT NULL,
	created		TIMESTAMP		NOT NULL,
	updated		TIMESTAMP		NOT NULL,
	deleted		TIMESTAMP		NULL,
	CONSTRAINT pk_tenant PRIMARY KEY (id),
	CONSTRAINT uq_tenant_tenant_id UNIQUE (tenant_id),
	CONSTRAINT chk_tenant_tenant_id CHECK (tenant_id ~ '^[a-z0-9\-]{4,30}$')
);

CREATE TABLE "user" (
	id			SERIAL			NOT NULL,
	user_id		UUID			NOT NULL,
	email		VARCHAR(254)	NOT NULL,
	created		TIMESTAMP		NOT NULL,
	CONSTRAINT pk_user PRIMARY KEY (id),
	CONSTRAINT uq_user_user_id UNIQUE (user_id),
	CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE assignment (
	id			SERIAL			NOT NULL,
	tenant_id	INTEGER			NOT NULL,
	user_id		INTEGER			NOT NULL,
	role		VARCHAR(6)		NOT NULL,
	created		TIMESTAMP		NOT NULL,
	updated		TIMESTAMP		NOT NULL,
	CONSTRAINT pk_assignment PRIMARY KEY (id),
	CONSTRAINT uq_assignment UNIQUE (tenant_id,user_id),
	CONSTRAINT fk_assignment_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id) ON DELETE CASCADE,
	CONSTRAINT fk_assignment_user FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE,
	CONSTRAINT chk_assignment_role CHECK (role IN ('ADMIN','EDITOR','VIEWER'))
);

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

CREATE TABLE issuer_service (
	issuer_id	INTEGER			NOT NULL,
	name		VARCHAR(20)		NOT NULL,
	CONSTRAINT pk_issuer_service PRIMARY KEY (issuer_id,name),
	CONSTRAINT fk_issuer_service FOREIGN KEY (issuer_id) REFERENCES issuer (id) ON DELETE CASCADE
);

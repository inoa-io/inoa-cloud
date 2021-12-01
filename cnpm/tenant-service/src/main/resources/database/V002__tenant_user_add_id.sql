ALTER TABLE tenant_user DROP CONSTRAINT pk_tenant_user;
ALTER TABLE tenant_user ADD COLUMN id SERIAL CONSTRAINT pk_tenant_user PRIMARY KEY;
ALTER TABLE tenant_user ADD UNIQUE (tenant_id,user_id);

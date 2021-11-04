ALTER TABLE tenant ALTER COLUMN tenant_id TYPE VARCHAR(36);
UPDATE tenant SET tenant_id = 'inoa' WHERE tenant_id = 'e0ac5bb5-f27b-4972-89d0-8e458fc9d6da';
UPDATE tenant SET tenant_id = 'wumm' WHERE tenant_id = '1d1153ab-e1fe-4712-86cc-0b1db51f5c73';
UPDATE tenant SET tenant_id = 'testing' WHERE tenant_id = '2381b39a-e721-4456-8f9f-8d2c18cec993';
ALTER TABLE tenant ALTER COLUMN tenant_id TYPE VARCHAR(30);
ALTER TABLE tenant ADD CONSTRAINT chk_tenant_id CHECK (tenant_id ~ '^[a-z0-9\-]{4,30}$');

ALTER TABLE tenant DROP CONSTRAINT chk_tenant_name;
ALTER TABLE tenant ALTER COLUMN name TYPE VARCHAR(100);
ALTER TABLE tenant ADD CONSTRAINT chk_tenant_name CHECK (length(name) >= 4);

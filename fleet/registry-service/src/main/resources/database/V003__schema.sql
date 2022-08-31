ALTER TABLE gateway ADD COLUMN connection_status_timestamp TIMESTAMP;
ALTER TABLE gateway ADD COLUMN connection_status_connected BOOLEAN NOT NULL default false;

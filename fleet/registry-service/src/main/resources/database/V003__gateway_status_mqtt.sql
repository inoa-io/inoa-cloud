ALTER TABLE gateway ADD COLUMN mqtt_timestamp TIMESTAMP;
ALTER TABLE gateway ADD COLUMN mqtt_connected BOOLEAN NULL;
UPDATE gateway SET mqtt_connected=false;
ALTER TABLE gateway ALTER COLUMN mqtt_connected SET NOT NULL;

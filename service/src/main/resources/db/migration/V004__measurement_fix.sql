DELETE FROM thing_type;

ALTER TABLE thing_type
DROP COLUMN ui_layout;

ALTER TABLE thing_type
ADD COLUMN category VARCHAR(100) NOT NULL;

INSERT INTO
	thing_type (
		id,
		thing_type_id,
		name,
		category,
		json_schema,
		created,
		updated
	)
VALUES
	(
		1,
		'dvh4013',
		'DZG DVH4013',
		'energy_meter',
		'{"title":"DZG - DVH 4013 Power Meter","description":"a bi-directional power meter","type":"object","required":["name","serial","modbus_interface"],"properties":{"name":{"type":"string","default":"My new DVH4013","title":"Name your thing..."},"serial":{"type":"string","title":"The serial number of the energy meter" },"modbus_interface":{"title":"RS485 interface where the power meter is connected.","type":"number","default":0,"widget":{"formlyConfig":{"type":"enum","props":{"options":[{"value":0,"label":"RS485 - Port 1"},{"value": 1,"label":"RS485 - Port 2"}]}}}}}}',
		CURRENT_TIMESTAMP,
		CURRENT_TIMESTAMP
	);

INSERT INTO
	thing_type (
		id,
		thing_type_id,
		name,
		category,
		json_schema,
		created,
		updated
	)
VALUES
	(
		2,
		'mdvh4006',
		'DZG MDVH4006',
		'energy_meter',
		'{"title":"MDVH4006","description":"a bi-directional power meter","type":"object","required":["name","serial","modbus_interface"],"properties":{"name":{"type":"string","default":"My new MDVH4006","title":"Name your thing..."},"serial":{"type":"string","title":"The serial number of the energy meter"},"modbus_interface":{"title":"RS485 interface where the power meter is connected.","type":"number","default": 0,"widget":{"formlyConfig":{"type":"enum","props":{"options":[{"value":0,"label":"RS485 - Port 1"},{"value":1,"label":"RS485 - Port 2"}]}}}}}}',
		CURRENT_TIMESTAMP,
		CURRENT_TIMESTAMP
	);

INSERT INTO
	thing_type (
		id,
		thing_type_id,
		name,
		category,
		json_schema,
		created,
		updated
	)
VALUES
	(
		3,
		'dvmodbusir',
		'DvModbusIR',
		'energy_meter',
		'{"title":"DV Modbus IR","description":"a bi-directional power meter connectedvia IR interface.","type":"object","required":["name","serial","modbus_interface"],"properties":{"name":{"type":"string","default":"My new MDVH4006","title":"Name your thing..."},"serial":{"type":"string","title":"The serial number of the energy meter"},"modbus_interface":{"title":"RS485 interface where the power meter is connected.","type":"number","default": 0,"widget":{"formlyConfig":{"type":"enum","props":{"options":[{"value":0,"label":"RS485 - Port 1"},{"value":1,"label":"RS485 - Port 2"}]}}}}}}',
		CURRENT_TIMESTAMP,
		CURRENT_TIMESTAMP
	);

INSERT INTO
	thing_type (
		id,
		thing_type_id,
		name,
		category,
		json_schema,
		created,
		updated
	)
VALUES
	(
		4,
		's0',
		'S0 Impulse Counter',
		'energy_meter',
		'{"title":"S0 Impulse Counter","description":"A simple counter connected via S0 interface.","type":"object","required":["name"],"properties":{"name":{"type":"string","default":"My new S0 counter","title":"Name your thing..."},"port":{"type":"string","title":"Port"}}}',
		CURRENT_TIMESTAMP,
		CURRENT_TIMESTAMP
	);

INSERT INTO
	thing_type (
		id,
		thing_type_id,
		name,
		category,
		json_schema,
		created,
		updated
	)
VALUES
	(
		5,
		'shplg-s',
		'Shelly Plug S',
		'smart_plug',
		'{"title":"Shelly Plug S","description":"a smart plug made by Shelly.","type":"object","required":["name","host"],"properties":{"name":{"type":"string","default":"My new Shelly Plug S","title":"Name your thing..."},"host":{"type":"string","title":"Host"}}}',
		CURRENT_TIMESTAMP,
		CURRENT_TIMESTAMP
	);

INSERT INTO
	thing_type (
		id,
		thing_type_id,
		name,
		category,
		json_schema,
		created,
		updated
	)
VALUES
	(
		6,
		'shplg-pm2',
		'Shelly Plus PM2',
		'smart_plug',
		'{"title":"Shelly Plus PM2","description":"a smart plug made by Shelly.","type":"object","required":["name","host"],"properties":{"name":{"type":"string","default":"My new Shelly Plus PM2","title":"Name your thing..."},"host":{"type":"string","title":"Host"}}}',
		CURRENT_TIMESTAMP,
		CURRENT_TIMESTAMP
	);
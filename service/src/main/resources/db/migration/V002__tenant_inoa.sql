INSERT INTO
	tenant (
		tenant_id,
		name,
		enabled,
		gateway_id_pattern,
		created,
		updated
	)
VALUES
	(
		'inoa',
		'INOA',
		true,
		'IS[A-Z]{2}[0-9]{2}-[A-F0-9]{12}',
		CURRENT_TIMESTAMP,
		CURRENT_TIMESTAMP
	);

INSERT INTO
	configuration_definition (tenant_id, key, type, description, pattern)
VALUES
	(
		(
			SELECT
				id
			FROM
				tenant
			WHERE
				tenant_id = 'inoa'
		),
		'ntp.host',
		'STRING',
		'Host of ntp server to use.',
		'[a-z0-9\.]{8,20}'
	);

INSERT INTO
	configuration_definition (tenant_id, key, type, description, pattern)
VALUES
	(
		(
			SELECT
				id
			FROM
				tenant
			WHERE
				tenant_id = 'inoa'
		),
		'mqtt.url',
		'URL',
		'URL of mqtt server.',
		null
	);

INSERT INTO
	configuration_definition (tenant_id, key, type, description, pattern)
VALUES
	(
		(
			SELECT
				id
			FROM
				tenant
			WHERE
				tenant_id = 'inoa'
		),
		'mqtt.insecure',
		'BOOLEAN',
		'Ignore invalid tls certificate.',
		null
	);

INSERT INTO
	tenant_configuration (definition_id, value)
VALUES
	(
		(
			SELECT
				id
			FROM
				configuration_definition
			WHERE
				tenant_id = (
					SELECT
						id
					FROM
						tenant
					WHERE
						tenant_id = 'inoa'
				)
				AND KEY = 'ntp.host'
		),
		'pool.ntp.org'
	);

INSERT INTO
	tenant_configuration (definition_id, value)
VALUES
	(
		(
			SELECT
				id
			FROM
				configuration_definition
			WHERE
				tenant_id = (
					SELECT
						id
					FROM
						tenant
					WHERE
						tenant_id = 'inoa'
				)
				AND KEY = 'mqtt.url'
		),
		'ssl://127.0.0.1:8883'
	);

INSERT INTO
	tenant_configuration (definition_id, value)
VALUES
	(
		(
			SELECT
				id
			FROM
				configuration_definition
			WHERE
				tenant_id = (
					SELECT
						id
					FROM
						tenant
					WHERE
						tenant_id = 'inoa'
				)
				AND KEY = 'mqtt.insecure'
		),
		'true'
	);
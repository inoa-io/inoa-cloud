INSERT
    INTO
        configuration_definition(
            KEY,
            TYPE,
            description,
            pattern
        )
    VALUES(
        'ntp.host',
        'STRING',
        'Host of ntp server to use.',
        '[a-z0-9\.]{8,20}'
    );

INSERT
    INTO
        configuration_definition(
            KEY,
            TYPE,
            description,
            pattern
        )
    VALUES(
        'mqtt.url',
        'URL',
        'URL of mqtt server.',
        NULL
    );

INSERT
    INTO
        configuration_definition(
            KEY,
            TYPE,
            description,
            pattern
        )
    VALUES(
        'mqtt.insecure',
        'BOOLEAN',
        'Ignore invalid tls certificate.',
        NULL
    );
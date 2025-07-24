-- Add converter factor to MDVH4006
INSERT
    INTO
        thing_configuration
    VALUES(
        DEFAULT,
        'Converter Factor',
        'The factor of the used transducer. E.g. 250A/5A = 50',
        2,
        'NUMBER',
        NULL
    );

-- Add DvModbusIR type
INSERT
    INTO
        thing_type
    VALUES(
        DEFAULT,
        'dzg-dvze-via-dvmodbusir',
        'DZG DVZE via DvModbusIR',
        'A DZG DVZE meter combined with the optical interface device DvModbusIR',
        'ELECTRIC_METER',
        NULL,
        'MODBUS_RS458'
    );
INSERT
    INTO
        thing_configuration
    VALUES(
        DEFAULT,
        'DvModbusIR Serial',
        'Serial number of the optical interface device',
        3,
        'NUMBER',
        NULL
    );
INSERT
    INTO
        thing_configuration
    VALUES(
        DEFAULT,
        'DZG DVZE Serial',
        'Serial number of the DZG DVZE power meter',
        3,
        'STRING',
        '1DZG[0-9]{10}'
    );
INSERT
    INTO
        thing_configuration
    VALUES(
        DEFAULT,
        'Modbus Interface',
        'Modbus interface the meter is connected to',
        3,
        'NUMBER',
        NULL
    );
INSERT
    INTO
        thing_type_measurand_type(
            thing_type_id,
            measurand_type_id
        )
    VALUES (3, 4), (3, 5), (3, 6), (3, 7);

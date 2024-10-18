CREATE
    TYPE protocol_enum AS ENUM(
        's0',
        'http',
        'modbus-rs485',
        'modbus-tcp',
        'rms',
        'adc'
    );

ALTER TABLE
    thing_type ADD COLUMN protocol protocol_enum NOT NULL DEFAULT 'http';

-- s0 update
UPDATE
    thing_type
SET
    protocol = 's0'
WHERE
    thing_type_id = 's0';

-- dvmodbusir update
UPDATE
    thing_type
SET
    protocol = 'modbus-rs485'
WHERE
    thing_type_id = 'dvmodbusir';

-- dvh4013 update
UPDATE
    thing_type
SET
    protocol = 'modbus-rs485'
WHERE
    thing_type_id = 'dvh4013';

-- mdvh4006 update
UPDATE
    thing_type
SET
    protocol = 'modbus-rs485'
WHERE
    thing_type_id = 'mdvh4006';
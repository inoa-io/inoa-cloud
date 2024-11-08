INSERT
    INTO
        thing_type(
            id,
            thing_type_id,
            protocol,
            name,
            category,
            json_schema,
            created,
            updated
        )
    VALUES(
        1,
        'dvh4013',
        'modbus-rs485',
        'DZG DVH4013',
        'energy_meter',
        '{
                "title": "DZG - DVH 4013 Power Meter",
                "description": "a bi-directional power meter",
                "type": "object",
                "required": [
                    "name",
                    "serial",
                    "modbus_interface"
                ],
                "properties": {
                    "name": {
                        "type": "string",
                        "default": "My new DVH4013",
                        "title": "Name your thing..."
                    },
                    "serial": {
                        "type": "string",
                        "title": "The serial number of the energy meter"
                    },
                    "modbus_interface": {
                        "title": "RS485 interface where the power meter is connected.",
                        "type": "number",
                        "default": 0,
                        "widget": {
                            "formlyConfig": {
                                "type": "enum",
                                "props": {
                                    "options": [
                                        {
                                            "value": 0,
                                            "label": "RS485 - Port 1"
                                        },
                                        {
                                            "value": 1,
                                            "label": "RS485 - Port 2"
                                        }
                                    ]
                                }
                            }
                        }
                    }
                }
            }',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT
    INTO
        thing_type(
            id,
            thing_type_id,
            protocol,
            name,
            category,
            json_schema,
            created,
            updated
        )
    VALUES(
        2,
        'mdvh4006',
        'modbus-rs485',
        'DZG MDVH4006',
        'energy_meter',
        '{
                "title": "MDVH4006",
                "description": "a bi-directional power meter",
                "type": "object",
                "required": [
                    "name",
                    "serial",
                    "modbus_interface"
                ],
                "properties": {
                    "name": {
                        "type": "string",
                        "default": "My new MDVH4006",
                        "title": "Name your thing..."
                    },
                    "serial": {
                        "type": "string",
                        "title": "The serial number of the energy meter"
                    },
                    "modbus_interface": {
                        "title": "RS485 interface where the power meter is connected.",
                        "type": "number",
                        "default": 0,
                        "widget": {
                            "formlyConfig": {
                                "type": "enum",
                                "props": {
                                    "options": [
                                        {
                                            "value": 0,
                                            "label": "RS485 - Port 1"
                                        },
                                        {
                                            "value": 1,
                                            "label": "RS485 - Port 2"
                                        }
                                    ]
                                }
                            }
                        }
                    }
                }
            }',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT
    INTO
        thing_type(
            id,
            thing_type_id,
            protocol,
            name,
            category,
            json_schema,
            created,
            updated
        )
    VALUES(
        3,
        'dvmodbusir',
        'modbus-rs485',
        'DvModbusIR',
        'energy_meter',
        '{
                "title": "DV Modbus IR",
                "description": "a bi-directional power meter connectedvia IR interface.",
                "type": "object",
                "required": [
                    "name",
                    "serial",
                    "modbus_interface"
                ],
                "properties": {
                    "name": {
                        "type": "string",
                        "default": "My new MDVH4006",
                        "title": "Name your thing..."
                    },
                    "serial": {
                        "type": "string",
                        "title": "The serial number of the energy meter"
                    },
                    "modbus_interface": {
                        "title": "RS485 interface where the power meter is connected.",
                        "type": "number",
                        "default": 0,
                        "widget": {
                            "formlyConfig": {
                                "type": "enum",
                                "props": {
                                    "options": [
                                        {
                                            "value": 0,
                                            "label": "RS485 - Port 1"
                                        },
                                        {
                                            "value": 1,
                                            "label": "RS485 - Port 2"
                                        }
                                    ]
                                }
                            }
                        }
                    }
                }
            }',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT
    INTO
        thing_type(
            id,
            thing_type_id,
            protocol,
            name,
            category,
            json_schema,
            created,
            updated
        )
    VALUES(
        4,
        's0',
        's0',
        'S0 Impulse Counter',
        'energy_meter',
        '{
                "title": "S0 Impulse Counter",
                "description": "A simple counter connected via S0 interface.",
                "type": "object",
                "required": [
                    "name",
                    "port"
                ],
                "properties":
                {
                    "name":
                    {
                        "type": "string",
                        "default": "My new S0 counter",
                        "title": "Name your thing..."
                    },
                    "port":
                    {
                        "type": "number",
                        "title": "Port",
                        "enum": [
                            0,
                            1
                        ]
                    }
                }
            }',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT
    INTO
        thing_type(
            id,
            thing_type_id,
            protocol,
            name,
            category,
            json_schema,
            created,
            updated
        )
    VALUES(
        5,
        'shellyplug-s',
        'http',
        'Shelly Plug S',
        'smart_plug',
        '{
                "title": "Shelly Plug S",
                "description": "a smart plug made by Shelly.",
                "type": "object",
                "required": [
                    "name",
                    "host"
                ],
                "properties": {
                    "name": {
                        "type": "string",
                        "default": "My new Shelly Plug S",
                        "title": "Name your thing..."
                    },
                    "host": {
                        "type": "string",
                        "title": "Host"
                    }
                }
            }',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT
    INTO
        thing_type(
            id,
            thing_type_id,
            protocol,
            name,
            category,
            json_schema,
            created,
            updated
        )
    VALUES(
        6,
        'shplg-pm2',
        'http',
        'Shelly Plus PM2',
        'smart_plug',
        '{
                "title": "Shelly Plus PM2",
                "description": "a smart plug made by Shelly.",
                "type": "object",
                "required": [
                    "name",
                    "host"
                ],
                "properties": {
                    "name": {
                        "type": "string",
                        "default": "My new Shelly Plus PM2",
                        "title": "Name your thing..."
                    },
                    "host": {
                        "type": "string",
                        "title": "Host"
                    }
                }
            }',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT
    INTO
        thing_type(
            id,
            thing_type_id,
            protocol,
            name,
            category,
            json_schema,
            created,
            updated
        )
    VALUES(
        7,
        'shellyplusplugs',
        'http',
        'Shelly Plus Plug S',
        'smart_plug',
        '{
                "title": "Shelly Plus Plug S",
                "description": "a smart plug made by Shelly.",
                "type": "object",
                "required": [
                    "name",
                    "host"
                ],
                "properties": {
                    "name": {
                        "type": "string",
                        "default": "My new Shelly Plus Plug S",
                        "title": "Name your thing..."
                    },
                    "host": {
                        "type": "string",
                        "title": "Host"
                    }
                }
            }',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT
    INTO
        thing_type(
            id,
            thing_type_id,
            protocol,
            name,
            category,
            json_schema,
            created,
            updated
        )
    VALUES(
        8,
        'abb-b23',
        'modbus-rs485',
        'ABB B23',
        'energy_meter',
        '{
                "title":"ABB B23",
                "description":"An advanced compact DIN-rail meter.",
                "type":"object","required":["name", "modbus_address", "modbus_interface"],
                "properties":
                {
                    "name":
                    {
                        "type":"string",
                        "default":"My new ABB B23",
                        "title":"Name your thing..."
                    },
                    "modbus_adress":
                    {
                        "type":"number",
                        "title":"Modbus address"
                    },
                    "modbus_interface":
                    {
                        "title":"RS485 interface where the power meter is connected.",
                        "type":"number",
                        "default": 0,"widget":
                        {
                            "formlyConfig":
                            {
                                "type":"enum",
                                "props":
                                {
                                    "options":
                                    [
                                        {"value":0,"label":"RS485 - Port 1"},
                                        {"value":1,"label":"RS485 - Port 2"}
                                    ]
                                }
                            }
                        }
                    }
                }
            }',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT
    INTO
        thing_type(
            id,
            thing_type_id,
            protocol,
            name,
            category,
            json_schema,
            created,
            updated
        )
    VALUES(
        9,
        'sct-013_30',
        'rms',
        'Split Core Transformer SCT-013 (30A/1V)',
        'current_sensor',
        '{
            "title": "Split Core Transformer SCT-013 (30A/1V)",
            "description": "Direct current split core transformer measuring via analog input",
            "type": "object",
            "required": [
                "name",
                "port"
            ],
            "properties": {
                "name": {
                    "type": "string",
                    "default": "Split Core Transformer",
                    "title": "Name your thing..."
                },
                "port": {
                    "type": "number",
                    "title": "Port",
                    "enum": [
                        1,
                        2,
                        3
                    ]
                }
            }
        }',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );
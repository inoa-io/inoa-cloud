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
        8,
        'abb-b23',
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
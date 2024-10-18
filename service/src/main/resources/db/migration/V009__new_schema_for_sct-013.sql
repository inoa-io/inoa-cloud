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
        'sct-013',
        'rms',
        'Split Core Transformer',
        'current_sensor',
        '{
            "title": "Split Core Transformer (SCT-013)",
            "description": "Direct current split core transformer measuring via analog input",
            "type": "object",
            "required": ["name", "ct-ratio", "port"],
            "properties":
            {
                "name":
                {
                    "type": "string",
                    "default": "Split Core Transformer",
                    "title": "Name your thing..."
                },
                "ct-ratio":
                {
                    "type": "string",
                    "title": "Conversion factor",
                    "enum": [
                        "5A/1V",
                        "10A/1V",
                        "15A/1V",
                        "20A/1V",
                        "25A/1V",
                        "30A/1V",
                        "50A/1V",
                        "60A/1V",
                        "100A/1V"
                    ]
                },
                "port":
                {
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
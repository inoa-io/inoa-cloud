-- s0 update schema port enum
UPDATE
    thing_type
SET
    json_schema = '{
			"title": "S0 Impulse Counter",
			"description": "A simple counter connected via S0 interface.",
			"type": "object",
			"required": ["name"],
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
		}'
WHERE
    thing_type_id = 's0';
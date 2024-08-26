
UPDATE thing_type
SET thing_type_id = 'shellyplug-s'
WHERE id = 5;

INSERT INTO thing_type (id, thing_type_id, name, category, json_schema, created, updated) VALUES (7, 'shellyplusplugs', 'Shelly Plus Plug S', 'smart_plug', '{"title":"Shelly Plus Plug S","description":"a smart plug made by Shelly.","type":"object","required":["name","host"],"properties":{"name":{"type":"string","default":"My new Shelly Plus Plug S","title":"Name your thing..."},"host":{"type":"string","title":"Host"}}}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
UPDATE
    thing_configuration
SET
    TYPE = 'NUMBER',
    validation_regex = '[0-9]{8}'
WHERE
    name = 'Serial';

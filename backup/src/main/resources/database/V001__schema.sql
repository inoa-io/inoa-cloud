CREATE TABLE message (
	id				BIGSERIAL		NOT NULL,
	topic			VARCHAR(100)	NOT NULL,
	key_			VARCHAR(100)	NOT NULL,
	value			TEXT  			NOT NULL,
	timestamp		TIMESTAMP		NOT NULL,
	timestamp_type	VARCHAR(20)		NOT NULL,
	headers			TEXT			NOT NULL,
	CONSTRAINT pk_message PRIMARY KEY (id)
);

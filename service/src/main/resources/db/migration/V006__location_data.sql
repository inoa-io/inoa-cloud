ALTER TABLE
    gateway ADD COLUMN location_house_number VARCHAR(10) NULL,
    ADD COLUMN location_road VARCHAR(100) NULL,
    ADD COLUMN location_neighbourhood VARCHAR(100) NULL,
    ADD COLUMN location_suburb VARCHAR(100) NULL,
    ADD COLUMN location_city_district VARCHAR(100) NULL,
    ADD COLUMN location_city VARCHAR(100) NULL,
    ADD COLUMN location_state VARCHAR(100) NULL,
    ADD COLUMN location_postcode VARCHAR(20) NULL,
    ADD COLUMN location_country VARCHAR(100) NULL,
    ADD COLUMN location_country_code CHAR(2) NULL,
    ADD COLUMN location_latitude DOUBLE PRECISION NULL,
    ADD COLUMN location_longitude DOUBLE PRECISION NULL,
    ADD CONSTRAINT gateway_location_country_code_check CHECK(
        location_country_code IS NULL
        OR LENGTH(location_country_code)= 2
    ),
    ADD CONSTRAINT gateway_location_latitude_check CHECK(
        location_latitude IS NULL
        OR location_latitude BETWEEN - 90 AND 90
    ),
    ADD CONSTRAINT gateway_location_longitude_check CHECK(
        location_longitude IS NULL
        OR location_longitude BETWEEN - 180 AND 180
    );
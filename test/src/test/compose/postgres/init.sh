#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL

    CREATE USER "tenant-service" WITH ENCRYPTED PASSWORD 'changeMe';
    CREATE DATABASE "tenant-service";
    GRANT ALL PRIVILEGES ON DATABASE "tenant-service" TO "tenant-service";

    CREATE USER "gateway-registry" WITH ENCRYPTED PASSWORD 'changeMe';
    CREATE DATABASE "gateway-registry";
    GRANT ALL PRIVILEGES ON DATABASE "gateway-registry" TO "gateway-registry";

EOSQL

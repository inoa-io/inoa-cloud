#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL

    CREATE USER "tenant-service" WITH ENCRYPTED PASSWORD 'changeMe';
    CREATE DATABASE "tenant-service";
    GRANT ALL PRIVILEGES ON DATABASE "tenant-service" TO "tenant-service";

    CREATE USER "registry" WITH ENCRYPTED PASSWORD 'changeMe';
    CREATE DATABASE "registry";
    GRANT ALL PRIVILEGES ON DATABASE "registry" TO "registry";

EOSQL

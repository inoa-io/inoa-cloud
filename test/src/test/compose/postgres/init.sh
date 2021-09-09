#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE "gateway-registry";
    CREATE DATABASE "hono-backup";
    GRANT ALL PRIVILEGES ON DATABASE "gateway-registry" TO postgres;
    GRANT ALL PRIVILEGES ON DATABASE "hono-backup" TO postgres;
EOSQL


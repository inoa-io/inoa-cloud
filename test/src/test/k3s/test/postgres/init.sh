#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL

    CREATE USER "keycloak" WITH ENCRYPTED PASSWORD 'changeMe';
    CREATE DATABASE "keycloak" OWNER "keycloak";

    CREATE USER "grafana" WITH ENCRYPTED PASSWORD 'changeMe';
    CREATE DATABASE "grafana" OWNER "grafana";

    CREATE USER "hawkbit" WITH ENCRYPTED PASSWORD 'changeMe';
    CREATE DATABASE "hawkbit" OWNER "hawkbit";

    CREATE USER "inoa" WITH ENCRYPTED PASSWORD 'changeMe';
    CREATE DATABASE "inoa" OWNER "inoa";

EOSQL

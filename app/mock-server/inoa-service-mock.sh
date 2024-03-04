#!/bin/bash

docker pull mockserver/mockserver

MOCKSERVER_CONFIG="$(pwd)/../../../target/mock-server"
mkdir -p "${MOCKSERVER_CONFIG}"

### Mockserver Configuration
cp -rf "$(pwd)/config/mockserver.properties" "${MOCKSERVER_CONFIG}"

### APIS
FLEET_OPENAPI_PATH="$(pwd)/../../../../inoa-fleet/src/main/resources/openapi/inoa-fleet-api.yaml"
MEASUREMENT_OPENAPI_PATH="$(pwd)/../../../../inoa-measurement/src/main/resources/openapi/inoa-measurement-api.yaml"

cp -rf "${FLEET_OPENAPI_PATH}" "${MOCKSERVER_CONFIG}"
### Not needed now.
# cp -rf ${HAWKBIT_OPENAPI_PATH} "${MOCKSERVER_CONFIG}"
cp -rf "${MEASUREMENT_OPENAPI_PATH}" "${MOCKSERVER_CONFIG}"

echo "Using Open APIs from directory: ${MOCKSERVER_CONFIG}"

docker run -d --rm --name=fleet-mock -v "${MOCKSERVER_CONFIG}:/config" -p 1080:1080  mockserver/mockserver -Dmockserver.propertyFile=/config/mockserver.properties

sleep 4


curl -v -X PUT "http://localhost:1080/mockserver/openapi" -d '{
    "specUrlOrPayload": "/config/inoa-fleet-api.yaml"
}'
#curl -v -X PUT "http://localhost:1080/mockserver/openapi" -d '{
#    "specUrlOrPayload": "/config/hawkbit-management-api.yaml"
#}'
curl -v -X PUT "http://localhost:1080/mockserver/openapi" -d '{
    "specUrlOrPayload": "/config/inoa-measurement-api.yaml"
}'


echo "APIs is available at: http://localhost:1080/api/v1"

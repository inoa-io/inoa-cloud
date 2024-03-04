#!/bin/bash

MOCKSERVER_PROPERTIES=".\src\test\mock-server\config\mockserver.properties"
TARGET_PATH=".\target"
# if I don't use an absolute path here, it won't work, because it adds strange characters to the path when auto converting
# add your own absolute path here to make it work, or figure out how to do it with a relative path and tell me how
MOCKSERVER_CONFIG="d:\_Work_\GrayC\projects\inoa-cloud\inoa-groundcontrol\target\mock-server"
# ----------------------- CHANGE THIS ^^^^^^^ OR IT WILL NOT WORK --------------------------------------------------
FLEET_OPENAPI_PATH=".\..\inoa-fleet\src\main\resources\openapi\inoa-fleet-api.yaml"
MEASUREMENT_OPENAPI_PATH=".\..\inoa-measurement\src\main\resources\openapi\inoa-measurement-api.yaml"

echo "Absolute path of target directory: $(realpath ${TARGET_PATH})"
echo "Absolute path of mockserver properties file: $(realpath ${MOCKSERVER_PROPERTIES})"
echo "Absolute path of mockserver config directory: $(realpath ${MOCKSERVER_CONFIG})"
echo "Absolute path of inoa-fleet-api.yaml: $(realpath ${FLEET_OPENAPI_PATH})"
echo "Absolute path of inoa-measurement-api.yaml: $(realpath ${MEASUREMENT_OPENAPI_PATH})"
echo

docker pull mockserver/mockserver

mkdir -p "${MOCKSERVER_CONFIG}"

### Mockserver Configuration
echo
echo "Copying config files to ${MOCKSERVER_CONFIG}"

cp -rf "${MOCKSERVER_PROPERTIES}" "${MOCKSERVER_CONFIG}"
cp -rf "${FLEET_OPENAPI_PATH}" "${MOCKSERVER_CONFIG}"
cp -rf "${MEASUREMENT_OPENAPI_PATH}" "${MOCKSERVER_CONFIG}"

echo
echo "Using Open APIs from directory: ${MOCKSERVER_CONFIG}"
echo

docker run -d --rm --name=fleet-mock -v ${MOCKSERVER_CONFIG}:/config -p 1080:1080 mockserver/mockserver -Dmockserver.propertyFile=/config/mockserver.properties

sleep 4

echo
echo "sending inoa-fleet-api.yaml to mockserver..."
echo
curl -v -X PUT "http://localhost:1080/mockserver/openapi" -d '{
    "specUrlOrPayload": "/config/inoa-fleet-api.yaml"
}'

echo
echo "sending inoa-measurement-api.yaml to mockserver..."
echo
curl -v -X PUT "http://localhost:1080/mockserver/openapi" -d '{
    "specUrlOrPayload": "/config/inoa-measurement-api.yaml"
}'

echo
echo "APIs is available at: http://localhost:1080/api/v1"
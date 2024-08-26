#!/bin/bash

### Not sure if needed but for convention...
source ./.env

### just intercept the requests for INOA http and mqtt endpoint.
INOA_HTTP="$(telepresence list | grep inoa-http)"
INOA_MQTT="$(telepresence list | grep inoa-mqtt)"

if [ -z "$INOA_HTTP" ]; then
  telepresence intercept inoa-http --workload=inoa --service=inoa --port 4300:http
fi

if [ -z "$INOA_MQTT" ]; then
  telepresence intercept inoa-mqtt --workload=inoa --service=inoa --port 1884:mqtt
fi

#!/bin/bash

ENV_FILE=.env
if [ ! -f "$ENV_FILE" ]; then
    echo "ERROR: Can't start INOA without local configuration."
    echo "$ENV_FILE does not exist. Please create it from .env.template and configure at least your local IP."
    exit 1;
fi
# shellcheck source=/dev/null
source $ENV_FILE

### just intercept the requests for INOA http and mqtt endpoint.
INOA_HTTP="$(telepresence list | grep inoa-http)"
INOA_MQTT="$(telepresence list | grep inoa-mqtt)"

if [ -z "$INOA_HTTP" ]; then
  telepresence intercept inoa-http --workload=inoa --service=inoa --port 4300:http
fi

if [ -z "$INOA_MQTT" ]; then
  telepresence intercept inoa-mqtt --workload=inoa --service=inoa --port 1884:mqtt
fi

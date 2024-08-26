#!/bin/bash

# shellcheck source=/dev/null
source ./.env

# TODO Verify connection status before quiting.
#TELEPRESENCE_CONNECTED=telepresence status | grep 'Not connected'

INOA_HTTP="$(telepresence list | grep inoa-http)"
INOA_MQTT="$(telepresence list | grep inoa-mqtt)"

if [ "$INOA_HTTP" ]; then
  telepresence leave inoa-http
fi

if [ "$INOA_MQTT" ]; then
  telepresence leave inoa-mqtt
fi

telepresence quit -s

mvn k3s:rm -Dk3s.skipRm=false

echo "INOA Cloud services stopped"


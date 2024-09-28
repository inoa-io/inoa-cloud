#!/bin/bash

ENV_FILE=.env
if [ ! -f "$ENV_FILE" ]; then
    echo "ERROR: Can't stop INOA without local configuration."
    echo "$ENV_FILE does not exist. Please create it from .env.template and configure at least your local IP."
    exit 1;
fi

# shellcheck source=/dev/null
source $ENV_FILE

mvn k3s:rm -Dk3s.skipRm=false

echo "INOA Cloud services stopped"


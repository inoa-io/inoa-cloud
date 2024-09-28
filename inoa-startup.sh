#!/bin/bash

ENV_FILE=.env
if [ ! -f "$ENV_FILE" ]; then
    echo "ERROR: Can't start INOA without local configuration."
    echo "$ENV_FILE does not exist. Please create it from .env.template and configure at least your local IP."
    exit 1;
fi

# shellcheck source=/dev/null
source $ENV_FILE

INOA_LOCAL_UI=""


############################################################
# Help                                                     #
############################################################
Help()
{
   # Display Help
   echo "Start local INOA Developer Setup."
   echo
   echo "Syntax: ./inoa-startup.sh [-c|m|u|h]"
   echo "options:"
   echo "c     Clean build before start."
   echo "m     Start some simulated Satellites to generate some traffic."
   echo "u     Start local UI for developing Ground Control."
   echo "h     Print help message."
   echo
}

CleanBuild() {
  mvn clean install "${MVN_ARGS[@]}"
}

LaunchK3S() {
  # shellcheck disable=SC2145
  echo "MVN_ARGS=${MVN_ARGS[@]}"
  ### Launch the INOA Cloud services in k3s via Maven
  mvn pre-integration-test "${MVN_ARGS[@]}" -pl ./test/

  echo "INOA configured and started for INOA_DOMAIN=${INOA_DOMAIN}"

  xdg-open "http://help.${INOA_DOMAIN}:8080"
}

### Start Ground Control locally for development Maybe: optional?
LaunchUI() {
  if [ "${INOA_LOCAL_UI}" ]; then
    cd app || exit
    yarn start
  fi

}

### Expose needed service ports to localhost so they are available for development purposes like run the service locally.
### Still needed: local host file must be modified for kafka to work.
ForwardPorts() {
  echo "Forwarding Ports for Postgres, InfluxDB & Kafka to localhost"
  kubectl port-forward services/postgres 5432:5432 & kubectl port-forward services/influxdb 8086:8086 & kubectl port-forward services/kafka 9092:9092 9093:9093 &
  echo "You have to add this entry to your /etc/hosts to be able to run the service locally."
  echo "127.0.0.1       kafka.default.svc.cluster.local"
}

echo "Starting INOA with: -Dinoa.domain=${INOA_DOMAIN}"

############################################################
# Process the input options. Add options as needed.        #
############################################################
# Get the options
# while getopts ":chmu" option; do
while getopts ":chmu" option; do
   case $option in
      h) # display Help
        Help
        exit;;
      c) # clean before launch
        CleanBuild;;
      m)
        export MCNOISE_REPLICAS=10;;
      u) # Launch local UI
        export INOA_LOCAL_UI=1;;
      *) # Invalid options
        echo "Invalid options given."
        Help
        exit;;
   esac
done

MVN_ARGS=("-DskipTests=true" "-Dk3s.failIfExists=false" "-Dk3s.kubeconfig=${KUBECONFIG}" "-Dinoa.domain=${INOA_DOMAIN}" "-Dmcnoise.replicas=${MCNOISE_REPLICAS}")

LaunchK3S
ForwardPorts
LaunchUI

echo "Happy IoT-ing"

#!/bin/bash

source ./.env

INOA_LOCAL_UI=""

MVN_ARGS="-DskipTests=true -Dk3s.failIfExists=false -Dk3s.kubeconfig=${KUBECONFIG} -Dinoa.domain=${INOA_DOMAIN} -Dmcnoise.replicas=${MCNOISE_REPLICAS}"

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
  mvn clean install ${MVN_ARGS}
}

LaunchK3S() {
  ### Launch the INOA Cloud services in k3s via Maven
  mvn pre-integration-test ${MVN_ARGS} -pl ./test/

  echo "INOA configured and started for INOA_DOMAIN=${INOA_DOMAIN}"

  ### Install & connect telepresence to connect to the INOA services
  telepresence helm install --kubeconfig=${KUBECONFIG}
  telepresence connect --kubeconfig=${KUBECONFIG}

  xdg-open "http://help.${INOA_DOMAIN}:8080"
}

### Start Ground Control locally for development Maybe: optional?
LaunchUI() {
  if [ "${INOA_LOCAL_UI}" ]; then
    cd app || exit
    yarn start
  fi

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

LaunchK3S
LaunchUI

echo "Happy IoT-ing"

#!/bin/bash

### Defines the IP address that is used to expose INOA services.
### TODO Find a generic way for interface detection.

INOA_IP="$(ip -4 addr show wlp3s0 | grep -oP '(?<=inet\s)\d+(\.\d+){3}')"

export INOA_IP
export INOA_REPLICAS=1
export MCNOISE_REPLICAS=0
export INOA_LOCAL_UI=""

############################################################
# Help                                                     #
############################################################
Help()
{
   # Display Help
   echo "Start local INOA Developer Setup."
   echo
   echo "Syntax: ./inoa-startup.sh [-c|m|n|u|h]"
   echo "options:"
   echo "c     Clean build before start."
   echo "m     Start some simulated Satellites to generate some traffic."
   echo "n     No INOA service launched in k3s. Used for local running INOA service."
   echo "u     Start local UI for developing Ground Control."
   echo "h     Print help message."
   echo
}

LaunchK3S() {
  ### Launch the INOA Cloud services in k3s via Maven
  mvn pre-integration-test -Dk3s.failIfExists=false -Dk3s.ip="${INOA_IP}" -Dinoa.replicas="${INOA_REPLICAS}" -Dmcnoise.replicas="${MCNOISE_REPLICAS}" -pl ./test/

  echo "INOA configured and started with INOA_IP=${INOA_IP}"

  xdg-open "http://help.${INOA_IP}.nip.io:8080"
}

### Start Ground Control locally for development Maybe: optional?
LaunchUI() {
  if [ "${INOA_LOCAL_UI}" ]; then
    cd app || exit
    yarn start
  fi

}
############################################################
# Process the input options. Add options as needed.        #
############################################################
# Get the options
while getopts ":chmnu" option; do
   case $option in
      h) # display Help
        Help
        exit;;
      c) # clean before launch
        mvn clean install -DskipTests;;
      m)
        export MCNOISE_REPLICAS=10;;
      n) # Do not start INOA in k3s
        export INOA_REPLICAS=0;;
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

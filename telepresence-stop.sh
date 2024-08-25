#!/bin/bash

### Connect to a running k8s cluster and interact with INOA services.
### If you like to use it for other clusters than the local k3s, you can simply set the $KUBECONFIG environment variable.

KUBECONFIG=${KUBECONFIG:-$HOME/.kube/k3s.yaml}

echo "Closing telepresence session with KUBECONFIG=${KUBECONFIG}"

telepresence leave inoa-http
telepresence leave inoa-mqtt

telepresence quit -s

#!/bin/bash

### Connect to a running k8s cluster and interact with INOA services.
### If you like to use it for other clusters than the local k3s, you can simply set the $KUBECONFIG environment variable.

KUBECONFIG=${KUBECONFIG:-$HOME/.kube/k3s.yaml}

echo "Opening telepresence session with KUBECONFIG=${KUBECONFIG}"

telepresence helm install --kubeconfig="$KUBECONFIG"
telepresence connect --kubeconfig="$KUBECONFIG"

telepresence intercept inoa-http --workload=inoa --service=inoa --port 4300:http
telepresence intercept inoa-mqtt --workload=inoa --service=inoa --port 1884:mqtt

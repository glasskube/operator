#!/usr/bin/env bash

function help() {
  HELP_NOTE=$1
  EXIT_CODE=0

  if [ -n "$HELP_NOTE" ]; then
    EXIT_CODE=1
    echo "$HELP_NOTE
"
  fi

  echo "Usage: $SCRIPT [options]

Options:
  -v VERSION (required)
    Check https://hub.docker.com/r/glasskube/operator/tags for available versions.
  -n NAMESPACE (optional)
    Specifies the namespace where all operators will be installed. If NAMESPACE is
    missing or empty, a cluster-wide installation will be performed.
  -h
    Show this help."
  exit "$EXIT_CODE"
}

function mk_temp_kustomization() {
  mkdir -p "$TEMP_DIR"
  echo "
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - ../$1
images:
  - name: glasskube/operator
    newTag: $VERSION
" >"$TEMP_DIR/kustomization.yaml"
}

SCRIPT=$(basename "$0")
GIT_ROOT=$(git rev-parse --show-toplevel)
DEPLOY_ROOT="$GIT_ROOT/deploy"
TEMP_DIR="$DEPLOY_ROOT/temp"

while getopts ":hv:n:" option; do
  case $option in
  h)
    help ""
    ;;
  v)
    VERSION="$OPTARG"
    ;;
  n)
    NAMESPACE="$OPTARG"
    ;;
  \?)
    help "Error: Invalid option."
    ;;
  esac
done

if [ -z "$VERSION" ]; then
  help "Error: VERSION is missing."
fi

helm repo add jetstack https://charts.jetstack.io
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add mmontes https://charts.mmontes-dev.duckdns.org
helm repo update

kubectl apply -f "$GIT_ROOT/deploy/crd"

if [ -z "$NAMESPACE" ]; then
  echo "Performing cluster-wide deployment of version $VERSION…"
  helm install cert-manager jetstack/cert-manager -n cert-manager --create-namespace --set installCRDs=true
  helm install kube-prometheus-stack prometheus-community/kube-prometheus-stack -n kube-prometheus-stack --create-namespace
  helm install mariadb-operator mmontes/mariadb-operator -n mariadb-system --create-namespace --set ha.enabled=false
  mk_temp_kustomization "cluster"
  kubectl apply -k "$TEMP_DIR"
else
  echo "Performing single-namespace deployment of version $VERSION in $NAMESPACE…"
  kubectl create namespace "$NAMESPACE"
  helm install cert-manager jetstack/cert-manager -n "$NAMESPACE" --set installCRDs=true
  helm install kube-prometheus-stack prometheus-community/kube-prometheus-stack -n "$NAMESPACE"
  helm install mariadb-operator mmontes/mariadb-operator -n "$NAMESPACE" --set ha.enabled=false
  mk_temp_kustomization "namespace"
  kubectl apply -k "$TEMP_DIR" -n "$NAMESPACE"
fi

echo "Cleaning up…"
rm -rf "$TEMP_DIR"

echo "Done."

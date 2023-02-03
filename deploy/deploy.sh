#!/usr/bin/env bash

function help {
  HELP_NOTE=$1
  EXIT_CODE=0

  if [ -n "$HELP_NOTE" ]; then
    EXIT_CODE=1
    echo "$HELP_NOTE
"
  fi

  echo "Usage: $SCRIPT [options]

Options:
  -v VERSION
    Check https://hub.docker.com/r/glasskube/operator/tags for available versions.
    If no version is provided, only dependencies are installed.
  -n NAMESPACE
    Specifies the namespace where all operators will be installed. If NAMESPACE is
    missing or empty, a cluster-wide installation will be performed.
  -g HOST_NAME
    Specifies the host name that should be used for the Grafana ingress. If no host
    name is provided, the Grafana ingress will not be enabled.
  -p AMOUNT
    Specifies the size of the prometheus storage claim(e.g. \"10Gi\"). If no value is
    set, prometheus persistence will not be enabled.
  -h
    Show this help."
  exit "$EXIT_CODE"
}

function confirm {
  read -p "$1 (y/N) " -r
  if [[ $REPLY =~ ^[Yy]$ ]]; then
    return 0
  else
    return 1
  fi
}

function install_helm_charts {
  NS_CERT_MANAGER="cert-manager"
  NS_PROMETHEUS="kube-prometheus-stack"
  NS_MARIADB="mariadb-system"
  NS_CNPG="cnpg-system"
  NS_MINIO="glasskube-system"
  NS_OVERRIDE=$1

  if [ -n "$NS_OVERRIDE" ]; then
    NS_CERT_MANAGER="$NS_OVERRIDE"
    NS_PROMETHEUS="$NS_OVERRIDE"
    NS_MARIADB="$NS_OVERRIDE"
    NS_CNPG="$NS_OVERRIDE"
    NS_MINIO="$NS_OVERRIDE"
  fi

  helm upgrade --install cert-manager jetstack/cert-manager \
    --namespace "$NS_CERT_MANAGER" --create-namespace \
    --set installCRDs=true

  local PROMETHEUS_ARGS=(
    "--set" "prometheus.prometheusSpec.retention=180d"
    "--set" "prometheus.prometheusSpec.podMonitorSelectorNilUsesHelmValues=false"
    "--set" "prometheus.prometheusSpec.ruleSelectorNilUsesHelmValues=false"
    "--set" "prometheus.prometheusSpec.serviceMonitorSelectorNilUsesHelmValues=false"
    "--set" "prometheus.prometheusSpec.probeSelectorNilUsesHelmValues=false"
  )
  if [ -n "$GRAFANA_HOST" ]; then
    local PROMETHEUS_ARGS+=("--set" "grafana.ingress.enabled=true")
    local PROMETHEUS_ARGS+=("--set" "grafana.ingress.hosts={$GRAFANA_HOST}")
    local PROMETHEUS_ARGS+=("--set" "grafana.persistence.enabled=true")
  fi
  if [ -n "$PROMETHEUS_SIZE" ]; then
    local PROMETHEUS_ARGS+=("--set" "prometheus.prometheusSpec.storageSpec.volumeClaimTemplate.spec.resources.requests.storage=$PROMETHEUS_SIZE")
  fi
  helm upgrade --install kube-prometheus-stack prometheus-community/kube-prometheus-stack \
    --namespace "$NS_PROMETHEUS" --create-namespace \
    "${PROMETHEUS_ARGS[@]}"
  kubectl apply \
    -n "$NS_PROMETHEUS" \
    -f https://raw.githubusercontent.com/cloudnative-pg/cloudnative-pg/91f3af3ecf92aa415a4498f62e2a8d01156d70ee/docs/src/samples/monitoring/grafana-configmap.yaml

  helm upgrade --install mariadb-operator mariadb-operator/mariadb-operator \
    --namespace "$NS_MARIADB" --create-namespace \
    --version 0.6.1 \
    --set ha.enabled=false \
    --set metrics.enabled=true --set metrics.serviceMonitor.enabled=false --set webhook.serviceMonitor.enabled=false

  helm upgrade --install cnpg cnpg/cloudnative-pg \
    --namespace "$NS_CNPG" --create-namespace

  helm upgrade --install glasskube-minio minio/minio \
    --namespace "$NS_MINIO" --create-namespace \
    --set replicas=1 --set persistence.size=20Gi --set mode=standalone --set DeploymentUpdate.type=Recreate \
    --set resources.requests.memory=256Mi
}

function mk_temp_kustomization {
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

while getopts ":hv:n:g:p:" option; do
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
  g)
    GRAFANA_HOST="$OPTARG"
    ;;
  p)
    PROMETHEUS_SIZE="$OPTARG"
    ;;
  \?)
    help "Error: Invalid option."
    ;;
  esac
done

if [ -z "$VERSION" ]; then
  echo "Warning: VERSION is missing."
  if ! confirm "Install helm charts without operator?"; then
    help "Error: Nothing was installed."
  fi
fi

helm repo add jetstack https://charts.jetstack.io
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add mariadb-operator https://mmontes11.github.io/mariadb-operator
helm repo add cnpg https://cloudnative-pg.github.io/charts
helm repo add minio https://charts.min.io/
helm repo update

kubectl apply -f "$GIT_ROOT/deploy/crd"

if [ -z "$NAMESPACE" ]; then
  echo "Performing cluster-wide deployment of version $VERSION…"
  install_helm_charts ""
  if [ -n "$VERSION" ]; then
    mk_temp_kustomization "cluster"
    kubectl apply -k "$TEMP_DIR"
  fi
else
  echo "Performing single-namespace deployment of version $VERSION in $NAMESPACE…"
  install_helm_charts "$NAMESPACE"
  if [ -n "$VERSION" ]; then
    mk_temp_kustomization "namespace"
    kubectl apply -k "$TEMP_DIR" -n "$NAMESPACE"
  fi
fi

echo "Cleaning up…"
rm -rf "$TEMP_DIR"

echo "Done."

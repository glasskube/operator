#!/usr/bin/env bash

NAMESPACE=$1
GIT_ROOT=$(git rev-parse --show-toplevel)

helm repo add jetstack https://charts.jetstack.io
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add mmontes https://charts.mmontes-dev.duckdns.org
helm repo update

kubectl apply -f "$GIT_ROOT/crd"

if [ -z "$NAMESPACE" ]; then
  helm install cert-manager jetstack/cert-manager -n cert-manager --create-namespace --set installCRDs=true
  helm install kube-prometheus-stack prometheus-community/kube-prometheus-stack -n kube-prometheus-stack --create-namespace
  helm install mariadb-operator mmontes/mariadb-operator -n mariadb-system --create-namespace --set ha.enabled=false
  kubectl apply -k "$GIT_ROOT/cluster"
else
  kubectl create namespace "$NAMESPACE"
  helm install cert-manager jetstack/cert-manager -n "$NAMESPACE" --set installCRDs=true
  helm install kube-prometheus-stack prometheus-community/kube-prometheus-stack -n "$NAMESPACE"
  helm install mariadb-operator mmontes/mariadb-operator -n "$NAMESPACE" --set ha.enabled=false
  kubectl apply -k "$GIT_ROOT/namespace" -n "$NAMESPACE"
fi

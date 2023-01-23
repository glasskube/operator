#!/usr/bin/env bash

# Currently only multi namespace deployments are supported

helm upgrade --install cert-manager jetstack/cert-manager --namespace cert-manager --create-namespace
helm upgrade --install kube-prometheus-stack prometheus-community/kube-prometheus-stack --namespace kube-prometheus-stack --create-namespace
helm upgrade --install mariadb-operator mmontes/mariadb-operator --namespace mariadb-system --create-namespace --set ha.enabled=false
helm upgrade --install cnpg cnpg/cloudnative-pg --namespace cnpg-system --create-namespace

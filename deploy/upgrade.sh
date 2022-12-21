#!/usr/bin/env bash

# Currently only multi namespace deployments are supported

helm upgrade cert-manager jetstack/cert-manager -n cert-manager
helm upgrade kube-prometheus-stack prometheus-community/kube-prometheus-stack -n kube-prometheus-stack
helm upgrade mariadb-operator mmontes/mariadb-operator -n mariadb-system --set ha.enabled=false

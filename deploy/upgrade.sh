#!/usr/bin/env bash

helm upgrade cert-manager jetstack/cert-manager -n cert-manager
helm upgrade kube-prometheus-stack prometheus-community/kube-prometheus-stack -n kube-prometheus-stack
helm upgrade mariadb-operator mmontes/mariadb-operator -n mariadb-system --set ha.enabled=false

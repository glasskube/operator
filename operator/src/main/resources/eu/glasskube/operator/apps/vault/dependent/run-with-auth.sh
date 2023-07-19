SERVICE_ACCOUNT_TOKEN="$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)"
VAULT_TOKEN=$(
    vault write auth/kubernetes/login "role=$AUTH_ROLE_NAME" "jwt=$SERVICE_ACCOUNT_TOKEN" | sed -nr "s/token\s+(\S*)/\1/p"
) /usr/local/bin/docker-entrypoint.sh vault server -config=/glasskube/config/config.hcl

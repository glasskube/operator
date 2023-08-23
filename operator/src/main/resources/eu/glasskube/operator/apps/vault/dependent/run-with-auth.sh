SERVICE_ACCOUNT_TOKEN="$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)"

VAULT_TOKEN=$(vault write "auth/$AUTH_PATH/login" "role=$AUTH_ROLE_NAME" "jwt=$SERVICE_ACCOUNT_TOKEN" | sed -nr "s/token\s+(\S*)/\1/p")

if [ -z "$VAULT_TOKEN" ]; then
  echo "authentication failed. exiting."
  exit 1
fi

export VAULT_TOKEN
/usr/local/bin/docker-entrypoint.sh vault server -config=/glasskube/config/config.hcl

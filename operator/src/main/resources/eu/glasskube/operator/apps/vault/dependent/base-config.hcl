disable_mlock = true

listener "tcp" {
  address = "0.0.0.0:8200"
  cluster_address = "0.0.0.0:8201"
  tls_cert_file = "/glasskube/tls/tls.crt"
  tls_key_file  = "/glasskube/tls/tls.key"
  tls_client_ca_file = "/glasskube/tls/ca.crt"
}

storage "postgresql" {
  ha_enabled = true
}

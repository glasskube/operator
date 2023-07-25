postgresql['enable'] = false

gitlab_rails['db_adapter'] = 'postgresql'
gitlab_rails['db_encoding'] = 'unicode'
gitlab_rails['db_host'] = ENV['DB_HOST']
gitlab_rails['db_password'] = ENV['DB_PASSWORD']

gitlab_rails['initial_root_password'] = ENV['INITIAL_ROOT_PASSWORD']

external_url "http://" + ENV['GITLAB_HOST']
gitlab_rails['gitlab_ssh_host'] = ENV['GITLAB_SSH_HOST']

puma['worker_processes'] = 0
sidekiq['max_concurrency'] = 10
nginx['worker_processes'] = 2
nginx['listen_https'] = false
nginx['real_ip_trusted_addresses'] = ['10.0.0.0/8', '172.16.0.0/12', '192.168.0.0/16']

gitlab_rails['env'] = {
  'MALLOC_CONF' => 'dirty_decay_ms:1000,muzzy_decay_ms:1000'
}

gitaly['configuration'] = {
  concurrency: [
    {
      rpc: '/gitaly.SmartHTTPService/PostReceivePack',
      max_per_repo: 3
    }, {
      rpc: '/gitaly.SSHService/SSHUploadPack',
      max_per_repo: 3
    }
  ],
  prometheus_listen_addr: '0.0.0.0:9236'
}
gitaly['env'] = {
  'MALLOC_CONF' => 'dirty_decay_ms:1000,muzzy_decay_ms:1000',
  'GITALY_COMMAND_SPAWN_MAX_PARALLEL' => '2'
}

prometheus_monitoring['enable'] = false
gitlab_exporter['enable'] = true
gitlab_exporter['listen_address'] = '0.0.0.0'
sidekiq['listen_address'] = '0.0.0.0'

gitlab_rails['incoming_email_enabled'] = false

gitlab_rails['smtp_enable'] = ENV['SMTP_ENABLED']
gitlab_rails['smtp_address'] = ENV['SMTP_HOST']
gitlab_rails['smtp_port'] = ENV['SMTP_PORT']
gitlab_rails['smtp_user_name'] = ENV['SMTP_USERNAME']
gitlab_rails['smtp_password'] = ENV['SMTP_PASSWORD']
gitlab_rails['smtp_tls'] = ENV['SMTP_TLS_ENABLED']
gitlab_rails['gitlab_email_from'] = ENV['SMTP_FROM_ADDRESS']


if (ENV['REGISTRY_ENABLED'] == 'true')
  registry['enable'] = ENV['REGISTRY_ENABLED']
  gitlab_rails['registry_enabled'] = ENV['REGISTRY_ENABLED']
  registry['token_realm'] = "https://" + ENV['GITLAB_HOST']
  gitlab_rails['registry_enabled'] = ENV['REGISTRY_ENABLED']
  gitlab_rails['registry_host'] = ENV['GITLAB_REGISTRY_HOST']
  gitlab_rails['registry_api_url'] = "http://localhost:5000"

  registry_external_url 'https://' + ENV['GITLAB_REGISTRY_HOST']
  registry_nginx['redirect_http_to_https'] = true
  registry_nginx['listen_port'] = 5443
  registry_nginx['ssl_certificate'] = "/etc/gitlab/ssl/tls.crt"
  registry_nginx['ssl_certificate_key'] = "/etc/gitlab/ssl/tls.key"
  registry_nginx['real_ip_trusted_addresses'] = ['10.0.0.0/8', '172.16.0.0/12', '192.168.0.0/16']

  if (ENV['REGISTRY_OBJECTSTORE_ENABLED'] == 'true')
    registry['storage'] = {
      's3' => {
        'bucket' => ENV['REGISTRY_OBJECTSTORE_S3_BUCKET'],
        'accesskey' => ENV['REGISTRY_OBJECTSTORE_S3_KEY'],
        'secretkey' => ENV['REGISTRY_OBJECTSTORE_S3_SECRET'],
        'region' => ENV['REGISTRY_OBJECTSTORE_S3_REGION'],
        'regionendpoint' => ENV['REGISTRY_OBJECTSTORE_S3_HOST'],
        'pathstyle' => ENV['REGISTRY_OBJECTSTORE_S3_USEPATH_STYLE']
      }
    }
  end
end


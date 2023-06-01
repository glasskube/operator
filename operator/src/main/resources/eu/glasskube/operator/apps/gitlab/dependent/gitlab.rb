postgresql['enable'] = false

gitlab_rails['db_adapter'] = 'postgresql'
gitlab_rails['db_encoding'] = 'unicode'
gitlab_rails['db_host'] = ENV['DB_HOST']
gitlab_rails['db_password'] = ENV['DB_PASSWORD']

gitlab_rails['initial_root_password'] = ENV['INITIAL_ROOT_PASSWORD']

external_url ENV['GITLAB_HOST']
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

# TODO: SMTP
# gitlab_rails['smtp_enable'] = true
# gitlab_rails['smtp_address'] = "smtp.server"
# gitlab_rails['smtp_port'] = 465
# gitlab_rails['smtp_user_name'] = "smtp user"
# gitlab_rails['smtp_password'] = "smtp password"
# gitlab_rails['smtp_domain'] = "example.com"
# gitlab_rails['smtp_authentication'] = "login"
# gitlab_rails['smtp_enable_starttls_auto'] = true
# gitlab_rails['smtp_tls'] = false
# gitlab_rails['smtp_pool'] = false
# gitlab_rails['gitlab_email_from'] = 'example@example.com'
# gitlab_rails['gitlab_email_display_name'] = 'Example'

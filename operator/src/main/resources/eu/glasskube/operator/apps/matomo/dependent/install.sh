#!/usr/bin/env sh

abort() {
  echo "$1"
  exit 1
}

convert_db_and_create_security_files() {
  ./console core:convert-to-utf8mb4 --yes || abort "could not convert database"
  ./console core:create-security-files || abort "could not create security files"
}

./console matomo:install --install-file "$MATOMO_INSTALL_FILE" --force --do-not-drop-db || abort "could not install matomo"
convert_db_and_create_security_files

if ./console site:list; then
  echo "site:list exit code was 0. do not create a new site"
else
  echo "site:list exit code was not 0. creating new site"
  ./console site:add --name "$MATOMO_FIRST_SITE_NAME" --urls "$MATOMO_FIRST_SITE_URL" || abort "could not add site"
  convert_db_and_create_security_files
fi

#!/usr/bin/env sh

abort() {
  echo "$1"
  exit 1
}

./console matomo:install --install-file "$MATOMO_INSTALL_FILE" --force --do-not-drop-db ||
  abort "could not install matomo"

if ./console site:list; then
  echo "site:list exit code was 0. do not create a new site"
else
  echo "site:list exit code was not 0. creating new site"
  ./console site:add --name "$MATOMO_FIRST_SITE_NAME" --urls "$MATOMO_FIRST_SITE_URL" ||
    abort "could not add site"
fi

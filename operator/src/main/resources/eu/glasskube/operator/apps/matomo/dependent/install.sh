#!/usr/bin/env sh

abort() {
  echo "$1"
  exit 1
}

./console matomo:install --install-file "$MATOMO_INSTALL_FILE" --force --do-not-drop-db || abort "could not install matomo"

./console core:update --yes || abort "upgrade failed"

if ./console site:list; then
  echo "site:list exit code was 0. do not create a new site"
else
  echo "site:list exit code was not 0. creating new site"
  ./console site:add --name "$MATOMO_FIRST_SITE_NAME" --urls "$MATOMO_FIRST_SITE_URL" || abort "could not add site"
fi

# Note: if the command returns Command "core:convert-to-utf8mb4" is not defined.
# then your database should already be using utf8mb4 and you don’t need to run the command.
# https://matomo.org/faq/how-to-update/how-to-convert-the-database-to-utf8mb4-charset/

./console core:convert-to-utf8mb4 --yes || echo "could not convert database or database already converted"
./console core:create-security-files || abort "could not create security files"

./console plugin:activate TagManager || abort "could not install tag manager"
./console plugin:deactivate ProfessionalServices Marketplace Feedback || echo "could not uninstall professional services, marketplace and feedback"

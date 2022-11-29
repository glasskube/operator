#!/bin/bash

ls -la /tmp/matomo
mkdir /tmp/install
cp /tmp/matomo/install.json /tmp/install/install.json
sed -i "s/%MATOMO_DATABASE_PASSWORD%/$MATOMO_DATABASE_PASSWORD/g" /tmp/install/install.json
rsync -crlOt --no-owner --no-group --no-perms /usr/src/matomo/ /var/www/html/

./console plugin:activate ExtraTools && ./console matomo:install --install-file=/tmp/install/install.json --force --do-not-drop-db

./console site:list

if [ $? -eq 1 ]; then
  echo "Init first test page"
  ./console site:add --name="MTL Test Site" --urls="https://glasskube.media-lab.de"
fi

echo $?

chown -R www-data:www-data /var/www/html
echo done

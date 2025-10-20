#!/usr/bin/env sh

echo "

[General]
always_load_commands_from_plugin=ExtraTools
force_ssl = 1
maintenance_mode = 1

[Tracker]
record_statistics = 0
" >> /usr/src/matomo/config/config.ini.php

rsync --checksum --recursive --links --times --omit-dir-times --no-owner --no-group --no-perms --delete \
  /usr/src/matomo/ /var/www/html/ \
  --exclude=tmp --exclude=misc

rsync --checksum --recursive --links --times --omit-dir-times --no-owner --no-group --no-perms \
  /usr/src/matomo/misc/ /var/www/html/misc

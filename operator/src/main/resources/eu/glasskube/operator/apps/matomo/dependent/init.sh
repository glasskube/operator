#!/usr/bin/env sh

echo "
[General]
always_load_commands_from_plugin=ExtraTools
" >/usr/src/matomo/config/config.ini.php

rsync --checksum --recursive --links --times --omit-dir-times --no-owner --no-group --no-perms \
  /usr/src/matomo/ /var/www/html/

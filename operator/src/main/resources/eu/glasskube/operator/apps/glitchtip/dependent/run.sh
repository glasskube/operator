#!/bin/bash

encode () {
  python3 -c "from urllib.parse import quote; print(quote(\"${1}\", safe=''))"
}

EMAIL_URL=${EMAIL_URL/$SMTP_USERNAME/$(encode $SMTP_USERNAME)}
EMAIL_URL=${EMAIL_URL/$SMTP_PASSWORD/$(encode $SMTP_PASSWORD)}

export EMAIL_URL

cd /code
bash -c bin/start.sh

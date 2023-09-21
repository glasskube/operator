#!/bin/bash
set -e

python manage.py wait_for_db
python manage.py migrate

# Create a Default User
python bin/user_script.py

exec gunicorn -w "$BACKEND_WORKERS" -k uvicorn.workers.UvicornWorker plane.asgi:application --bind 0.0.0.0:8000 --max-requests 1200 --max-requests-jitter 1000 --access-logfile -

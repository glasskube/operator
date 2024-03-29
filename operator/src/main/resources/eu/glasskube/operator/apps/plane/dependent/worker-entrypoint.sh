#!/bin/bash
set -e

python manage.py wait_for_db

celery -A plane worker -l info --concurrency "$WORKERS"

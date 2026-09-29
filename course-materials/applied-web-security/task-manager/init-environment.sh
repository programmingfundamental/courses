#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
if [ -f .env ]; then printf '%s\n' '.env already exists; values preserved.'; exit 0; fi
umask 077
printf 'DB_PASSWORD=%s\nJWT_SECRET=%s\n' "$(openssl rand -base64 32)" "$(openssl rand -base64 32)" > .env
printf '%s\n' 'Created .env for Docker Compose.'

#!/bin/bash
set -euo pipefail

ROOT_DIR="/home/cata884/Downloads/db_project/Project"
DB_DIR="$ROOT_DIR/database"
FRONTEND_DIR="$ROOT_DIR/frontend"

# Start Oracle XE container
sudo podman run --replace -d --name oracle-xe \
  -p 1521:1521 \
  -e ORACLE_PASSWORD=parola \
  -e APP_USER=movie_user \
  -e APP_USER_PASSWORD=parola \
  docker.io/gvenzl/oracle-xe:21-slim

# Wait for DB to be ready (up to 3 minutes)
echo "Waiting for Oracle XE to be ready..."
# shellcheck disable=SC2034
for i in {1..36}; do
  if sudo podman logs oracle-xe 2>/dev/null | grep -q "DATABASE IS READY TO USE!"; then
    echo "Oracle XE is ready."
    break
  fi
  sleep 5
done

# Recreate schema
cd "$DB_DIR"
sh ./script.sh drop
sh ./script.sh create

# Run backend in background
cd "$ROOT_DIR"
./mvnw spring-boot:run &
BACKEND_PID=$!
echo "Backend started (PID $BACKEND_PID)."

# Build frontend
cd "$FRONTEND_DIR"
npm run dev

echo "Setup complete. Backend is still running (PID $BACKEND_PID)."

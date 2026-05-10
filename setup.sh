#!/bin/bash
set -euo pipefail

echo "Setup db_project script. Killing any process on port 8080"

PIDS=$(lsof -t -i:8080 2>/dev/null || true)
if [ -n "$PIDS" ]; then
  kill -9 "$PIDS" 2>/dev/null || true
fi

ROOT_DIR="/home/cata884/Downloads/db_project/Project"
DB_DIR="$ROOT_DIR/database"
FRONTEND_DIR="$ROOT_DIR/frontend"

sudo podman run --replace -d --name oracle-xe \
  -p 1521:1521 \
  -e ORACLE_PASSWORD=parola \
  -e APP_USER=movie_user \
  -e APP_USER_PASSWORD=parola \
  docker.io/gvenzl/oracle-xe:21-slim

echo "Waiting for Oracle XE to be ready..."
# shellcheck disable=SC2034
for i in {1..36}; do
  if sudo podman logs oracle-xe 2>/dev/null | grep -q "DATABASE IS READY TO USE!"; then
    echo "Oracle XE is ready."
    break
  fi
  sleep 5
done

cd "$DB_DIR"
sh ./script.sh drop
sh ./script.sh create

cd "$ROOT_DIR"
./mvnw spring-boot:run &
BACKEND_PID=$!
echo "Backend started (PID $BACKEND_PID)."

echo "Aștept ca backend-ul să fie ready pe :8080..."

# shellcheck disable=SC2034
for i in {1..60}; do
  if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/categorii | grep -qE "200|401|403|404|500"; then
    echo "Backend ready."
    break
  fi
  sleep 1
done

cd "$FRONTEND_DIR"
npm run dev

echo "Setup complete. Backend is still running (PID $BACKEND_PID)."

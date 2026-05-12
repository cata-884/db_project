#!/bin/bash
command=$1

if [ "$command" != "create" ] && [ "$command" != "drop" ]; then
  echo "Usage: $0 [create|drop]"
  exit 1
fi
if [ "$command" == "create" ]; then
  echo "Creating database..."
  for f in 0[1-5]_*.sql; do
      # shellcheck disable=SC2024
      sudo podman exec -i oracle-xe sqlplus -s movie_user/parola@//localhost:1521/XEPDB1 < "$f"
  done
else
  echo "Dropping database..."
  # shellcheck disable=SC2024
  sudo podman exec -i oracle-xe sqlplus -s movie_user/parola@//localhost:1521/XEPDB1 < "99_drop_all.sql"
fi
echo "done"
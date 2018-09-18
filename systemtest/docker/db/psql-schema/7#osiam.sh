if test x"$(psql -U veraweb -tAc "SELECT 1 FROM pg_database WHERE datname='ong'")" != x"1"; then
  echo "CREATE USER ong WITH PASSWORD 'b4s3dg0d';" | psql -U veraweb
  echo "CREATE DATABASE ong;" | psql -U veraweb
  echo "GRANT ALL PRIVILEGES ON DATABASE ong TO ong;" | psql -U veraweb
fi

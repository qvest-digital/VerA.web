if test x"$(psql -tAc "SELECT 1 FROM pg_database WHERE datname='ong'")" != x"1"; then
  echo "CREATE USER ong WITH PASSWORD 'b4s3dg0d';" | psql
  echo "CREATE DATABASE ong;" | psql
  echo "GRANT ALL PRIVILEGES ON DATABASE ong TO ong;" | psql
fi

FROM postgres:15-alpine
COPY 01-tabelas.sql /docker-entrypoint-initdb.d/01-tabelas.sql
COPY 02-setup-secops.sh /docker-entrypoint-initdb.d/02-setup-secops.sh
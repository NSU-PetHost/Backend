FROM postgres:15

COPY createDB.sql /docker-entrypoint-initdb.d/

EXPOSE 5432

VOLUME ["/var/lib/postgresql/data"]

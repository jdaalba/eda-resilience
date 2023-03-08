FROM postgres
#ENV POSTGRES_DB resilience
EXPOSE 5432
COPY scripts/init.sql /docker-entrypoint-initdb.d/

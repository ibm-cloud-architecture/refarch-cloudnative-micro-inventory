FROM mysql:5.7.14

ADD scripts/load-data.sql /docker-entrypoint-initdb.d/

CMD ["mysqld"]

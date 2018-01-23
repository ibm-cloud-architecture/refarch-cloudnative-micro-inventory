FROM mysql

ADD scripts/load-data.sh load-data.sh
ADD scripts/load-data.sql load-data.sql
RUN chmod u+x load-data.sh

CMD ["mysqld"]

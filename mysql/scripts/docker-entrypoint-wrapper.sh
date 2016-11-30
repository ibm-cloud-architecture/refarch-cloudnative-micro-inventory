#!/bin/bash

my_hostname=`hostname`
my_ip=`getent hosts ${my_hostname} | head -1 | awk '{print $1;}'`

sed -i -e 's/server-id=.*/server-id='${SERVER_ID}'/' /etc/mysql/mysql.conf.d/binlog.cnf

sed -i -e 's/auto-increment-offset = .*/auto-increment-offset = '$(((SERVER_ID % 2) + 1))'/' /etc/mysql/mysql.conf.d/auto_increment.cnf

sed -i -e 's/report-host=.*/report-host='${my_ip}'/' /etc/mysql/mysql.conf.d/binlog.cnf
    
/usr/local/bin/docker-entrypoint.sh $@




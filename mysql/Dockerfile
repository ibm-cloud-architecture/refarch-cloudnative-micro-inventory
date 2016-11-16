FROM mysql

ARG server_id=1
ARG master_ip
ARG master_root_passwd

ENV SERVER_ID ${server_id}
ENV MASTER_IP ${master_ip}
ENV MASTER_ROOT_PASSWORD ${master_root_passwd}

RUN echo "deb http://ppa.launchpad.net/fkrull/deadsnakes/ubuntu trusty main" > /etc/apt/sources.list.d/python.list

RUN gpg --keyserver keyserver.ubuntu.com --recv-keys DB82666C
RUN gpg --export DB82666C | apt-key add - 
RUN apt-get update
RUN apt-get -y install curl python2.6

RUN curl https://bootstrap.pypa.io/get-pip.py | python2.6

RUN echo "http://cdn.mysql.com//Downloads/MySQLGUITools/mysql-utilities-1.6.4.tar.gz" > /tmp/requirements.txt
RUN echo "http://cdn.mysql.com//Downloads/Connector-Python/mysql-connector-python-2.1.3.tar.gz" >> /tmp/requirements.txt

RUN pip install -r /tmp/requirements.txt
RUN rm -f /tmp/requirements.txt

COPY etc/mysql/mysql.conf.d/binlog.cnf /etc/mysql/mysql.conf.d/binlog.cnf
COPY etc/mysql/mysql.conf.d/auto_increment.cnf /etc/mysql/mysql.conf.d/auto_increment.cnf

ADD scripts /root/scripts
WORKDIR /root/scripts

RUN chmod u+x /root/scripts/*.sh

ENTRYPOINT ["/root/scripts/docker-entrypoint-wrapper.sh"]
CMD ["mysqld"]

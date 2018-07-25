#!/bin/sh

mysql_user=$1
mysql_password=$2
mysql_host=$3
mysql_port=$4
mysql_database=$5

usage () {
	printf "USAGE:\n\tbash load-data.sh USER PASSWORD HOST PORT DATABASE\n\n"
	exit 1
}

wait_mysql () {
	printf "Waiting for MySQL to fully initialize\n\n"
	sleep 1
    echo "trying to load data again"
}

# User
if [[ -z "$mysql_user" ]]; then
	echo "User not provided. Attempting container environment variable..."
	mysql_user=$MYSQL_USER
fi

if [[ -z "$mysql_user" ]]; then
	echo "Unable to get user."
	usage
fi

if [[ -z "$mysql_password" ]]; then
	echo "Password not provided. Attempting container environment variable..."
	mysql_password=$MYSQL_PASSWORD
fi

# Optional
if [[ -z "$mysql_host" ]]; then
	echo "Host not provided. Using localhost..."
	mysql_host='0.0.0.0'
fi

if [[ -z "$mysql_port" ]]; then
	echo "Port not provided. Using 3306..."
	mysql_port='3306'
fi

if [[ -z "$mysql_database" ]]; then
	echo "Database not provided. Attempting container environment variable..."
	mysql_database=$MYSQL_DATABASE
fi

if [[ -z "$mysql_database" ]]; then
	echo "Database not provided. Using inventorydb..."
	mysql_database='inventorydb'
fi

# Replacing database name in mysql_data.sql
echo "Replacing database name in mysql_data.sql"
if [[ $OSTYPE =~ .*darwin.* ]]; then
	sed -i.bak "s/inventorydb/${mysql_database}/g" mysql_data.sql
else
	sed -i "s/inventorydb/${mysql_database}/g" mysql_data.sql
fi

# load data
echo "Loading data..."
if [[ -z "$mysql_password" ]]; then
	echo "No password set. Probably using passwordless root"
	while !(mysql -u${mysql_user} --host ${mysql_host} --port ${mysql_port} <mysql_data.sql)
	do
		wait_mysql
	done
else
	echo "Password set"
	while !(mysql -u${mysql_user} -p${mysql_password} --host ${mysql_host} --port ${mysql_port} <mysql_data.sql)
	do
		wait_mysql
	done
fi

printf "\n\nData loaded to %s.items.\n\n" "${mysql_database}"
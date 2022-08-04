#! /bin/bash
folder=/tmp/$(logname)/mydb
PGDATA=$folder/data
PGSOCKETS=$folder/sockets
export PGDATA
export PGSOCKETS

#Initialize folders
rm -fr $PGDATA
rm -fr $PGSOCKETS
mkdir -p $PGDATA
mkdir -p $PGSOCKETS
sleep 1

#Initialize DB
initdb

sleep 1
#Start folder
pg_ctl -o "-c unix_socket_directories=$PGSOCKETS" -D $PGDATA -l $folder/logfile start


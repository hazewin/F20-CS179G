#! /bin/bash
folder=/tmp/$(logname)/mydb
PGDATA=$folder/data
PGSOCKETS=$folder/sockets
export PGDATA
export PGSOCKETS

root=$(realpath $(dirname "$0"))
echo $root
root=$(dirname $root)
echo $root
dbname=$(logname)_db
echo "creating db named ... $dbname"
createdb -h localhost $dbname
pg_ctl status

echo "Copying csv files ... "
sleep 1
<<<<<<< HEAD:postgresql/createPostgreDB.sh
cp ../data/*.csv /tmp/$USER/myDB/data/.

echo "Initializing tables .. "
sleep 1
psql -h localhost -p $PGPORT $USER"_DB" < ../sql/create.sql
=======
cp data/*.csv /tmp/$(logname)/mydb/data/

echo "Initializing tables .. "
sleep 1
psql -h localhost $dbname < sql/create.sql
>>>>>>> d021635979fefa4016db6d18cd6288d07046ad16:createPostgreDB.sh

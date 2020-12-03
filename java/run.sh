#! /bin/bash
DBNAME=$(logname)_db
PORT=$PGPORT

# Example: source ./run.sh flightDB 5432 user
java -cp lib/*:bin/ DBproject $DBNAME $PORT $USER

#! /bin/bash
DBNAME=$USER"_DB"
PORT=$PGPORT

# Example: source ./run.sh flightDB 5432 user
java -cp lib/*:bin/ DBproject $DBNAME $PORT $USER

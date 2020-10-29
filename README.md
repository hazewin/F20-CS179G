# F20-CS179G

hnguy154/Hazel Nguyen: 862054134

stsan003/Sandy Tsan: 861299012

Ashly Hernandez:

# Running Scripts

1. Initialize the PSQL environment by running source ./startPostgreSQL.sh
2. Create the database by running source ./createPostgreDB.sh
  - this will copy the csv files to the working directory and data folder of your database
  - create tables from "create.sql" from the sql folder
  - if failed, jump to encounter problems section
  
3. Run the necessary scripts by 

  - psql -h localhost -p $PGPORT $USER"_DB" < script name
  
NOTE: 
- Do not forget to stop the server and shutdown the database by running source ./stopPostgreDB.sh
- Make sure to "pg_ctl status" to make sure the server is properly running
  
# Running Java Interface & DBproject.java

1. Head to java directory
2. Run compile.sh compiles your code in src
3. Run run.sh executes src code with inputs dbname, port, user.

example: run.sh flightDB 7432 hbae003
or source ./run.sh flightDB 5432 user
java -cp lib/*:bin/ DBproject $DBNAME $PORT $USER

# Extra Functionalities

- We added an extra "restart.sh" scripts that the user can easily run instead of typing all three shell scripts out
- We split up the user prompt so that it will be easier for the user to keep track of what they're inputing

# Encounter Problems
If the steps above fail, make sure to type this into the command line.

cp ../data/*.csv /tmp/$USER/myDB/data/.

psql -h localhost -p $PGPORT $USER"_DB" < ../sql/create.sql

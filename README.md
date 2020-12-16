# F20-CS179G

hnguy154/Hazel Nguyen: 862054134

stsan003/Sandy Tsan: 861299012

ahern001/Ashly Hernandez: 862018436

# Deploy Application
1. Start HDFS system in Pseudo-Distributed Mode
- run `bin/hdfs namenode format`
- run `sbin/start-dfs.sh`
2. SSH to Localhost to access HDFS file system
- run `ssh localhost`
- check on namenode http://localhost:9870
3. Initialize the PSQL environment 
- run source `./startPostgreSQL.sh`
4. Create the database
- run source `./createPostgreDB.sh`
5. Run Java Interface
- see #running java interface

# Running Java Interface & DBproject.java

1. Head to java directory
2. Run compile.sh compiles your code in src
3. Run run.sh executes src code with inputs dbname, port, user : `./run.sh $DBNAME $PORT $USER `

 
NOTE: 
- Do not forget to stop the server and shutdown the database by running source `./stopPostgreDB.sh`
- Make sure to `pg_ctl status` to make sure the server is properly running
- Do not forget to stop the hadoop file system `sbin/stop-dfs.sh`
- Run `jps` after starting HDFS to make sure namenode, datanode, and secondary node is running properly


# Resources
Hadoop (Single Node Cluster) Setup: https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html#Standalone_Operation

Hadoop HDFS Commands : 
- https://data-flair.training/blogs/top-hadoop-hdfs-commands-tutorial/ 
- https://hadoop.apache.org/docs/r2.4.1/hadoop-project-dist/hadoop-common/FileSystemShell.html



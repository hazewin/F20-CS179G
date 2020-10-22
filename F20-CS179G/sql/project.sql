DROP TABLE User CASCADE;
DROP TABLE Post CASCADE;
DROP TABLE Profile CASCADE;


CREATE TABLE User(
username VARCHAR(64),
email VARCHAR(64),
password CHAR(64),
name VARCHAR(64),
PRIMARY KEY(username),
);


CREATE TABLE Post(
photo_id INTEGER,
num_likes INTEGER,
date CHAR(32),
num_comments INTEGER,
stats INTEGER,
tags VARCHAR(64),
PRIMARY KEY(photo_id),
FOREIGN KEY(username) REFERENCES User(username) ON DELETE NO ACTION),
);

Create table Profile (
username VARCHAR(64) NOT NULL,
Followers VARCHAR(64) NOT NULL,
Following VARCHAR(64) NOT NULL,
FOREIGN KEY(username) REFERENCES User(username));
DROP TABLE User CASCADE;
DROP TABLE Post CASCADE;
DROP TABLE UserProfile CASCADE;

-------------
--SEQUENCES--
-------------

CREATE SEQUENCE userID START WITH 11;
CREATE SEQUENCE post_id START WITH 11;
CREATE SEQUENCE profile_id START WITH 11;

-------------
---DOMAINS---
-------------

CREATE DOMAIN _STATUS CHAR(5) CHECK (value IN ( 'TRUE' , 'FALSE') );

------------
---TABLES---
------------

CREATE TABLE User(
userID INTEGER NOT NULL,
fullname VARCHAR(128) NOT NULL,
username VARCHAR(64) NOT NULL,
email VARCHAR(64) NOT NULL,
user_password CHAR(64) NOT NULL,
PRIMARY KEY(username)
);

---------------
---RELATIONS---
---------------

CREATE TABLE Post(
post_id INTEGER NOT NULL,
username_id VARCHAR(64) NOT NULL,
likes INTEGER NOT NULL,
date_posted DATE NOT NULL,
num_comments INTEGER NOT NULL,
tags VARCHAR(64) NOT NULL,
photo_url VARCHAR(128) NOT NULL,
PRIMARY KEY(photo_id),
FOREIGN KEY(username_id) REFERENCES User(username)
);

CREATE TABLE UserProfile(
profile_id INTEGER NOT NULL,
username_id VARCHAR(64) NOT NULL,
num_posts INTEGER NOT NULL,
followers INTEGER NOT NULL,
followings INTEGER NOT NULL,
follow_status _STATUS,
FOREIGN KEY(username_id) REFERENCES User(username)
);


----------------------------
-- INSERT DATA STATEMENTS --
----------------------------

COPY User (
	userID,
    fullname,
    username,
    email,
    user_password
)
FROM 'users.csv'
WITH DELIMITER ',';

COPY Post (
	post_id,
	username_id,
    likes,
    date_posted,
    num_comments,
    tags,
    photo_url
)
FROM 'posts.csv'
WITH DELIMITER ',';

COPY UserProfile (
	profile_id,
	username_id,
    num_posts,
    followers,
    followings,
    follow_status
)
FROM 'profiles.csv'
WITH DELIMITER ',';

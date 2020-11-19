DROP TABLE IF EXISTS DBUsers CASCADE;
DROP TABLE IF EXISTS Post CASCADE;
DROP TABLE IF EXISTS UserProfile CASCADE;
DROP TABLE IF EXISTS UserFollowing CASCADE;

-------------
--SEQUENCES--
-------------
CREATE SEQUENCE userID START WITH 11;
CREATE SEQUENCE post_id START WITH 11;
CREATE SEQUENCE profile_id START WITH 11;
-------------
---DOMAINS---
-------------

------------
---TABLES---
------------

CREATE TABLE DBUsers(
userID INTEGER NOT NULL,
fullname VARCHAR(128) NOT NULL,
username VARCHAR(64) NOT NULL,
email VARCHAR(64) NOT NULL,
user_password VARCHAR(64) NOT NULL,
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
PRIMARY KEY(post_id),
FOREIGN KEY(username_id) REFERENCES DBUsers(username)
);

CREATE TABLE UserProfile(
profile_id INTEGER NOT NULL,
username_id VARCHAR(64) NOT NULL,
num_posts INTEGER NOT NULL,
followers INTEGER NOT NULL,
followings INTEGER NOT NULL,
follow_status VARCHAR(5) NOT NULL,
FOREIGN KEY(username_id) REFERENCES DBUsers(username)
);

CREATE TABLE UserFollowing (
  username_id VARCHAR(64) REFERENCES DBUsers(username),
  follower VARCHAR(64) REFERENCES DBUsers(username),
  PRIMARY KEY (username_id, follower)
);

CREATE INDEX follower_idx ON UserFollowing(follower);

----------------------------
-- INSERT DATA STATEMENTS --
----------------------------

COPY DBUsers (
	userID,
    fullname,
    username,
    email,
    user_password
)
FROM 'users.csv'
WITH DELIMITER ','
CSV HEADER;

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
WITH DELIMITER ','
CSV HEADER;

COPY UserProfile (
	profile_id,
	username_id,
    num_posts,
    followers,
    followings,
    follow_status
)
FROM 'profiles.csv'
WITH DELIMITER ','
CSV HEADER;

COPY UserFollowing (
	username_id,
    follower
)
FROM 'followings.csv'
WITH DELIMITER ','
CSV HEADER;

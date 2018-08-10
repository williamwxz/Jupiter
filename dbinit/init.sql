DROP DATABASE IF EXISTS laiproject;
CREATE DATABASE laiproject;
USE laiproject;
CREATE TABLE items (item_id VARCHAR(255) NOT NULL, name VARCHAR(255), rating FLOAT, address VARCHAR(255), image_url VARCHAR(255), url VARCHAR(255), distance FLOAT, PRIMARY KEY ( item_id));
CREATE TABLE categories (item_id VARCHAR(255) NOT NULL, category VARCHAR(255) NOT NULL, PRIMARY KEY ( item_id, category), FOREIGN KEY (item_id) REFERENCES items(item_id));    
CREATE TABLE users (user_id VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, first_name VARCHAR(255), last_name VARCHAR(255), PRIMARY KEY ( user_id ));
CREATE TABLE history (user_id VARCHAR(255) NOT NULL, item_id VARCHAR(255) NOT NULL, last_favor_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (user_id, item_id), FOREIGN KEY (item_id) REFERENCES items(item_id), FOREIGN KEY (user_id) REFERENCES users(user_id));
INSERT INTO users VALUES ("1111", "2222", "John", "Smith");

DROP TABLE people IF EXISTS;

CREATE TABLE people  (
                         person_id BIGINT auto_increment NOT NULL PRIMARY KEY,
                         first_name VARCHAR(20),
                         last_name VARCHAR(20)
);
# --- !Ups

create table CLIENT (
  ID VARCHAR NOT NULL PRIMARY KEY,
  SECRET VARCHAR NULL,
  REDIRECT_URI VARCHAR NULL,
  SCOPE VARCHAR NULL,
  GRANT_TYPE VARCHAR NOT NULL
);

INSERT INTO CLIENT (ID, SECRET, REDIRECT_URI, SCOPE, GRANT_TYPE) VALUES ('client1', 'secret1', 'http://localhost:9000/', '', 'authorization_code');

CREATE TABLE USER (
  ID INTEGER NOT NULL PRIMARY KEY,
  NAME VARCHAR NOT NULL,
  PASSWORD VARCHAR NOT NULL
);

INSERT INTO USER (ID, NAME, PASSWORD) VALUES (1, 'doug', 'password');

CREATE TABLE AUTH_CODES (
  AUTHORIZATION_CODE VARCHAR NOT NULL PRIMARY KEY,
  USER_ID INTEGER NOT NULL REFERENCES USER,
  REDIRECT_URI VARCHAR NULL,
  CREATED_AT DATE NOT NULL,
  SCOPE VARCHAR NULL,
  CLIENT_ID VARCHAR NULL,
  EXPIRES_IN INTEGER NOT NULL
);

INSERT INTO AUTH_CODES (AUTHORIZATION_CODE, USER_ID, REDIRECT_URI, CREATED_AT, SCOPE, CLIENT_ID, EXPIRES_IN) VALUES ('doug_code', 1, 'http://localhost:9000/', SYSDATE(), '', 'client1', 100000);

CREATE TABLE ACCESS_TOKEN(
  ACCESS_TOKEN VARCHAR NOT NULL PRIMARY KEY,
  REFRESH_TOKEN VARCHAR NULL,
  USER_ID INTEGER NOT NULL REFERENCES USER,
  SCOPE VARCHAR NULL,
  EXPIRES_IN INTEGER NOT NULL,
  CREATED_AT DATE NOT NULL,
  CLIENT_ID VARCHAR NULL
);

# --- !Downs

DROP TABLE CLIENT;
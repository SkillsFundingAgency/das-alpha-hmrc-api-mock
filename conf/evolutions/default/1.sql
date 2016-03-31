# --- !Ups

CREATE TABLE "levy_declaration" (
  "year"   INTEGER NOT NULL,
  "month"  INTEGER NOT NULL,
  "amount" NUMERIC NOT NULL,
  "empref" VARCHAR NOT NULL,
  PRIMARY KEY ("year", "month", "empref")
);

CREATE TABLE "gateway_id_scheme" (
  "id"     VARCHAR NOT NULL,
  "empref" VARCHAR NOT NULL,
  PRIMARY KEY ("id", "empref")
);

CREATE TABLE "access_token" (
  "client_id"    VARCHAR NOT NULL,
  "scope"        VARCHAR NOT NULL,
  "gateway_id"   VARCHAR NOT NULL,
  "access_token" VARCHAR NOT NULL,
  "expires_at"   BIGINT  NOT NULL,
  "created_at"   BIGINT  NOT NULL,
  PRIMARY KEY ("client_id", "scope")
);

INSERT INTO "levy_declaration" ("year", "month", "amount", "empref") VALUES (2016, 2, 1000, '123/AB12345');
INSERT INTO "levy_declaration" ("year", "month", "amount", "empref") VALUES (2016, 1, 500, '123/AB12345');
INSERT INTO "levy_declaration" ("year", "month", "amount", "empref") VALUES (2015, 12, 600, '123/AB12345');

# --- !Downs

DROP TABLE "gateway_id_scheme";
DROP TABLE "levy_declaration";
DROP TABLE "access_token";

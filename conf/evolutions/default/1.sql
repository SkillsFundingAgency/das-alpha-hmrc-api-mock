# --- !Ups

create table "scheme" (
    "empref" VARCHAR NOT NULL PRIMARY KEY,
    "termination_date" BIGINT NULL
);

INSERT INTO "scheme" ("empref") VALUES ('123/AB12345');
INSERT INTO "scheme" ("empref") VALUES ('123/BC12345');
INSERT INTO "scheme" ("empref") VALUES ('321/ZX54321');
INSERT INTO "scheme" ("empref") VALUES ('222/MM22222');

CREATE TABLE "levy_declaration"(
  "year" INTEGER NOT NULL,
  "month" INTEGER NOT NULL,
  "amount" NUMERIC NOT NULL,
  "empref" VARCHAR NOT NULL,
  PRIMARY KEY ("year", "month", "empref"),
  FOREIGN KEY ("empref") REFERENCES "scheme"
);

-- noinspection SqlNoDataSourceInspection
CREATE TABLE "access_token" (
  "access_token" VARCHAR NOT NULL PRIMARY KEY,
  "scope" VARCHAR NOT NULL,
  "expires_at" BIGINT NOT NULL,
  "created_at" BIGINT NOT NULL
);

INSERT INTO "levy_declaration" ("year", "month", "amount", "empref") VALUES (2016, 2, 1000, '123/AB12345');
INSERT INTO "levy_declaration" ("year", "month", "amount", "empref") VALUES (2016, 1, 500, '123/AB12345');
INSERT INTO "levy_declaration" ("year", "month", "amount", "empref") VALUES (2015, 12, 600, '123/AB12345');

# --- !Downs

DROP TABLE "levy_declaration";
DROP TABLE "scheme";
DROP TABLE "access_token";
